package org.anonymous.board.services.comment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestComment;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.entities.QCommentData;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.board.services.BoardInfoService;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentUpdateService {

    private final CommentDataRepository commentDataRepository;

    private final BoardDataRepository boardDataRepository;

    private final BoardInfoService boardInfoService;

    private final PasswordEncoder passwordEncoder;

    private final HttpServletRequest request;

    private final MemberUtil memberUtil;

    /**
     * 댓글 등록 & 수정
     *
     * @param form
     * @return
     */
    public CommentData save(RequestComment form) {

        String mode = Objects.requireNonNullElse(form.getMode(), "write");

        Long seq = form.getSeq();

        // 게시글 번호
        Long boardDataSeq = form.getBoardDataSeq();

        CommentData item = null;

        // 댓글 수정
        if (mode.equals("edit") && seq != null & seq > 0L) {

            item = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

        } else { // 댓글 등록
            // 바뀌지 않는 데이터들은 최초 등록시에만 Set

            // 게시글 데이터
            BoardData data = boardInfoService.get(boardDataSeq);

            item = new CommentData();

            item.setData(data);

            item.setIpAddr(request.getRemoteAddr());

            item.setUserAgent(request.getHeader("User-Agent"));
        }

        item.setCommenter(form.getCommenter());

        item.setContent(form.getContent());

        String guestPw = form.getGuestPw();

        if (StringUtils.hasText(guestPw)) {

            item.setGuestPw(passwordEncoder.encode(guestPw));
        }

        commentDataRepository.saveAndFlush(item);

        // 댓글 개수 업데이트
        updateCount(boardDataSeq);

        return item;
    }

    /**
     * 게시글 번호로 총 댓글 개수 반영
     *
     * @param seq
     */
    public void updateCount(Long seq) {

        QCommentData commentData = QCommentData.commentData;

        // 게시글별 댓글 개수
        long total = commentDataRepository.count(commentData.data.seq.eq(seq));

        BoardData item = boardDataRepository.findById(seq).orElse(null);

        if (item != null) {

            item.setCommentCount(total);

            boardDataRepository.saveAndFlush(item);
        }
    }
}