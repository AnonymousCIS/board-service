package org.anonymous.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.validators.BoardConfigValidator;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminBoardController {

    private final Utils utils;

    private final BoardConfigValidator configValidator;

    // 게시판 설정 등록 & 수정
    @PostMapping("/config/save")
    public JSONData save(@Valid @RequestBody RequestConfig form, Errors errors) {

        configValidator.validate(form, errors);

        if (errors.hasErrors()) {

            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        return new JSONData();
    }

    // 게시판 설정 목록 조회
    @GetMapping("/list")
    public JSONData list(@ModelAttribute BoardConfigSearch search) {

        return new JSONData();
    }

    // 게시판 단일 | 목록 일괄 수정
    @PatchMapping("/config/update")
    public JSONData update(@RequestBody List<RequestConfig> form) {

        return new JSONData();
    }

    // 게시판 단일 | 목록 일괄 삭제
    @DeleteMapping("/config/deletes")
    public JSONData configDeletes(@RequestParam("bid") List<String> bids) {

        return new JSONData();
    }

    // 게시글 단일 | 목록 일괄 삭제
    @DeleteMapping("/deletes")
    public JSONData deletes() {

        return new JSONData();
    }
    
    // 댓글 단일 | 목록 일괄 삭제
    @DeleteMapping("/comment/deletes")
    public JSONData commentDeletes(@RequestParam("seq") List<Long> seqs) {
        
        return new JSONData();
    }
}
