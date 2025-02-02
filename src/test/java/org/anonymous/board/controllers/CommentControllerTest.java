package org.anonymous.board.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.services.BoardUpdateService;
import org.anonymous.member.contants.Authority;
import org.anonymous.member.test.annotations.MockMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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

    // @BeforeEach
    void init() {

        RequestBoardData _boardData = new RequestBoardData();

        _boardData.setBid("freetalk");
        _boardData.setSubject("게시글 제목");
        _boardData.setContent("게시글 내용");
        _boardData.setPoster("작성자44");
        _boardData.setGid(UUID.randomUUID().toString());
        _boardData.setGuestPw("a1234");
        _boardData.setStatus(DomainStatus.ALL);

        boardData = boardUpdateService.process(_boardData);

        form = new RequestComment();

        form.setContent("댓글 내용");
        form.setBoardDataSeq(boardData.getSeq());
        form.setCommenter("댓글 작성자44");
        form.setStatus(DomainStatus.ALL);
    }

    @Test
    @MockMember(email = "user44@test.org")
    @DisplayName("댓글 작성 및 수정 테스트")
    void commentTest() throws Exception {

        // 댓글 작성
//        for (int i = 0; i < 5; i++) {
//            String body = om.writeValueAsString(form);
//
//            String res = mockMvc.perform(post("/comment/save")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(body)).andDo(print())
//                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
//
//            JSONData jsonData = om.readValue(res, JSONData.class);
//
//            CommentData data = om.convertValue(jsonData.getData(), CommentData.class);
//        }

        // 댓글 단일 조회
//        mockMvc.perform(get("/comment/view/" + data.getSeq())).andDo(print());

        // 게시글에 속한 댓글 목록 조회
        // mockMvc.perform(get("/comment/inboardlist/" + data.getData().getSeq())).andDo(print());
    }

    @Test
    @MockMember(authority = {Authority.ADMIN, Authority.USER})
    @DisplayName("유저 삭제 댓글 조회 테스트")
    void userDeleteViewTest() throws Exception {

        // 유저 삭제 댓글 단일 조회 (유저 삭제 댓글 조회됐는지 체크)
        mockMvc.perform(get("/comment/view/" + "952")).andDo(print());

        // 게시글에 속한 댓글 목록 조회 (유저 삭제 댓글까지 조회됐는지 체크)
        // mockMvc.perform(get("/comment/inboardlist/" + "952")).andDo(print());

        // 댓글 목록 조회 (유저 삭제 게시글까지 조회됐는지 체크)
    }

    @Test
    @MockMember(email = "user44@test.org")
    @DisplayName("댓글 유저 삭제 테스트")
    void commentDeleteTest() throws Exception {

        // 댓글 유저 삭제 테스트
        mockMvc.perform(patch("/comment/userdeletes")
                        .param("seq", String.valueOf(952)))
                .andDo(print());
    }
}
