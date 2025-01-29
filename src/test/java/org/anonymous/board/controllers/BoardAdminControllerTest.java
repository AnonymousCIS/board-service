package org.anonymous.board.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.anonymous.member.contants.Authority;
import org.anonymous.member.test.annotations.MockMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
// @ActiveProfiles({"default, test"})
@AutoConfigureMockMvc
@DisplayName("게시판 - 관리자 통합 테스트")
public class BoardAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private RequestConfig form;

//    @BeforeEach
//    void init() {
//
//        form = new RequestConfig();
//
//        form.setBid("notice");
//        form.setName("공지사항");
//    }

    @Test
    @MockMember(authority = {Authority.ADMIN, Authority.USER})
    @DisplayName("게시판 설정 등록 테스트")
    void boardRegisterTest() throws Exception {

        form = new RequestConfig();

        form.setBid("notice");
        form.setName("공지사항");

        String body = om.writeValueAsString(form);

        // 게시판 등록 & 수정
        mockMvc.perform(post("/admin/config/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        // 게시판 목록 조회
        mockMvc.perform(get("/admin/config/list"))
                .andDo(print());
    }

    @Test
    @MockMember(authority = {Authority.ADMIN, Authority.USER})
    @DisplayName("게시글, 댓글 관리자 삭제 테스트")
    void deleteTest() throws Exception {

        mockMvc.perform(delete("/admin/deletes")
                        .param("seq","1"))
                .andDo(print());
    }
}