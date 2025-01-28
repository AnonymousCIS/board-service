package org.anonymous.board.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.global.entities.BaseEntity;

@Data
@Entity
public class BlockData extends BaseEntity {

    @Id
    @GeneratedValue
    private Long seq;

    @Column(nullable = false)
    private BoardStatus status;

    // 차단 컨텐츠 타입
    // 댓글 | 게시글
    @Column(nullable = false)
    private String type;

    // 차단 회원 이메일
    @Column(nullable = false)
    private String email;
}