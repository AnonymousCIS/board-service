package org.anonymous.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.Config;
import org.anonymous.board.services.*;
import org.anonymous.board.services.configs.BoardConfigInfoService;
import org.anonymous.board.validators.BoardDataValidator;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.paging.ListData;
import org.anonymous.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final Utils utils;

    private final BoardAuthService authService;

    private final BoardInfoService infoService;

    private final BoardUpdateService updateService;

    private final BoardDataValidator boardDataValidator;

    private final BoardStatusService statusService;

    private final BoardConfigInfoService configInfoService;

    private final BoardViewUpdateService viewUpdateService;

    /**
     * 게시판 설정 단일 조회
     *
     * @param bid
     * @return
     */
    @GetMapping("/info/{bid}")
    public JSONData configView(@PathVariable("bid") String bid) {

        Config config = configInfoService.get(bid);

        return new JSONData(config);
    }

    /**
     * 게시글 등록 & 수정 처리
     *
     * @param form
     * @param errors
     * @return
     */
    @PostMapping("/save")
    public JSONData save(@Valid@RequestBody RequestBoardData form, Errors errors) {

        String mode = form.getMode();

        mode = StringUtils.hasText(mode) ? mode : "write";

        BoardStatus status = form.getStatus();

        status = StringUtils.hasText(String.valueOf(status)) ? status : BoardStatus.ALL;

        commonProcess(form.getBid(), mode);

        boardDataValidator.validate(form, errors);

        if (errors.hasErrors()) {

            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        BoardData data = updateService.process(form);

        return new JSONData(data);
    }

    /**
     * 게시글 단일 조회
     *
     * 게시글 조회, 수정시 기초 데이터로 활용 (프론트엔드)
     *
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        commonProcess(seq, "view");

        BoardData data = infoService.get(seq);

        return new JSONData(data);
    }

    /**
     * 게시글 목록 조회
     *
     * @param bid
     * @return
     */
    @GetMapping("/list/{bid}")
    public JSONData list(@PathVariable("bid") String bid, @ModelAttribute BoardSearch search) {

        commonProcess(bid, "list");

        ListData<BoardData> data = infoService.getList(bid, search);

        return new JSONData(data);
    }

    /**
     * 게시글 상태 단일 | 목록 일괄 수정
     *
     * ALL || SECRET || BLOCK
     *
     * @param seqs
     * @return
     */
    @PatchMapping("/status")
    public JSONData status(@RequestParam("seq") List<Long> seqs, BoardStatus status) {

        commonProcess(seqs, "status");

        List<BoardData> items = statusService.process(seqs, status);
        
        return new JSONData(items);
    }

    /**
     * 조회수 업데이트 처리
     *
     * @param seq
     * @return
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/viewcount/{seq}")
    public JSONData viewCount(@PathVariable("seq") Long seq) {

        viewUpdateService.process(seq);
        
        return new JSONData();
    }

    /**
     * 비회원 비밀번호 검증
     *
     * 응답 코드 204 : 검증 성공
     * 응답 코드 401 : 검증 실패
     *
     * @param seq : 게시글 번호
     * @param password
     * @return
     */
    @PostMapping("/password/{seq}")
    public ResponseEntity<Void> validateGuestPassword(@PathVariable("seq") Long seq, @RequestParam(name = "password", required = false) String password) {

        if (!StringUtils.hasText(password)) {

            throw new BadRequestException(utils.getMessage("NotBlank.password"));
        }

        HttpStatus status = boardDataValidator.checkGuestPassword(password, seq) ? HttpStatus.NO_CONTENT : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).build();
    }


    /**
     * 게시글 번호로 공통 처리
     *
     * Base Method
     *
     * @param seq
     * @param mode
     */
    private void commonProcess(Long seq, String mode) {

        // 게시판 권한 체크 - 조회, 수정, 삭제
        authService.check(mode, seq);
    }

    /**
     * 게시글 번호로 공통 처리
     *
     * @param seqs
     * @param mode
     */
    private void commonProcess(List<Long> seqs, String mode) {

        for (Long seq : seqs) {

            // 게시판 권한 체크 - 조회, 수정, 삭제
            authService.check(mode, seq);
        }
    }

    /**
     * 게시판 아이디로 공통 처리
     *
     * @param bid
     * @param mode
     */
    private void commonProcess(String bid, String mode) {

        // 게시판 권한 체크 - 글 목록, 글 작성
        authService.check(mode, bid);
    }
}