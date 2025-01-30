package org.anonymous.board.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.global.entities.BaseEntity;

@Data
@Entity
@IdClass(BlockDataId.class)
public class BlockData extends BaseEntity {

    @Id
    private Long seq;

    @Column(nullable = false)
    private DomainStatus status;

    // 차단 컨텐츠 타입
    // 댓글 | 게시글
    @Id
    @Column(nullable = false)
    private String type;

    // 차단 회원 이메일
    @Id
    @Column(nullable = false)
    private String email;
}