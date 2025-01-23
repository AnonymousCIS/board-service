package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.BoardView;
import org.anonymous.board.entities.QBoardView;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.BoardViewRepository;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 조회수 반영 기능
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class BoardViewUpdateService {

   private final BoardDataRepository boardDataRepository;

   private final BoardViewRepository boardViewRepository;

   private final MemberUtil memberUtil;

   private final Utils utils;

   public long process(Long seq) {

       BoardData item = boardDataRepository.findById(seq).orElse(null);

       if (item == null) return 0L;

       // 이렇게 하면 쿠키를 지우면 계속 조회수 증가하므로 잘못된 로직
       // int hash = memberUtil.isLogin() ? Objects.hash(memberUtil.getMember().getSeq()) : Integer.parseInt(utils.getUserHash());

       try {
           BoardView view = new BoardView();

           view.setSeq(seq);
           view.setHash(utils.getMemberHash());

           boardViewRepository.saveAndFlush(view);

       } catch (Exception e) {}

       // 조회수 업데이트 (BoardData.viewCount)
       QBoardView boardView = QBoardView.boardView;

       long total = boardViewRepository.count(boardView.seq.eq(seq));

       item.setViewCount(total);

       boardDataRepository.saveAndFlush(item);

       return total;
   }
}