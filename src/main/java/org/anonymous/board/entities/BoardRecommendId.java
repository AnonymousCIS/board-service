package org.anonymous.board.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BoardRecommendId {

    // 게시글 번호
    private Long seq;

    // 회원번호
    private Long memberSeq;
}
