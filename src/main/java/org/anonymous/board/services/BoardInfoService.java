package org.anonymous.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.board.controllers.BoardSearch;
import org.anonymous.board.controllers.RequestBoardData;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.Config;
import org.anonymous.board.entities.QBoardData;
import org.anonymous.board.entities.QCommentData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.services.configs.BoardConfigInfoService;
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

@Lazy
@Service
@RequiredArgsConstructor
public class BoardInfoService {

    private final BoardConfigInfoService configInfoService;

    private final BoardDataRepository boardDataRepository;

    private final JPAQueryFactory queryFactory;

    private final HttpServletRequest request;

    private final ModelMapper modelMapper;

    private final MemberUtil memberUtil;

    private final Utils utils;

    /**
     * 게시글 단일 조회
     *
     * @param seq
     * @return
     */
    public BoardData get(Long seq) {

        BoardData item = boardDataRepository.findBySeq(seq).orElseThrow(BoardDataNotFoundException::new);

        // if (item.getDomainStatus().equals(DomainStatus.BLOCK) && !memberUtil.isAdmin()) throw new UnAuthorizedException();

        // if (item.getDomainStatus().equals(DomainStatus.SECRET) && !memberUtil.isAdmin() && !item.isMine()) throw new UnAuthorizedException();

        addInfo(item, true);

        return item;
    }

    /**
     * 게시글 수정시 필요한 커맨드 객체 RequestBoard 로 변환해 반환
     *
     * @param seq
     * @return
     */
    public RequestBoardData getForm(Long seq) {

        return getForm(get(seq));
    }

    /**
     * 게시글 수정시 필요한 커맨드 객체 RequestBoard 로 변환해 반환
     *
     * Base Method
     *
     * @param item
     * @return
     */
    public RequestBoardData getForm(BoardData item) {

        RequestBoardData form = modelMapper.map(item, RequestBoardData.class);

        form.setMode("edit");
        form.setBid(item.getConfig().getBid());

        return form;
    }

