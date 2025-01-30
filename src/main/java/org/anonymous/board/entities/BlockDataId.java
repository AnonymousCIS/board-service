package org.anonymous.board.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BlockDataId {

    private Long seq;

    // 차단 컨텐츠 타입
    // 댓글 | 게시글
    private String type;

    // 차단 회원 이메일
    private String email;
}