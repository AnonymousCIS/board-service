package org.koreait.board.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    // 게시글 설정 단일 조회
    @GetMapping("/info/{seq}")
    public JSONData configView(@PathVariable("seq") Long seq) {

        return new JSONData();
    }

    // 게시글 작성 & 수정
    @PostMapping("/save")
    public JSONData save() {

        return new JSONData();
    }

    // 게시글 단일 조회
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        return new JSONData();
    }

    // 게시글 목록 조회
    @GetMapping("/list/{bid}")
    public JSONData list(@PathVariable("bid") String bid) {

        return new JSONData();
    }
    
    // 게시글 상태 단일 | 목록 일괄 수정
    // SECRET || BLOCK
    @PatchMapping("/status")
    public JSONData status() {
        
        return new JSONData();
    }
    
    // 게시글 조회수 반영 처리
    @PatchMapping("/viewcount")
    public JSONData viewCount() {
        
        return new JSONData();
    }
}