package org.anonymous.board.services.comment;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.CommentData;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class CommentStatusService {

    public List<CommentData> process(List<CommentData> items) {

        return null;
    }
}