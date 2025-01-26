package org.anonymous.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.Config;
import org.anonymous.board.services.BoardDeleteService;
import org.anonymous.board.services.comment.CommentDeleteService;
import org.anonymous.board.services.configs.BoardConfigDeleteService;
import org.anonymous.board.services.configs.BoardConfigInfoService;
import org.anonymous.board.services.configs.BoardConfigUpdateService;
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

    private final BoardConfigInfoService infoService;

    private final BoardConfigValidator configValidator;

    private final BoardConfigUpdateService updateService;

    private final BoardConfigDeleteService ConfigDeleteService;

    private final BoardDeleteService boardDeleteService;

    private final CommentDeleteService commentDeleteService;

    /**
     * 게시판 설정 등록 & 수정 처리
     *
     * @param form
     * @param errors
     * @return
     */
    @PostMapping("/config/save")
    public JSONData save(@Valid @RequestBody RequestConfig form, Errors errors) {

        configValidator.validate(form, errors);

        if (errors.hasErrors()) {

            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        Config config = updateService.process(form);

        return new JSONData();
    }

    /**
     * 게시판 설정 목록 조회
     *
     * @param search
     * @return
     */
    @GetMapping("/config/list")
    public JSONData list(@ModelAttribute BoardConfigSearch search) {

        return new JSONData();
    }

    /**
     * 게시판 단일 | 목록 일괄 수정
     *
     * @param form
     * @return
     */
    @PatchMapping("/config/update")
    public JSONData update(@RequestBody List<RequestConfig> form) {

        List<Config> items = updateService.process(form);

        return new JSONData(items);
    }

    /**
     * 게시판 단일 | 목록 일괄 삭제 처리
     *
     * @param bids
     * @return
     */
    @DeleteMapping("/config/deletes")
    public JSONData configDeletes(@RequestParam("bid") List<String> bids) {

        List<Config> items = ConfigDeleteService.process(bids);

        return new JSONData(items);
    }

    /**
     * 게시글 단일 | 목록 일괄 삭제
     *
     * @return
     */
    @DeleteMapping("/deletes")
    public JSONData deletes() {

        return new JSONData();
    }

    /**
     * 댓글 단일 | 목록 일괄 삭제
     *
     * @param seqs
     * @return
     */
    @DeleteMapping("/comment/deletes")
    public JSONData commentDeletes(@RequestParam("seq") List<Long> seqs) {
        
        return new JSONData();
    }
}
