package org.anonymous.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.global.entities.BaseMemberEntity;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = {
        @Index(name = "idx_bd_created_at", columnList = "createdAt DESC"),
        @Index(name = "idx_bd_bid", columnList = "bid"),
        @Index(name = "idx_bd_category", columnList = "category"),
        @Index(name = "idx_bd_status", columnList = "domainStatus"),
        @Index(name = "idx_bd_notice_created_at", columnList = "notice DESC, createdAt DESC")
})
public class BoardData extends BaseMemberEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long seq;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bid")
    private Config config;

    @Column(length = 45, nullable = false)
    private String gid;

    // 작성자명
    @Column(length = 45, nullable = false)
    private String poster;

    // 비회원 게시글 수정 & 삭제 비밀번호
    @Column(length = 65)
    private String guestPw;

    // 공지사항 여부
    private boolean notice;

    // 게시글 제목
    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String content;

    // 조회수
    private long viewCount;

    // 댓글수
    private long commentCount;

    // 추천수
    private long recommendCount;

    // IP 주소 - 관리자 차단용
    @Column(length = 20)
    private String ipAddr;

    // Browser 정보 - 접속 환경 (안드로이드 || 아이폰 등) 통계용
    private String userAgent;

    // 외부 링크
    // 게시글 링크를 외부링크로 변경, Youtube 재생 등
    @Column(length = 150)
    private String externalLink;

    @Column(length = 60)
    private String youtubeUrl;

    // 게시글 분류
    @Column(length = 60)
    private String category;

    @Enumerated(EnumType.STRING)
    private DomainStatus domainStatus;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "data", cascade = CascadeType.REMOVE)
    private List<CommentData> comment;

    // 이전 게시글
    @Transient
    private BoardData prev;

    // 다음 게시글
    @Transient
    private BoardData next;

    // 게시글 목록 버튼 노출 여부
    @Transient
    private boolean listable;

    // 게시글 수정 & 삭제 버튼 노출 여부
    @Transient
    private boolean editable;

    // 게시글 작성 버튼 노출 여부
    @Transient
    private boolean writable;

    // 내가 작성한 게시글 여부
    @Transient
    private boolean mine;
}
