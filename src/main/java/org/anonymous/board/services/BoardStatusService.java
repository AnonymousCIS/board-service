package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.global.exceptions.BadRequestException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardStatusService {

    private final BoardDataRepository boardDataRepository;

    private final CommentDataRepository commentDataRepository;

    /**
     * 게시글 & 댓글 상태 단일 변경
     *
     * Base Method
     *
     * @param seq
     * @return
     */
    public Object process(Long seq, BoardStatus status, String mode) {

        if (!StringUtils.hasText(mode)) throw new BadRequestException();

        Object data = null;

        if (mode.equals("board")) {

            // 존재하지 않는 게시글일 경우 예외 발생
            BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

            boardData.setBoardStatus(status);

            boardDataRepository.saveAndFlush(boardData);

            data = boardData;

        } else if (mode.equals("comment")) {

            CommentData commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

            commentData.setBoardStatus(status);

            commentDataRepository.saveAndFlush(commentData);

            data = commentData;
        }

        // 여기서주면되나?

        return data;
    }

    /**
     * 게시글 & 댓글 상태 목록 변경
     *
     * @param seqs
     * @return
     */
    public List<Object> process(List<Long> seqs, BoardStatus status, String mode) {

        List<Object> processed = new ArrayList<>();

        for (Long seq : seqs) {

            if (mode.equals("board")) {

                Object data = process(seq, status, mode);

                if (data != null) processed.add(data);

            } else if (mode.equals("comment")) {

                Object data = process(seq, status, mode);

                if (data != null) processed.add(data);
            }
        }
        return processed;
    }
}