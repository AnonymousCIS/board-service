package org.anonymous.board.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.services.BoardAuthService;
import org.anonymous.board.services.BoardStatusService;
import org.anonymous.board.services.comment.CommentDeleteService;
import org.anonymous.board.services.comment.CommentInfoService;
import org.anonymous.board.services.comment.CommentUpdateService;
import org.anonymous.board.validators.CommentValidator;
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

@Tag(name = "Comment API", description = "댓글 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final Utils utils;

    private final BoardAuthService authService;

    private final CommentInfoService infoService;

    private final CommentUpdateService updateService;

    private final BoardStatusService statusService;

    private final CommentDeleteService deleteService;

    private final CommentValidator commentValidator;

    /**
     * 댓글 작성 & 수정 처리
     *
     * @return
     */
    @Operation(summary = "댓글 작성 & 수정 처리", description = "신규 댓글을 작성하거나 혹은 기존 댓글을 수정합니다.")
    @Parameters({
            @Parameter(name = "form", description = "댓글 작성 양식"),
            @Parameter(name = "seq", description = "댓글 ID", required = true, example = "1125"),
            @Parameter(name = "boardDataSeq", description = "댓글이 속한 게시글 ID", required = true, example = "1125"),
            @Parameter(name = "mode", description = "댓글 처리 모드", required = true, examples = {
                    @ExampleObject(name = "write", value = "write"),
                    @ExampleObject(name = "edit", value = "edit")
            }),
            @Parameter(name = "status", description = "댓글 공개 상태", required = true, examples = {
                    @ExampleObject(name = "ALL", value = "ALL", description = "전체 공개 상태"),
                    @ExampleObject(name = "SECRET", value = "SECRET", description = "비밀 상태(관리자 & 작성자 조회 가능)"),
                    @ExampleObject(name = "BLOCK", value = "BLOCK", description = "관리자 차단 상태(관리자 조회 가능")
            }),
            @Parameter(name = "commenter", description = "댓글 작성자", required = true),
            @Parameter(name = "guestPw", description = "비회원 댓글일 경우 비회원 비밀번호"),
            @Parameter(name = "content", description = "댓글 내용", required = true)
    })
    @PostMapping("/save")
    public JSONData save(@RequestBody @Valid RequestComment form, Errors errors) {

        String mode = form.getMode();

        mode = StringUtils.hasText(mode) ? mode : "write";

        if (mode.equals("edit")) {

            // 수정 권한 여부 체크
            commonProcess(form.getSeq());
        }

        commentValidator.validate(form, errors);

        if (errors.hasErrors()) {

            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        CommentData data = updateService.save(form);

        return new JSONData(data);
    }

    /**
     * 댓글 단일 조회
     *
     * 수정시 기초 데이터 활용 (프론트엔드)
     *
     * @param seq : 댓글
     * @return
     */
    @Operation(summary = "댓글 단일 조회", description = "댓글 ID로 댓글을 검색해 단일 조회합니다.")
    @Parameter(name = "seq", description = "댓글 ID", example = "1125")
    @GetMapping("/view/{seq}")
    public JSONData view(@PathVariable("seq") Long seq) {

        commonProcess(seq);

        CommentData item = infoService.get(seq);

        return new JSONData(item);
    }

    /**
     * 게시글에 속한 댓글 목록 조회
     *
     * @param seq : 게시글(BoardData) seq
     * @return
     */
    @Operation(summary = "게시글에 속한 댓글 목록 조회", description = "게시글 ID로 속한 댓글 목록을 검색해 조회합니다.")
    @Parameter(name = "seq", description = "게시글 ID")
    @GetMapping("/inboardlist/{seq}")
    public JSONData inBoardList(@PathVariable("seq") Long seq) {

        List<CommentData> items = infoService.getList(seq);

        return new JSONData(items);
    }

    /**
     * 댓글 목록 조회
     *
     * @param search
     * @return
     */
    @Operation(summary = "댓글 목록 조회", description = "댓글 목록을 검색해 조회합니다.")
    @Parameters({
            @Parameter(name = "search", description = "댓글 목록 조회용"),
            @Parameter(name = "seq", description = "댓글 ID", required = true, example = "1125"),
            @Parameter(name = "sort", description = "필드명_정렬방향, 검색 처리시 분해해서 사용", example = "createdAt_DESC"),
            @Parameter(name = "email", description = "회원 이메일별 조회용"),
            @Parameter(name = "status", description = "댓글 상태별 조회용", examples = {
                    @ExampleObject(name = "ALL", value = "ALL", description = "전체 공개 상태"),
                    @ExampleObject(name = "SECRET", value = "SECRET", description = "비밀 상태(관리자 & 작성자 조회 가능)"),
                    @ExampleObject(name = "BLOCK", value = "BLOCK", description = "관리자 차단 상태(관리자 조회 가능")
            })
    })
    @GetMapping("/list")
    public JSONData list(@ModelAttribute CommentSearch search) {

        ListData<CommentData> items = infoService.getList(search);

        return new JSONData(items);
    }

    /**
     * 댓글 상태 단일 | 목록 일괄 수정
     *
     * ALL || BLOCK
     *
     * @return
     */
    @Operation(summary = "댓글 상태 단일 & 목록 일괄 수정 처리", description = "댓글 상태를 단일 & 목록 일괄 수정합니다.")
    @Parameters({
            @Parameter(name = "seq", description = "댓글 ID", example = "1125"),
            @Parameter(name = "status", description = "댓글 상태별 조회용", examples = {
                    @ExampleObject(name = "ALL", value = "ALL", description = "전체 공개 상태"),
                    @ExampleObject(name = "SECRET", value = "SECRET", description = "비밀 상태(관리자 & 작성자 조회 가능)"),
                    @ExampleObject(name = "BLOCK", value = "BLOCK", description = "관리자 차단 상태(관리자 조회 가능")
            })
    })
    @PatchMapping("/status")
    public JSONData status(@RequestParam("seq") List<Long> seqs, @RequestParam("status") BoardStatus status) {

        commonProcess(seqs);

        List<CommentData> items = statusService.process(seqs, status , "comment");

        return new JSONData(items);
    }

    /**
     * 댓글 삭제
     * 단일 & 목록 일괄 수정
     *
     * DB 삭제 X, 일반 유저 전용 삭제로 deleteAt 현재 시간으로 부여
     *
     * @param seqs
     * @return
     */
    @Operation(summary = "댓글 삭제 단일 & 목록 일괄 처리", description = "댓글 ID로 댓글을 단일 & 목록 삭제합니다. 일반 사용자용 삭제이므로 DB에서 삭제되지 않고 DeletedAt을 현재 시간으로 부여합니다.")
    @Parameter(name = "seq", description = "댓글 ID")
    @PatchMapping("/userdeletes")
    public JSONData userDeletes(@RequestParam("seq") List<Long> seqs) {

        commonProcess(seqs);

        List<CommentData> items = deleteService.userDelete(seqs);

        return new JSONData(items);
    }

    /**
     * 비회원 비밀번호 검증
     *
     * 응답 코드 204 : 검증 성공
     * 응답 코드 401 : 검증 실패
     *
     * @param seq : 댓글 번호
     * @param password
     * @return
     */
    @Operation(summary = "비회원 비밀번호 검증", description = "비회원 댓글 작성 & 수정 & 삭제시 비회원 비밀번호 검증 합니다.")
    @Parameters({
            @Parameter(name = "seq", description = "댓글 ID", example = "1125"),
            @Parameter(name = "password", description = "비회원 비밀번호", example = "_aA1234")
    })
    @PostMapping("/password/{seq}")
    public ResponseEntity<Void> validateGuestPassword(@PathVariable("seq") Long seq, @RequestParam(name = "password", required = false) String password) {

        if (!StringUtils.hasText(password)) {

            throw new BadRequestException(utils.getMessage("NotBlank.password"));
        }

        HttpStatus status = commentValidator.checkGuestPassword(password, seq) ? HttpStatus.NO_CONTENT : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).build();
    }

    /**
     * 공통 처리
     *
     * @param seq
     */
    private void commonProcess(Long seq) {

        // 댓글 권한 체크
        authService.check("comment", seq);
    }

    /**
     * 댓글 번호로 공통 처리
     *
     * @param seqs
     */
    private void commonProcess(List<Long> seqs) {

        for (Long seq : seqs) {

            // 댓글 권한 체크 - 조회, 수정, 삭제
            authService.check("comment", seq);
        }
    }
}