package org.anonymous.board.services.comment;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.repositories.CommentDataRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentDataRepository commentDataRepository;

    private final CommentUpdateService updateService;

    private final CommentInfoService infoService;

    /**
     * 댓글 삭제
     *
     * @param seq
     * @return
     */
    public CommentData delete(Long seq) {

        CommentData item = infoService.get(seq);

        BoardData data = item.getData();

        commentDataRepository.delete(item);
        commentDataRepository.flush();

        // 댓글 개수 업데이트
        updateService.updateCount(data.getSeq());

        return item;
    }
}