package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.*;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.BoardRecommendRepository;
import org.anonymous.board.repositories.BoardViewRepository;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardRecommendUpdateService {

    private final BoardDataRepository boardDataRepository;

    private final BoardRecommendRepository boardRecommendRepository;


    private final MemberUtil memberUtil;

    public long process(Long seq) {

        BoardData item = boardDataRepository.findById(seq).orElse(null);

        if (item == null) return 0L;

        try {
            BoardRecommend recommend = new BoardRecommend();

            recommend.setSeq(seq);
            recommend.setMemberSeq(memberUtil.getMember().getSeq());

            boardRecommendRepository.saveAndFlush(recommend);

        } catch (Exception e) {}

        // 조회수 업데이트 (BoardData.viewCount)

        QBoardRecommend boardRecommend = QBoardRecommend.boardRecommend;

        long total = boardRecommendRepository.count(boardRecommend.seq.eq(seq));

         item.setRecommendCount(total);

        boardDataRepository.saveAndFlush(item);

        return total;
    }
}
