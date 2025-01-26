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
//    @Id
//    @GeneratedValue
//    private Long seq;
//
//    private BoardStatus status;
//
//    @Column(nullable = false)
//    @OneToMany
//    private BoardData boardData;
//}