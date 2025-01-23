package org.anonymous.board.controllers;

import lombok.Data;
import org.anonymous.global.paging.CommonSearch;

import java.util.List;

@Data
public class BoardConfigSearch extends CommonSearch {

    // 게시판 단일 & 목록 조회용
    private List<String> bid;
}