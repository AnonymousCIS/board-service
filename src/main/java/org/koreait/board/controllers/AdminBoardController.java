package org.koreait.board.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminBoardController {

    // 게시판 설정 등록 & 수정
    @PostMapping("/config/save")
    public JSONData save() {

        return new JSONData();
    }

    // 게시판 설정 목록 조회
    @GetMapping("/list")
    public JSONData list() {

        return new JSONData();
    }

    // 게시판 단일 | 목록 일괄 수정
    @PatchMapping("/config/update")
    public JSONData update() {

        return new JSONData();
    }

    // 게시판 단일 | 목록 일괄 삭제
    @DeleteMapping("/config/deletes")
    public JSONData configDeletes() {

        return new JSONData();
    }

    // 게시글 단일 | 목록 일괄 삭제
    @DeleteMapping("/deletes")
    public JSONData deletes() {

        return new JSONData();
    }
    
    // 댓글 단일 | 목록 일괄 삭제
    @DeleteMapping("/comment/deletes")
    public JSONData commentDeletes() {
        
        return new JSONData();
    }
}
