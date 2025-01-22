package org.koreait.board.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.koreait.member.Member;

@Data
public class RequestBoard {

    // 게시글 번호
    private Long seq;

    private String mode;

    // 게시판 ID
    @NotBlank
    private String bid;

    // 파일 첨부용
    @NotBlank
    private String gid;

    // 작성자
    @NotBlank
    private String poster;

    private Member email;

    // 비회원 비밀번호
    @Size(min = 4, max = 50)
    private String guestPw;

    // 게시글 제목
    @NotBlank
    private String subject;

    // 게시글 내용
    @NotBlank
    private String content;

    // 공지글 여부
    private boolean notice;

    // 외부 링크
    private String externalLink;

    private String youtubeUrl;

    // 게시글 분류
    private String category;
}
