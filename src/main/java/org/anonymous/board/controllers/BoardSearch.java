package org.anonymous.board.controllers;

import lombok.Data;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.global.paging.CommonSearch;

import java.util.List;

@Data
public class BoardSearch extends CommonSearch {
    // 게시판 단일 & 목록 조회용
    private List<String> bid;

    // 필드명_정렬 방향, 검색 처리시 분해해서 사용 예정
    // EX) viewCount_DESC
    private String sort;

    // 회원 이메일별 조회용
    // 관리자쪽에서 사용
    private List<String> email;

    // 분류 조회용
    private List<String> category;

    // 상태별 게시글 조회용
    // 관리자쪽에서 사용
    private List<DomainStatus> status;
}