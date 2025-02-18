package org.anonymous.board.repositories;

import org.anonymous.board.entities.BoardRecommend;
import org.anonymous.board.entities.BoardRecommendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardRecommendRepository extends JpaRepository<BoardRecommend, BoardRecommendId>, QuerydslPredicateExecutor<BoardRecommend> {
}