    /**
     * 게시글 목록 조회
     *
     * Base Method
     *
     * @param search
     * @return
     */
    public ListData<BoardData> getList(BoardSearch search) {

        int page = Math.max(search.getPage(), 1);

        Config config = null;

        int rowsPerPage = 0;

        List<String> bids = search.getBid();

        if (bids != null && !bids.isEmpty()) {

            // 첫번째 게시판 설정으로
            config = configInfoService.get(bids.get(0));

            rowsPerPage = config.getRowsPerPage();
        }

        int limit = search.getLimit() > 0 ? search.getLimit() : rowsPerPage;

        // QueryDSL 사용시 필수 - Page 시작 위치
        int offset = (page - 1) * limit;

        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();

        QBoardData boardData = QBoardData.boardData;

        QCommentData commentData = QCommentData.commentData;

        // 게시판 ID (bid) 검색, 모아보기 기능도 있기때문에 List 형태
        if (bids != null && !bids.isEmpty()) {

            andBuilder.and(boardData.config.bid.in(bids));
        }

        // 분류 검색
        List<String> categories = search.getCategory();

        if (categories != null && !categories.isEmpty()) {

            andBuilder.and(boardData.category.in(categories));
        }

        // 상태별 검색 - 관리자용
        List<DomainStatus> statuses = search.getStatus();

        if (statuses != null && !statuses.isEmpty()) {

            andBuilder.and(boardData.domainStatus.in(statuses));
        }

        /*
        // 관리자가 아닐 경우 비밀글, 차단글을 조회 목록에서 제외
        if (!memberUtil.isAdmin()) {

            // ne = eq 반대, Not Equal
            // dsl 문은 ! 사용 불가

            // 비밀 게시글일 경우
           andBuilder.and(boardData.domainStatus.ne(DomainStatus.SECRET)
                    .or(boardData.createdBy.eq(memberUtil.getMember().getEmail())));

            // 관리자 차단 게시글일 경우
            andBuilder.and(boardData.domainStatus.ne(DomainStatus.BLOCK));
        }
         */

        /**
         * 키워드 검색
         *
         * - sopt
         * ALL : 제목 + 내용 + 작성자(작성자 + 이메일 + 닉네임)
         * SUBJECT : 제목
         * CONTENT : 내용
         * SUBJECT_CONTENT : 제목 + 내용
         * POSTER : 작성자 + 이메일 + 닉네임
         * COMMENT : 댓글 내용
         */
        String sopt = search.getSopt();
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";

        if (StringUtils.hasText(skey)) {

            skey = skey.trim();

            StringExpression subject = boardData.subject;
            StringExpression content = boardData.content;
            StringExpression poster = boardData.poster.concat(boardData.createdBy);
            StringExpression comment = commentData.content;

            StringExpression condition = null;

            if (sopt.equals("SUBJECT")) { // 제목 검색

                condition = subject;

            } else if (sopt.equals("CONTENT")) { // 내용 검색

                condition = content;

            } else if (sopt.equals("SUBJECT_CONTENT")) { // 제목 + 내용 검색

                condition = subject.concat(content);

            } else if (sopt.equals("POSTER")) { // 작성자 검색

                condition = poster;

            } else if (sopt.equals("COMMENT")) { // 댓글 내용 검색

                condition = comment;
            }

            else { // 통합 검색

                condition = subject.concat(content).concat(poster);
            }

            andBuilder.and(condition.contains(skey));
        }

        // 회원 이메일로 검색
        // OneToMany 안쓰는 이유 : Page 때문.. 생각보다 OneToMany 는 자주 쓰이지 않음
        List<String> emails = search.getEmail();

        if (emails != null && !emails.isEmpty()) {

            andBuilder.and(boardData.createdBy.in(emails));
        }
        /* 검색 처리 E */

        JPAQuery<BoardData> query = queryFactory.selectFrom(boardData)
                .leftJoin(boardData.config)
                .fetchJoin()
                .leftJoin(boardData.comment, commentData)
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

            if (field.equals("viewCount")) { // 조회수 정렬

                query.orderBy(direction.equalsIgnoreCase("DESC")
                        ? boardData.viewCount.desc() : boardData.viewCount.asc());

            } else if (field.equals("commentCount")) { // 댓글수 정렬

                query.orderBy(direction.equalsIgnoreCase("DESC")
                        ? boardData.commentCount.desc() : boardData.commentCount.asc());

            } else if (field.equals("recommendCount")) { // 추천수 정렬

                query.orderBy(direction.equalsIgnoreCase("DESC")
                        ? boardData.recommendCount.desc() : boardData.recommendCount.asc());

            } else if (boardData.domainStatus.equals(DomainStatus.BLOCK) && field.equals("modifiedAt")) {
                // 차단 게시글일경우 차단된 시간순 정렬

                query.orderBy(direction.equalsIgnoreCase("DESC") ?
                        boardData.modifiedAt.desc() : boardData.modifiedAt.asc());

            } else { // 기본 정렬 조건 (1차 정렬 공지사항 최신순, 2차 정렬 최신순)

                query.orderBy(boardData.notice.desc(), boardData.createdAt.desc());
            }
        } else { // 기본 정렬 조건 (1차 정렬 공지사항 최신순, 2차 정렬 최신순)

            query.orderBy(boardData.notice.desc(), boardData.createdAt.desc());
        }
        /* 정렬 조건 처리 E */

        List<BoardData> items = query.fetch();

        long total = boardDataRepository.count(andBuilder);

        items.forEach(this::addInfo);

        // 게시판 설정이 없는 경우
        int ranges = utils.isMobile() ? 5 : 10;

        // 게시판 설정이 있는 경우
        if (config != null) {

            ranges = utils.isMobile() ? config.getPageRangesMobile() : config.getPageRanges();
        }

        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 게시판별 단일 & 목록 조회
     *
     * @param bid
     * @param search
     * @return
     */
    public ListData<BoardData> getList(String bid, BoardSearch search) {

        search.setBid(List.of(bid));

        return getList(search);
    }

    /**
     * 게시판별 최신 게시글 조회
     *
     * 주로 메인 페이지에서 사용
     *
     * Base Method
     *
     * @param bid
     * @param limit
     * @return
     */
    public List<BoardData> getLatest(String bid, String category, int limit) {

        BoardSearch search = new BoardSearch();

        search.setLimit(limit);
        search.setBid(List.of(bid));
        search.setCategory(category == null ? null : List.of(category));

        ListData<BoardData> data = getList(search);

        List<BoardData> items = data.getItems();

        return items == null ? List.of() : items;
    }

