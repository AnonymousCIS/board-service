package org.anonymous.board.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.member.contants.Authority;
import org.anonymous.member.test.annotations.MockMember;
import org.junit.jupiter.api.BeforeEach;
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
// @ActiveProfiles({"default, test"})
@AutoConfigureMockMvc
@DisplayName("게시판 - 공통 통합 테스트")
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private RequestBoardData form;

//    @Autowired
//    private BoardConfigUpdateService configUpdateService;
//
//    private Config config;

    @BeforeEach
    void init() {
//        RequestConfig _config = new RequestConfig() ;
//
//        _config.setBid("freetalk");
//        _config.setName("자유게시판");
//        _config.setOpen(true);
//
//        config = configUpdateService.process(_config);

        form = new RequestBoardData();

        form.setBid("freetalk");
        form.setSubject("제목");
        form.setContent("내용");
        form.setPoster("작성자66");
        form.setGid(UUID.randomUUID().toString());
        form.setGuestPw("a1234");
        form.setStatus(DomainStatus.ALL);
    }

    @Test
    @MockMember(email = "user04@test.org", authority = {Authority.USER})
    @DisplayName("게시글 테스트")
    void boardDataTest() throws Exception {

//        for (int i = 0; i < 6; i++) {
//
//            // 게시글 작성
//            String body = om.writeValueAsString(form);
//
//            String res = mockMvc.perform(post("/save")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(body)).andDo(print())
//                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
//
//            JSONData jsonData = om.readValue(res, JSONData.class);
//
//            BoardData data = om.readValue(om.writeValueAsString(jsonData.getData()), BoardData.class);
//        }

//        // 게시글 단일 조회
//        mockMvc.perform(get("/view/" + data.getSeq()))
//                .andDo(print());
//
        // 게시판에 속한 게시글 목록 조회
//        mockMvc.perform(get("/list/" + "freetalk"))
//                .andDo(print());

        // 게시글 상태 단일 | 목록 일괄 수정
        mockMvc.perform(patch("/status")
                .param("seq", "52")
                .param("seq", "53")
                .param("status", String.valueOf(DomainStatus.ALL)))
                .andDo(print());

//        // 게시글 유저 삭제
//        mockMvc.perform(patch("/userdeletes")
//                        .param("seq", String.valueOf(data.getSeq())))
//                .andDo(print());
    }
}