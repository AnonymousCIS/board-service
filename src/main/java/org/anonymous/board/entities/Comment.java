package org.anonymous.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long seq;

    // 회원 한명에 여러 댓글
    private String email;

    // 한개의 게시글에 여러 댓글
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardData boardData;

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

    // 댓글 수정 & 삭제 버튼 노출 여부
    @Transient
    private boolean editable;

    // 댓글 작성 버튼 노출 여부
    @Transient
    private boolean writable;

    // 내가 작성한 댓글 여부
    @Transient
    private boolean mine;
}