    public List<BoardData> getLatest(String bid, int limit) {

        return getLatest(bid, null, limit);
    }

    /**
     * 게시판별 최신 게시글 5개(기본 값) 조회
     *
     * @param bid
     * @return
     */
    public List<BoardData> getLatest(String bid) {

        return getLatest(bid, 5);
    }

    /**
     * 현재 로그인한 회원이 작성한 게시글 목록 조회
     *
     * MyPage 에서 연동
     *
     * @param search
     * @return
     */
    public ListData<BoardData> getMyList(BoardSearch search) {

        // 템플릿 출력시 오류 방지위한 빈 객체
        if (!memberUtil.isLogin()) return new ListData<>(List.of(), null);

        Member member = memberUtil.getMember();

        String email = member.getEmail();

        search.setEmail(List.of(email));

        return getList(search);
    }

    /**
     * 추가 정보 처리
     *
     * @param item
     */
    private void addInfo(BoardData item, boolean isView) {
        /* 이전 & 다음 게시글 S */

        // 게시글 단일 상세조회일 경우에만 이전 & 다음 게시글 조회
        if (isView) {

            QBoardData boardData = QBoardData.boardData;

            Long seq = item.getSeq();

            // lt (seq 보다 작은 것) 중에 가장 최신인 것 1개 조회
            BoardData prev = queryFactory.selectFrom(boardData)
                            .where(boardData.seq.lt(seq))
                            .orderBy(boardData.seq.desc())
                            .fetchFirst();

            // gt (seq 보다 큰 것) 중에서 가장 오래된 것 1개 조회
            BoardData next = queryFactory.selectFrom(boardData)
                            .where(boardData.seq.gt(seq))
                            .orderBy(boardData.seq.asc())
                            .fetchFirst();


            if (prev != null) {
                prev.setPrev(null);
                prev.setNext(null);
            }
            if (next != null) {
                next.setPrev(null);
                next.setNext(null);
            }
            item.setPrev(prev);
            item.setNext(next);
            /* 이전 & 다음 게시글 E */
        }
        /* listable, writable, editable, mine 처리 S */
        Config config = item.getConfig();

        configInfoService.addInfo(config);

        boolean listable = config.isListable();

        boolean writeable = config.isWritable();

        String createdBy = item.getCreatedBy();

        Member loggedMember = memberUtil.getMember();

        // 비회원 게시글 - 비밀번호 확인이 필요하므로 버튼 노출
        // 회원 게시글 - 로그인한 회원과 일치하면 버튼 노출
        boolean editable = createdBy == null

                || (memberUtil.isLogin() && loggedMember.getEmail().equals(createdBy));

        boolean mine = utils.getValue(utils.getUserHash() + "_board_" + item.getSeq()) != null

                || (memberUtil.isLogin() && loggedMember.getEmail().equals(createdBy));

        item.setListable(listable);
        item.setWritable(writeable);
        item.setEditable(editable);
        item.setMine(mine);
        /* listable, writable, editable, mine 처리 E */

        if (item.getDomainStatus() == DomainStatus.BLOCK && !memberUtil.isAdmin()) {
            item.setSubject("차단된 게시글");
        }
        
        if (item.getDomainStatus() == DomainStatus.SECRET && !memberUtil.isAdmin() && !item.isMine()) {
            item.setSubject("비밀글");
        }
    }

    private void addInfo(BoardData item) {

        addInfo(item, false);
    }

    /**
     * 게시글 번호와 게시판 아이디로 현재 페이지 구하기
     *
     * @param seq
     * @param limit
     * @return
     */
    public int getPage(String bid, Long seq, int limit) {
        QBoardData boardData = QBoardData.boardData;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(boardData.config.bid.eq(bid))
                .and(boardData.seq.goe(seq));

        long total = boardDataRepository.count(builder);

        int page = (int)Math.ceil((double)total / limit);

        return page;
    }
}