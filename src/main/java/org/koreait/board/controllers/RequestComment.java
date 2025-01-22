package org.koreait.board.controllers;

import lombok.Data;

@Data
public class RequestComment {

    private String mode;

    private Long seq;

    private Long boardDataSeq;

    private String commenter;

    private String guestPw;

    private String content;


}