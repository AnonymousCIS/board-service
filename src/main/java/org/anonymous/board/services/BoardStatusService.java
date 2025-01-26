package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardStatusService {

    private final BoardDataRepository boardDataRepository;

    /**
     * 게시글 상태 단일 변경
     *
     * Base Method
     *
     * @param seq
     * @return
     */
    public BoardData process(Long seq, BoardStatus status) {

        // 존재하지 않는 게시글일 경우 예외 발생
        BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        if (status.equals(BoardStatus.ALL)) {

            boardData.setBoardStatus(BoardStatus.ALL);

        } else if (status.equals(BoardStatus.SECRET)) {

            boardData.setBoardStatus(BoardStatus.SECRET);

        } else if (status.equals(BoardStatus.BLOCK)) {

            boardData.setBoardStatus(BoardStatus.BLOCK);
        }

        if (boardData != null) {

            boardDataRepository.save(boardData);
            boardDataRepository.flush();
        }

        return boardData;
    }

    /**
     * 게시글 상태 목록 변경
     *
     * @param seqs
     * @return
     */
    public List<BoardData> process(List<Long> seqs, BoardStatus status) {

        List<BoardData> boardDataList = new ArrayList<>();

        for (Long seq : seqs) {

            BoardData boardData = process(seq, status);

            if (boardData != null) {

                boardDataList.add(boardData);
            }
        }

        return boardDataList;
    }
}