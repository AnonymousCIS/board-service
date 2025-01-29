package org.anonymous.board.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.services.BoardUpdateService;
import org.anonymous.global.rests.JSONData;
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
@AutoConfigureMockMvc
@DisplayName("댓글 테스트")
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoardUpdateService boardUpdateService;

    private BoardData boardData;

    private RequestComment form;

    @BeforeEach
    void init() {

        RequestBoardData _boardData = new RequestBoardData();

        // _boardData.setSeq(1L);
        _boardData.setBid("freetalk");
        _boardData.setSubject("게시글 제목");
        _boardData.setContent("게시글 내용");
        _boardData.setPoster("작성자1");
        _boardData.setGid(UUID.randomUUID().toString());
        _boardData.setGuestPw("a1234");
        _boardData.setStatus(BoardStatus.ALL);

        boardData = boardUpdateService.process(_boardData);

        form = new RequestComment();

        form.setSeq(1L);
        form.setContent("댓글 내용");
        form.setBoardDataSeq(boardData.getSeq());
        form.setCommenter("댓글 작성자1");
    }

    @Test
    @MockMember
    @DisplayName("댓글 테스트")
    void commentTest() throws Exception {

        String body = om.writeValueAsString(form);

        String res = mockMvc.perform(post("/comment/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andDo(print())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        JSONData jsonData = om.readValue(res, JSONData.class);

        // CommentData data = om.readValue(om.writeValueAsString(jsonData), CommentData.class);

        CommentData data = om.convertValue(jsonData.getData(), CommentData.class);

        // 댓글 단일 조회
        mockMvc.perform(get("/comment/view/" + data.getSeq()))
                .andDo(print());

        // 게시글에 속한 댓글 목록 조회
        mockMvc.perform(get("/comment/inboardlist/" + data.getData().getSeq())).andDo(print());
    }
}
