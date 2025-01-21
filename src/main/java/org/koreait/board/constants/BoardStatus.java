package org.koreait.board.constants;

public enum BoardStatus {
    ALL, // 모두 볼수 있음
    SECRET, // 작성자 & 관리자만 조회 가능 (비밀글)
    BLOCK, // 관리자만 조회 가능 (사실상 삭제)
}
