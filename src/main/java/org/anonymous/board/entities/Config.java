package org.anonymous.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.anonymous.global.entities.BaseMemberEntity;
import org.anonymous.member.contants.Authority;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
public class Config extends BaseMemberEntity implements Serializable {

    @Id
    @Column(length = 30)
    private String bid;

    @Column(length = 90, nullable = false)
    private String name;

    private boolean open;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "config", cascade = CascadeType.REMOVE)
    private List<BoardData> boardData;

    @Lob
    private String category;

    private int rowsPerPage;

    private int pageRanges;

    private int pageRangesMobile;

    private boolean useEditor;

    private boolean useEditorImage;

    private boolean useAttachFile;

    private boolean useComment;

    // 게시글 조회 하단에 게시글 목록 노출 여부
    private boolean listUnderView;

    // 게시글 작성 후 이동 경로
    // list : 목록
    // view : 게시글 조회
    private String locationAfterWriting;

    private String skin;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority listAuthority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority viewAuthority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority writeAuthority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority commentAuthority;

    @Transient
    private List<String> categories;

    @Transient
    private boolean listable;

    @Transient
    private boolean writable;
}