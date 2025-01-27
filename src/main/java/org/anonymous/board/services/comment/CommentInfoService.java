package org.anonymous.board.services.comment;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.controllers.RequestComment;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.entities.QCommentData;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.global.exceptions.UnAuthorizedException;
import org.anonymous.member.Member;
import org.anonymous.member.MemberUtil;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

    private final ModelMapper modelMapper;

    private final MemberUtil memberUtil;

    /**
     * 댓글 단일 조회
     *
     * @param seq
     * @return
     */
    public CommentData get(Long seq) {

        CommentData item = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

        if (item.getBoardStatus().equals(BoardStatus.BLOCK) && !memberUtil.isAdmin()) throw new UnAuthorizedException();

        if (item.getBoardStatus().equals(BoardStatus.SECRET) && !memberUtil.isAdmin() && !item.isMine()) throw new UnAuthorizedException();

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

        // 관리자가 아닐 경우 차단 댓글을 조회 목록에서 제외
        if (!memberUtil.isAdmin()) {

            andBuilder.and(commentData.boardStatus.ne(BoardStatus.BLOCK));
        }

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
    }
}