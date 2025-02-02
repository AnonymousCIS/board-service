package org.anonymous.board.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.DomainStatus;
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

@Tag(name = "Board API", description = "공용 Board 기능")
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

    private final BoardDeleteService deleteService;

    /**
     * 게시판 설정 단일 조회
     *
     * @param bid
     * @return
     */
    @Operation(summary = "게시판 단일 조회", description = "게시판 ID로 게시판을 검색해 단일 조회합니다.")
    @Parameters({
            @Parameter(name = "bid", description = "게시판 ID", required = true, examples = {
                    @ExampleObject(name = "notice", value = "notice"),
                    @ExampleObject(name = "freetalk", value = "freetalk"),
                    @ExampleObject(name = "FAQ", value = "FAQ"),
                    @ExampleObject(name = "QNA", value = "QNA")
            }),
    })
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
    @Operation(summary = "게시글 작성 & 수정 처리", description = "신규 게시글을 작성하거나 혹은 기존 게시글을 수정합니다.")
    @Parameters({
            @Parameter(name = "form", description = "게시글 작성 양식"),
            @Parameter(name = "seq", description = "게시글 ID", required = true, example = "1125"),
            @Parameter(name = "bid", description = "게시글이 속한 게시판 ID", required = true, example = "notice"),
            @Parameter(name = "mode", description = "게시글 처리 모드", required = true, examples = {
                    @ExampleObject(name = "write", value = "write"),
                    @ExampleObject(name = "edit", value = "edit")
            }),
            @Parameter(name = "status", description = "게시글 공개 상태", required = true, examples = {
                    @ExampleObject(name = "ALL", value = "ALL", description = "전체 공개 상태"),
                    @ExampleObject(name = "SECRET", value = "SECRET", description = "비밀 상태(관리자 & 작성자 조회 가능)"),
                    @ExampleObject(name = "BLOCK", value = "BLOCK", description = "관리자 차단 상태(관리자 조회 가능")
            }),
            @Parameter(name = "gid", description = "파일 첨부용 Group ID"),
            @Parameter(name = "poster", description = "게시글 작성자", required = true),
            @Parameter(name = "guestPw", description = "비회원 게시글일 경우 비회원 비밀번호"),
            @Parameter(name = "subject", description = "게시글 제목", required = true),
            @Parameter(name = "content", description = "게시글 내용", required = true),
            @Parameter(name = "category", description = "게시글 분류", examples = {
                    @ExampleObject(name = "일반", value = "일반", description = "기본 분류"),
                    @ExampleObject(name = "질문", value = "질문", description = "질문 게시글 분류"),
                    @ExampleObject(name = "정보공유", value = "정보공유", description = "정보성 게시글 분류")
            })
    })
    @PostMapping("/save")
    public JSONData save(@Valid@RequestBody RequestBoardData form, Errors errors) {

        String mode = form.getMode();

        mode = StringUtils.hasText(mode) ? mode : "write";

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
    @Operation(summary = "게시글 단일 조회", description = "게시글 ID로 게시글을 검색해 단일 조회합니다.")
    @Parameter(name = "seq", description = "게시글 ID", example = "1125")
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
    @Operation(summary = "게시글 목록 조회", description = "게시판 ID로 게시글 목록을 검색해 조회합니다.")
    @Parameters({
            @Parameter(name = "search", description = "게시글 목록 조회용"),
            @Parameter(name = "bid", description = "게시판 ID", required = true, examples = {
                    @ExampleObject(name = "notice", value = "notice"),
                    @ExampleObject(name = "freetalk", value = "freetalk"),
                    @ExampleObject(name = "FAQ", value = "FAQ"),
                    @ExampleObject(name = "QNA", value = "QNA")
            }),
            @Parameter(name = "sort", description = "필드명_정렬방향, 검색 처리시 분해해서 사용", example = "viewCount_DESC"),
            @Parameter(name = "email", description = "회원 이메일별 조회용"),
            @Parameter(name = "category", description = "분류 조회용", examples = {
                    @ExampleObject(name = "일반", value = "일반", description = "기본 분류"),
                    @ExampleObject(name = "질문", value = "질문", description = "질문 게시글 분류"),
                    @ExampleObject(name = "정보공유", value = "정보공유", description = "정보성 게시글 분류")
            }),
            @Parameter(name = "status", description = "게시글 상태별 조회용", examples = {
                    @ExampleObject(name = "ALL", value = "ALL", description = "전체 공개 상태"),
                    @ExampleObject(name = "SECRET", value = "SECRET", description = "비밀 상태(관리자 & 작성자 조회 가능)"),
                    @ExampleObject(name = "BLOCK", value = "BLOCK", description = "관리자 차단 상태(관리자 조회 가능")
            })
    })
    @GetMapping({"/list", "/list/{bid}"})
    public JSONData list(@PathVariable(name="bid", required = false) String bid, @ModelAttribute BoardSearch search) {

        ListData<BoardData> data = null;

        if (StringUtils.hasText(bid)) {

            data = infoService.getList(bid, search);

            commonProcess(bid, "list");

        } else {

            data = infoService.getList(search);

            commonProcess("", "list");
        }

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
    @Operation(summary = "게시글 상태 단일 & 목록 일괄 수정 처리", description = "게시글 상태를 단일 & 목록 일괄 수정합니다.")
    @Parameters({
            @Parameter(name = "seq", description = "게시글 ID", example = "1125"),
            @Parameter(name = "status", description = "게시글 상태별 조회용", examples = {
                    @ExampleObject(name = "ALL", value = "ALL", description = "전체 공개 상태"),
                    @ExampleObject(name = "SECRET", value = "SECRET", description = "비밀 상태(관리자 & 작성자 조회 가능)"),
                    @ExampleObject(name = "BLOCK", value = "BLOCK", description = "관리자 차단 상태(관리자 조회 가능")
            })
    })
    @PatchMapping("/status")
    public JSONData status(@RequestParam("seq") List<Long> seqs, @RequestParam("status") DomainStatus status) {

        commonProcess(seqs, "edit");

        List<BoardData> items = statusService.process(seqs, status, "board");
        
        return new JSONData(items);
    }


    /**
     * 게시글 삭제
     * 단일 & 목록 일괄 수정
     *
     * DB 삭제 X, 일반 유저 전용 삭제로 deleteAt 현재 시간으로 부여
     *
     * @param seqs
     * @return
     */
    @Operation(summary = "게시글 삭제 단일 & 목록 일괄 처리", description = "게시글 ID로 게시글을 단일 & 목록 삭제합니다. 일반 사용자용 삭제이므로 DB에서 삭제되지 않고 DeletedAt을 현재 시간으로 부여합니다.")
    @Parameter(name = "seq", description = "게시글 ID")
    @PatchMapping("/userdeletes")
    public JSONData userDeletes(@RequestParam("seq") List<Long> seqs) {

        commonProcess(seqs, "delete");

        List<BoardData> items = deleteService.userDelete(seqs);

        return new JSONData(items);
    }

    /**
     * 조회수 업데이트 처리
     *
     * @param seq
     * @return
     */
    @Operation(summary = "게시글 조회수 업데이트 처리", description = "게시글 조회수 업데이트시 반영 처리합니다.")
    @Parameter(name = "seq", description = "게시글 ID", example = "1125")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/viewcount/{seq}")
    public JSONData viewCount(@PathVariable("seq") Long seq) {

        long total = viewUpdateService.process(seq);
        
        return new JSONData(total);
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
    @Operation(summary = "비회원 비밀번호 검증", description = "비회원 게시글 작성 & 수정 & 삭제시 비회원 비밀번호 검증 합니다.")
    @Parameters({
            @Parameter(name = "seq", description = "게시글 ID", example = "1125"),
            @Parameter(name = "password", description = "비회원 비밀번호", example = "_aA1234")
    })
    @PostMapping("/password/{seq}")
    public ResponseEntity<Void> validateGuestPassword(@PathVariable("seq") Long seq, @RequestParam(name = "password", required = false) String password) {

        if (!StringUtils.hasText(password)) {

            throw new BadRequestException(utils.getMessage("NotBlank.password"));
        }

        HttpStatus status = boardDataValidator.checkGuestPassword(password, seq) ? HttpStatus.NO_CONTENT : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).build();
    }


    /**
     * 게시글 번호로 개별 공통 처리
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
     * 게시글 번호로 목록 공통 처리
     *
     * @param seqs
     * @param mode
     */
    private void commonProcess(List<Long> seqs, String mode) {

        // 게시판 권한 체크 - 조회, 수정, 삭제
        authService.check(mode, seqs);
    }

    /**
     * 게시판 아이디로 개별 공통 처리
     *
     * @param bid
     * @param mode
     */
    private void commonProcess(String bid, String mode) {

        // 게시판 권한 체크 - 글 목록, 글 작성
        authService.check(mode, bid);
    }

    /**
     * 모드로 개별 공통 처리
     *
     * @param mode
     */
//    private void commonProcess(String mode) {
//
//        // 게시판 권한 체크 - 글 목록
//        authService.check(mode);
//    }
}