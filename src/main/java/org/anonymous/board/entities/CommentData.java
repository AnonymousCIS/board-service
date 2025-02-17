package org.anonymous.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.global.entities.BaseMemberEntity;

import java.io.Serializable;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = @Index(name = "idx_comment_data_created_at", columnList = "createdAt ASC"))
public class CommentData extends BaseMemberEntity implements Serializable {

    @Id @GeneratedValue
    private Long seq;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardData data;

    @Enumerated(EnumType.STRING)
    private DomainStatus status;

    // 작성자
    @Column(length = 40, nullable = false)
    private String commenter;

    @Column(length = 65)
    private String guestPw;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 20)
    private String ipAddr;

    @Column(length = 150)
    private String userAgent;

    // 댓글 수정 & 삭제 가능 여부
    @Transient
    private boolean editable;

    // 내가 작성한 댓글 여부
    @Transient
    private boolean mine;
}