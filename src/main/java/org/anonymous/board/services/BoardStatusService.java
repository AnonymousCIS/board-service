package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardStatusService {

    public List<BoardData> process(Long seq) {

        return null;
    }
}