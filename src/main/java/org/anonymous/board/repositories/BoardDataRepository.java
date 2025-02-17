package org.anonymous.board.repositories;

import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.QBoardData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface BoardDataRepository extends JpaRepository<BoardData, Long>, QuerydslPredicateExecutor<BoardData> {

    @EntityGraph(attributePaths = "config")
    Optional<BoardData> findBySeq(Long seq);

    @EntityGraph(attributePaths = "config")
    List<BoardData> findAllByCreatedBy(String email);

    default long getTotal(String bid) {
        QBoardData boardData = QBoardData.boardData;

        return count(boardData.config.bid.eq(bid));
    }
}
