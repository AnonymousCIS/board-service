package org.anonymous.board.services.comment;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.board.controllers.CommentSearch;
import org.anonymous.board.controllers.RequestComment;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.entities.QCommentData;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.paging.ListData;
import org.anonymous.global.paging.Pagination;
import org.anonymous.member.Member;
import org.anonymous.member.MemberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 단일 조회 & 목록 조회
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class CommentInfoService {

    private final CommentDataRepository commentDataRepository;

    private final JPAQueryFactory queryFactory;

    private final HttpServletRequest request;

    private final ModelMapper modelMapper;

    private final MemberUtil memberUtil;

    private final Utils utils;

    /**
     * 댓글 단일 조회
     *
     * @param seq
     * @return
     */
    public CommentData get(Long seq) {

        CommentData item = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

        // if (item.getDomainStatus().equals(DomainStatus.BLOCK) && !memberUtil.isAdmin()) throw new UnAuthorizedException();

        // if (item.getDomainStatus().equals(DomainStatus.SECRET) && !memberUtil.isAdmin() && !item.isMine()) throw new UnAuthorizedException();

        if (item.getDeletedAt() != null && !memberUtil.isAdmin()) throw new BadRequestException();

        return item;
    }

    /**
     * 수정시 사용할 커맨드 객체
     *
     * @param seq
     * @return
     */
    public RequestComment getForm(Long seq) {

        CommentData item = get(seq);

        BoardData data = item.getData();

        RequestComment form = modelMapper.map(item, RequestComment.class);

        form.setMode("edit");
        form.setBoardDataSeq(data.getSeq());

        return form;
    }

    /**
     * 게시글 번호로 작성된 댓글 목록 조회
     *
     * @param seq
     * @return
     */
    public List<CommentData> getList(Long seq) {

        BooleanBuilder andBuilder = new BooleanBuilder();

        QCommentData commentData = QCommentData.commentData;

        /*
        // 관리자가 아닐 경우 비밀글, 차단글을 조회 목록에서 제외
        if (!memberUtil.isAdmin()) {

            // ne = eq 반대, Not Equal
            // dsl 문은 ! 사용 불가

            // 비밀 댓글일 경우
            andBuilder.and(commentData.domainStatus.ne(DomainStatus.SECRET)
                    .or(commentData.createdBy.eq(memberUtil.getMember().getEmail())));

            // 관리자 차단 댓글일 경우
            andBuilder.and(commentData.domainStatus.ne(DomainStatus.BLOCK));
        }
         */

        // 관리자가 아닐 경우 유저삭제된 댓글은 제외하고 조회
        if (!memberUtil.isAdmin())andBuilder.and(commentData.deletedAt.isNull());

        // 댓글의 부모인 게시글의 등록번호(seq)로 조건
        andBuilder.and(commentData.data.seq.eq(seq));

        List<CommentData> items = queryFactory.selectFrom(commentData)
                .where(andBuilder)
                .orderBy(commentData.createdAt.asc())
                .fetch();

        items.forEach(this::addInfo);

        return items;
    }

    /**
     * 댓글 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<CommentData> getList(CommentSearch search) {

        int page = Math.max(search.getPage(), 1);

        // Config config = null;

        // 임시 🍬🍬🍬
        int rowPerPage = 5;

        List<Long> seqs = search.getSeq();

        int limit = search.getLimit() > 0 ? search.getLimit() : rowPerPage;

        // QueryDSL 사용시 필수 - Page 시작 위치
        int offset = (page - 1) * limit;

        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();

        QCommentData commentData = QCommentData.commentData;

        // 게시글 ID (seq) 검색, 모아보기 기능도 있기때문에 List 형태
        if (seqs != null && !seqs.isEmpty()) {

            andBuilder.and(commentData.data.seq.in(seqs));
        }

        // 상태별 검색 - 관리자용
        List<DomainStatus> statuses = search.getStatus();

        if (statuses != null && !statuses.isEmpty()) {

            andBuilder.and(commentData.domainStatus.in(statuses));
        }

        // 관리자가 아닐 경우 비밀글, 차단글을 조회 목록에서 제외
        if (!memberUtil.isAdmin()) {

            // ne = eq 반대, Not Equal
            // dsl 문은 ! 사용 불가

            // 비밀 게시글일 경우
            andBuilder.and(commentData.domainStatus.ne(DomainStatus.SECRET)
                    .or(commentData.createdBy.eq(memberUtil.getMember().getEmail())));

            // 관리자 차단 게시글일 경우
            andBuilder.and(commentData.domainStatus.ne(DomainStatus.BLOCK));
        }

        /**
         * 키워드 검색
         *
         * - sopt
         * ALL : 내용 + 작성자(작성자 + 이메일 + 닉네임)
         * CONTENT : 내용
         * COMMENTER : 작성자 + 이메일 + 닉네임
         */
        String sopt = search.getSopt();
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";

        if (StringUtils.hasText(skey)) {

            skey = skey.trim();

            StringExpression content = commentData.content;

            StringExpression commenter = commentData.commenter;

            StringExpression condition = null;

            if (sopt.equals("CONTENT")) { // 내용 검색

                condition = content;

            } else if (sopt.equals("COMMENTER")) { // 작성자 검색

                condition = commenter;

            } else { // 통합 검색

                condition = content.concat(commenter);
            }

            andBuilder.and(condition.contains(skey));
        }

        // 회원 이메일로 검색
        // OneToMany 안쓰는 이유 : Page 때문.. 생각보다 OneToMany 는 자주 쓰이지 않음
        List<String> emails = search.getEmail();

        if (emails != null && !emails.isEmpty()) {

            andBuilder.and(commentData.createdBy.in(emails));
        }
        /* 검색 처리 E */

        JPAQuery<CommentData> query = queryFactory.selectFrom(commentData)
                .leftJoin(commentData.data)
                .fetchJoin()
                .where(andBuilder)
                .offset(offset)
                .limit(limit);

        /* 정렬 조건 처리 S */
        String sort = search.getSort();

        if (StringUtils.hasText(sort)) {

            // 0번째 : 필드명, 1번째 : 정렬 방향
            String[] _sort = sort.split("_");

            String field = _sort[0];

            String direction = _sort[1];

            if (field.equals("deletedAt")) { // 유저가 삭제 최신순 정렬

                query.orderBy(commentData.deletedAt.desc());

            } else { // 기본 정렬 조건 최신순

                query.orderBy(commentData.createdAt.desc());
            }

        } else { // 기본 정렬 조건 최신순

            query.orderBy(commentData.createdAt.desc());
        }
        /* 정렬 조건 처리 E */

        List<CommentData> items = query.fetch();

        long total = commentDataRepository.count(andBuilder);

        items.forEach(this::addInfo);

        // 게시판 설정이 없는 경우
        int ranges = utils.isMobile() ? 5 : 10;

        /*
        // 게시판 설정이 있는 경우
        if (config != null) {

            ranges = utils.isMobile() ? config.getPageRangesMobile() : config.getPageRanges();
        }
         */

        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 추가 데이터 처리
     *
     * @param item
     */
    private void addInfo(CommentData item) {

        Member member = memberUtil.getMember();

        String createdBy = item.getCreatedBy();

        boolean editable = memberUtil.isAdmin() || createdBy == null || (memberUtil.isLogin() && member.getEmail().equals(createdBy));

        // 댓글 수정 & 삭제 가능
        // 단 비회원은 비밀번호 검증 페이지로 넘어감
        item.setEditable(editable);

        if (item.getDomainStatus() == DomainStatus.BLOCK && !memberUtil.isAdmin()) {
            item.setContent("차단된 댓글");
        }

        if (item.getDomainStatus() == DomainStatus.SECRET && !memberUtil.isAdmin() && !item.isMine()) {
            item.setContent("비밀 댓글");
        }
    }
}