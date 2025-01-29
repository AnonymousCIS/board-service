package org.anonymous.board.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.Config;
import org.anonymous.board.services.configs.BoardConfigUpdateService;
import org.anonymous.global.rests.JSONData;
import org.anonymous.member.contants.Authority;
import org.anonymous.member.test.annotations.MockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
// @ActiveProfiles({"default, test"})
@AutoConfigureMockMvc
@DisplayName("게시판 - 공통 통합 테스트")
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoardConfigUpdateService configUpdateService;

    private Config config;

    private RequestBoardData form;

    @BeforeEach
    void init() {
        RequestConfig _config = new RequestConfig() ;

        _config.setBid("freetalk");
        _config.setName("자유게시판");
        _config.setOpen(true);

        config = configUpdateService.process(_config);

        form = new RequestBoardData();

        form.setBid(config.getBid());
        form.setSubject("제목");
        form.setContent("내용");
        form.setPoster("작성자");
        form.setGid(UUID.randomUUID().toString());
        form.setGuestPw("a1234");
        form.setStatus(BoardStatus.ALL);
    }

    @Test
    @MockMember(authority = {Authority.ADMIN, Authority.USER})
    @DisplayName("게시글 테스트")
    void writeTest() throws Exception {

        String body = om.writeValueAsString(form);

        String res = mockMvc.perform(post("/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)).andDo(print())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONData jsonData = om.readValue(res, JSONData.class);

        BoardData data = om.readValue(om.writeValueAsString(jsonData.getData()), BoardData.class);

        // System.out.println(data);

        // 게시글 단일 조회
        mockMvc.perform(get("/view/" + data.getSeq()))
                .andDo(print());

        // 게시글 목록 조회
        mockMvc.perform(get("/list/" + config.getBid()))
                .andDo(print());

        // 게시글 유저 삭제
//        mockMvc.perform(patch("/userdeletes")
//                        .param("seq", String.valueOf(data.getSeq())))
//                .andDo(print());

        // 게시글 관리자 삭제
//        mockMvc.perform(delete("/admin/deletes")
//                        .param("seq", String.valueOf(data.getSeq())))
//                .andDo(print());
    }
}