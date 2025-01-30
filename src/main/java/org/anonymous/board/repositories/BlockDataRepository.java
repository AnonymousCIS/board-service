package org.anonymous.board.repositories;

import org.anonymous.board.entities.BlockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface BlockDataRepository extends JpaRepository<BlockData, Long>, QuerydslPredicateExecutor<BlockData> {

    List<BlockData> findByEmail(String email);
}
