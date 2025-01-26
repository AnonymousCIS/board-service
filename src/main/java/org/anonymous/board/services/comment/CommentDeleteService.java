package org.anonymous.board.services.comment;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.CommentDataRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentDataRepository commentDataRepository;

    private final CommentUpdateService updateService;

    private final CommentInfoService infoService;

    /**
     * 댓글 단일 삭제 (일반 사용자)
     *
     * DB 에서 삭제 X
     * 현재 시간으로 DeletedAt 할당
     *
     * Base Method
     *
     * @param seq
     * @return
     */
    public CommentData userDelete(Long seq) {

        CommentData item = infoService.get(seq);

        if (item == null) {

            throw new CommentNotFoundException();
        }

        item.setDeletedAt(LocalDateTime.now());

        commentDataRepository.saveAndFlush(item);

        return item;
    }

    /**
     * 댓글 목록 삭제 (일반 사용자)
     *
     * DB 에서 삭제 X
     * 현재 시간으로 DeletedAt 할당
     *
     * @param seqs
     * @return
     */
    public List<CommentData> userDelete(List<Long> seqs) {
        List<CommentData> userDeleted = new ArrayList<>();

        for (Long seq : seqs) {

            CommentData item = userDelete(seq);

            if (item != null) {

                userDeleted.add(item);
            }
        }
        return userDeleted;
    }

    /**
     * 댓글 단일 삭제
     * DB 에서 삭제
     *
     * 관리자만 가능
     *
     * Base Method
     *
     * @param seq
     * @return
     */
    public CommentData delete(Long seq) {

        CommentData item = infoService.get(seq);

        if (item == null) {

            throw new CommentNotFoundException();
        }

        BoardData data = item.getData();

        commentDataRepository.delete(item);
        commentDataRepository.flush();

        // 댓글 개수 업데이트
        updateService.updateCount(data.getSeq());

        return item;
    }

    /**
     * 댓글 목록 삭제
     *
     * @param seqs
     * @return
     */
    public List<CommentData> delete(List<Long> seqs) {

        List<CommentData> deleted = new ArrayList<>();

        for (Long seq : seqs) {

            CommentData item = delete(seq);

            if (item != null) {

                deleted.add(item);
            }
        }
        return deleted;
    }
}