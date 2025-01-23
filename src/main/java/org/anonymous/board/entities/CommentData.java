package org.anonymous.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.anonymous.global.entities.BaseMemberEntity;

import java.io.Serializable;

@Data
@Entity
@Table(indexes = @Index(name = "idx_comment_data_created_at", columnList = "createdAt ASC"))
public class CommentData extends BaseMemberEntity implements Serializable {

    @Id @GeneratedValue
    private Long seq;

    // 한개의 게시글에 여러 댓글
    // 🍅🍅🍅🍅🍅🍅🍅🍅🍅🍅🍅🍅
    // 프론트 처리? 불필요?
    // 게시글 쪽에서는 Cascade REMOVE 때문에 OneToMany 사용한것 연관
    // 🍅🍅🍅🍅🍅🍅🍅🍅🍅🍅🍅🍅
     @JsonIgnore
     @ToString.Exclude
     @ManyToOne(fetch = FetchType.LAZY)
     private BoardData data;

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