package org.anonymous.board.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.anonymous.board.constants.DomainStatus;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestComment {

    private String mode;

    private Long seq;

    private DomainStatus status;

    // 원 게시글 번호
    @NotNull
    private Long boardDataSeq;

    @NotBlank
    private String commenter;

    @Size(min=4, max = 50)
    private String guestPw;

    @NotBlank
    private String content;
}