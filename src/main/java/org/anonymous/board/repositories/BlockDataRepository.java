package org.anonymous.board.repositories;

import org.anonymous.board.entities.BlockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BlockDataRepository extends JpaRepository<BlockData, Long>, QuerydslPredicateExecutor<BlockData> {
}
