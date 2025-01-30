package org.anonymous.board.constants;

public enum DomainStatus {

    ALL, // 모두 조회 가능
    
    SECRET, // 작성자 & 관리자만 조회 가능 (비밀글)
    
    BLOCK, // 관리자가 차단, 관리자만 조회 가능
}