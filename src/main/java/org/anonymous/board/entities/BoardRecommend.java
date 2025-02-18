package org.anonymous.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(BoardRecommendId.class)
public class BoardRecommend {

    // 게시글 번호
    @Id
    private Long seq;

    // 회원번호
    @Id
    private Long memberSeq;
}
