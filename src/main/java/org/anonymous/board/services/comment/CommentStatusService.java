//package org.anonymous.board.services.comment;
//
//import lombok.RequiredArgsConstructor;
//import org.anonymous.board.constants.BoardStatus;
//import org.anonymous.board.entities.CommentData;
//import org.anonymous.board.exceptions.CommentNotFoundException;
//import org.anonymous.board.repositories.CommentDataRepository;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Lazy
//@Service
//@RequiredArgsConstructor
//public class CommentStatusService {
//
//    private final CommentDataRepository commentDataRepository;
//
//    /**
//     * 댓글 상태 단일 변경
//     *
//     * Base Method
//     *
//     * @param seq
//     * @return
//     */
//    public CommentData process(Long seq, BoardStatus status) {
//
//        // 존재하지 않는 댓일 경우 예외 발생
//        CommentData commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);
//
//        if (status.equals(BoardStatus.ALL)) {
//
//            commentData.setBoardStatus(BoardStatus.ALL);
//
//        } else if (status.equals(BoardStatus.SECRET)) {
//
//            commentData.setBoardStatus(BoardStatus.SECRET);
//
//        } else if (status.equals(BoardStatus.BLOCK)) {
//
//            commentData.setBoardStatus(BoardStatus.BLOCK);
//        }
//
//        if (commentData != null) {
//
//            commentDataRepository.save(commentData);
//            commentDataRepository.flush();
//        }
//
//        return commentData;
//    }
//
//    /**
//     * 게시글 상태 목록 변경
//     *
//     * @param seqs
//     * @return
//     */
//    public List<CommentData> process(List<Long> seqs, BoardStatus status) {
//
//        List<CommentData> processed = new ArrayList<>();
//
//        for (Long seq : seqs) {
//
//            CommentData commentData = process(seq, status);
//
//            if (commentData != null) {
//
//                processed.add(commentData);
//            }
//        }
//
//        return processed;
//    }
//}