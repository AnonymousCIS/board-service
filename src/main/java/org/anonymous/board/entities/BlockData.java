//package org.anonymous.board.entities;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import org.anonymous.board.constants.BoardStatus;
//
//@Data
//@Entity
//public class BlockData {
//
//    // 차단당한 컨텐츠 예시
//
//    @Id
//    @GeneratedValue
//    private Long seq;
//
//    private BoardStatus status;
//
//    @Column(nullable = false)
//    @OneToMany
//    private BoardData boardData;
//
//    @Column(nullable = false)
//    @OneToMany
//    private CommentData commentData;
//}