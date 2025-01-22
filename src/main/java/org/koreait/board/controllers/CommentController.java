package org.koreait.board.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    // 댓글 작성 & 수정
    @PostMapping("/save")
    public JSONData save() {

        return new JSONData();
    }

    // 댓글 단일 조회
    // seq : 댓글 seq
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        return new JSONData();
    }

    // 댓글 목록 조회
    // seq : 게시글(BoardData) seq
    @GetMapping("/list/{seq}")
    public JSONData list(@PathVariable("seq") Long seq) {

        return new JSONData();
    }

    // 댓글 상태 단일 | 목록 일괄 수정
    // SECRET || BLOCK
    @PatchMapping("/status")
    public JSONData status() {

        return new JSONData();
    }
}