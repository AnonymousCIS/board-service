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
     * @param seq
     * @param status
     * @param mode
     */
    public <T> T process(Long seq, BoardStatus status, String mode) {

        // 잘못된 요청
        if ((!mode.equals("board") && !mode.equals("comment")) || seq == null || status == null) throw new BadRequestException();

        T data = null;

        String type = "";

        if (mode.equals("board")) {

            BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

            boardData.setBoardStatus(status);

            boardDataRepository.saveAndFlush(boardData);

            data = (T) boardData;

            type = "board";

        } else if (mode.equals("comment")) {

            CommentData commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

            commentData.setBoardStatus(status);

            commentDataRepository.saveAndFlush(commentData);

            data = (T) commentData;

            type = "comment";
        }
        // 여기서 Member 도메인한테 email, type(게시글|댓글), seq(해당 게시글, 댓글) 주기

        return data;
    }

    /**
     * 게시글 & 댓글 상태 목록 변경
     *
     * @param seqs
     * @param status
     * @param mode
     */
    public <T> List<T> process(List<Long> seqs, BoardStatus status, String mode) {

        List<T> processed = new ArrayList<>();

        for (Long seq : seqs) {

            T data = process(seq, status, mode);

            if (data != null) processed.add(data);
        }
        return processed;
    }
}

