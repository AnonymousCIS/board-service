package org.anonymous.board.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

@Data
@Entity
@IdClass(BoardViewId.class)
public class BoardView {

    // 게시글 번호
    @Id
    private Long seq;

    // 회원번호 || IP + User-Agent 로 조합해서 생성
    @Id
    private int hash;
}