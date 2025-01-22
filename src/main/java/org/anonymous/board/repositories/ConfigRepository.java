package org.anonymous.board.repositories;

import org.anonymous.board.entities.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ConfigRepository extends JpaRepository<Config, String>, QuerydslPredicateExecutor<Config> {

    default boolean exists(String bid) {

        // QBoard board = QBoard.board;

        // return exists(board.bid.eq(bid));

        return false;
    }
}
