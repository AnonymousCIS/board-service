package org.anonymous.board.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.entities.Config;
import org.anonymous.board.services.BoardDeleteService;
import org.anonymous.board.services.comment.CommentDeleteService;
import org.anonymous.board.services.configs.BoardConfigDeleteService;
import org.anonymous.board.services.configs.BoardConfigInfoService;
import org.anonymous.board.services.configs.BoardConfigUpdateService;
import org.anonymous.board.validators.BoardConfigValidator;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.paging.ListData;
import org.anonymous.global.rests.JSONData;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Board API", description = "관리자 전용 Board 기능")
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
    @Operation(summary = "게시판 단일 등록 & 수정 처리", description = "신규 게시판 설정을 등록하거나 혹은 기존 게시판 설정을 수정합니다.")
    // @ApiResponse(responseCode = "201",)
    @Parameters({
            @Parameter(name = "form", description = "게시판 설정 양식"),
            @Parameter(name = "bid", description = "게시판 ID", required = true, examples = {
                    @ExampleObject(name = "notice", value = "notice", description = "공지사항"),
                    @ExampleObject(name = "freetalk", value = "freetalk", description = "자유게시판"),
                    @ExampleObject(name = "FAQ", value = "FAQ", description = "자주 묻는 질문"),
                    @ExampleObject(name = "QNA", value = "QNA", description = "1:1 문의")
            }),
            @Parameter(name = "name", description = "게시판명", required = true, examples = {
                    @ExampleObject(name = "공지사항", value = "공지사항"),
                    @ExampleObject(name = "자유게시판", value = "자유게시판"),
                    @ExampleObject(name = "자주묻는질문", value = "자주묻는질문"),
                    @ExampleObject(name = "1:1문의", value = "1:1문의")
            }),
            @Parameter(name = "open", description = "게시판 공개 여부", required = true),
            @Parameter(name = "category", description = "게시판내 게시글 분류 목록", examples = {
                    @ExampleObject(name = "일반", value = "일반", description = "기본 분류"),
                    @ExampleObject(name = "질문", value = "질문", description = "질문 게시글 분류"),
                    @ExampleObject(name = "정보공유", value = "정보공유", description = "정보성 게시글 분류")
            })
    })
    @PostMapping("/config/save")
    public JSONData save(@Valid @RequestBody RequestConfig form, Errors errors) {

        configValidator.validate(form, errors);

        if (errors.hasErrors()) {

            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        Config item = updateService.process(form);

        return new JSONData(item);
    }

    /**
     * 게시판 설정 목록 조회
     *
     * @param search
     * @return
     */
    @Operation(summary = "게시판 목록 조회", description = "게시판 목록을 검색해 조회합니다.")
    @Parameters({
            @Parameter(name = "search", description = "게시판 단일 & 목록 조회용"),
            @Parameter(name = "bid", description = "게시판 ID", required = true, examples = {
                    @ExampleObject(name = "notice", value = "notice"),
                    @ExampleObject(name = "freetalk", value = "freetalk"),
                    @ExampleObject(name = "FAQ", value = "FAQ"),
                    @ExampleObject(name = "QNA", value = "QNA")
            })
    })
    @GetMapping("/config/list")
    public JSONData list(@ModelAttribute BoardConfigSearch search) {

        ListData<Config> items = infoService.getList(search);

        return new JSONData(items);
    }

    /**
     * 게시판 단일 | 목록 일괄 수정 처리
     *
     * @param form
     * @return
     */
    @Operation(summary = "게시판 단일 & 목록 일괄 수정 처리", description = "게시판 설정을 단일 & 목록 일괄 수정합니다.")
    @Parameters({
            @Parameter(name = "form", description = "게시판 설정 양식"),
            @Parameter(name = "bid", description = "게시판 ID", required = true, examples = {
                    @ExampleObject(name = "notice", value = "notice"),
                    @ExampleObject(name = "freetalk", value = "freetalk"),
                    @ExampleObject(name = "FAQ", value = "FAQ"),
                    @ExampleObject(name = "QNA", value = "QNA")
            }),
            @Parameter(name = "name", description = "게시판명", required = true, examples = {
                    @ExampleObject(name = "공지사항", value = "공지사항"),
                    @ExampleObject(name = "자유게시판", value = "자유게시판"),
                    @ExampleObject(name = "자주묻는질문", value = "자주묻는질문"),
                    @ExampleObject(name = "1:1문의", value = "1:1문의")
            }),
            @Parameter(name = "open", description = "게시판 공개 여부", required = true),
            @Parameter(name = "category", description = "게시판내 게시글 분류 목록")
    })
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
    @Operation(summary = "게시판 단일 & 목록 일괄 삭제 처리", description = "게시판 ID로 게시판을 단일 & 목록 일괄 삭제합니다.")
    @Parameters({
            @Parameter(name = "bid", description = "게시판 ID", required = true, examples = {
                    @ExampleObject(name = "notice", value = "notice"),
                    @ExampleObject(name = "freetalk", value = "freetalk"),
                    @ExampleObject(name = "FAQ", value = "FAQ"),
                    @ExampleObject(name = "QNA", value = "QNA")
            }),
    })
    @DeleteMapping("/config/deletes")
    public JSONData configDeletes(@RequestParam("bid") List<String> bids) {

        List<Config> items = ConfigDeleteService.process(bids);

        return new JSONData(items);
    }

    /**
     * 게시글 단일 | 목록 일괄 삭제 처리
     *
     * @return
     */
    @Operation(summary = "게시글 단일 & 목록 일괄 삭제 처리", description = "게시글 ID로 게시글을 단일 & 목록 일괄 삭제합니다.")
    @Parameter(name = "seq", description = "게시글 ID", required = true, example = "1125")
    @DeleteMapping("/deletes")
    public JSONData deletes(@RequestParam("seq") List<Long> seqs) {

        List<BoardData> items = boardDeleteService.delete(seqs);

        return new JSONData(items);
    }

    /**
     * 댓글 단일 | 목록 일괄 삭제
     *
     * @param seqs
     * @return
     */
    @Operation(summary = "댓글 단일 & 목록 일괄 삭제 처리", description = "댓글 ID로 댓글을 단일 & 목록 일괄 삭제합니다.")
    @Parameter(name = "seq", description = "댓글 ID", required = true, example = "1125")
    @DeleteMapping("/comment/deletes")
    public JSONData commentDeletes(@RequestParam("seq") List<Long> seqs) {

        List<CommentData> items = commentDeleteService.delete(seqs);
        
        return new JSONData(items);
    }
}
