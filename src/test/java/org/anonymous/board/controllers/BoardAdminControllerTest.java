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

import java.util.ArrayList;
import java.util.List;

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

    private RequestConfig form2;

    /*
    @BeforeEach
    void init() {

        form = new RequestConfig();

        form.setBid("notice");
        form.setName("공지사항");
    }
     */

    @Test
    @MockMember(authority = {Authority.ADMIN, Authority.USER})
    @DisplayName("게시판 테스트")
    void configTest() throws Exception {

        form = new RequestConfig();

        form.setBid("notice");
        form.setName("공지사항");

        String body = om.writeValueAsString(form);

        // 게시판 등록
        mockMvc.perform(post("/admin/config/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print());

        // 게시판 등록2

        form2 = new RequestConfig();

        form2.setBid("freetalk");
        form2.setName("자유게시판");

        String bodyFree = om.writeValueAsString(form2);

        mockMvc.perform(post("/admin/config/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyFree))
                .andDo(print());

        // 게시판 목록 조회
        mockMvc.perform(get("/admin/config/list"))
                .andDo(print());

        // 게시판 설정 단일 수정 (/save)
        form.setOpen(false);
        form.setName("(수정)공지사항");
        form.setMode("edit");
        String body2 = om.writeValueAsString(form);

        mockMvc.perform(post("/admin/config/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body2))
                .andDo(print());

        // 게시판 설정 목록 일괄 수정 (/update)
        form.setSkin("gallery");
        form.setName("(최종수정)공지사항");
        form.setMode("edit");

        form2.setName("(수정)자유게시판");

        List<RequestConfig> bodyList = new ArrayList<>();
        bodyList.add(form);
        bodyList.add(form2);

        String bodyListString = om.writeValueAsString(bodyList);

        mockMvc.perform(patch("/admin/config/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyListString))
                .andDo(print());
    }

    @Test
    @MockMember(authority = {Authority.ADMIN, Authority.USER})
    @DisplayName("게시판 삭제 테스트")
    void configDeleteTest() throws Exception {

        mockMvc.perform(delete("/admin/config/deletes")
                        .param("bid","freetalk"))
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