package org.anonymous.board.repositories;

import org.anonymous.board.entities.CommentData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CommentDataRepository extends JpaRepository<CommentData, Long>, QuerydslPredicateExecutor<CommentData> {

    @EntityGraph(attributePaths = "data")
    List<CommentData> findAllByCreatedBy(String email);
}
