package org.anonymous.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.validators.BoardDataValidator;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final Utils utils;

    private final BoardDataValidator boardDataValidator;

    // 게시판 설정 단일 조회
    @GetMapping("/info/{bid}")
    public JSONData configView(@PathVariable("bid") String bid) {

        return new JSONData();
    }

    // 게시글 작성 & 수정
    @PostMapping("/save")
    public JSONData save(@Valid@RequestBody RequestBoardData form, Errors errors) {

        String mode = form.getMode();

        mode = StringUtils.hasText(mode) ? mode : "write";

        commonProcess(form.getBid(), mode);

        boardDataValidator.validate(form, errors);

        if (errors.hasErrors()) {

            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        return new JSONData();
    }

    // 게시글 단일 조회
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        commonProcess(seq, "view");

        return new JSONData();
    }

    // 게시글 목록 조회
    @GetMapping("/list/{bid}")
    public JSONData list(@PathVariable("bid") String bid) {

        commonProcess(bid, "list");

        return new JSONData();
    }
    
    // 게시글 상태 단일 | 목록 일괄 수정
    // SECRET || BLOCK
    @PatchMapping("/status")
    public JSONData status(@RequestParam("seq") List<String> seqs) {
        
        return new JSONData();
    }
    
    // 게시글 조회수 반영 처리
    @GetMapping("/viewcount")
    public JSONData viewCount() {
        
        return new JSONData();
    }


    private void commonProcess(Long seq, String mode) {


    }

    // Base Method
    private void commonProcess(String bid, String mode) {


    }
}