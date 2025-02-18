package org.anonymous.board.services;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.*;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.BoardRecommendRepository;
import org.anonymous.member.Member;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardRecommendService {

    private final BoardDataRepository boardDataRepository;

    private final BoardRecommendRepository boardRecommendRepository;

    private final MemberUtil memberUtil;

    /**
     * 추천하기 || 추천취소
     *
     * @param seq
     */
    public long process(Long seq) {

        Long memberSeq = memberUtil.getMember().getSeq();

        BoardRecommendId boardRecommendId = new BoardRecommendId(seq, memberSeq);

        if (boardRecommendRepository.existsById(boardRecommendId)) { // 제거

            boardRecommendRepository.deleteById(boardRecommendId);

        } else { // 추가

            BoardRecommend recommend = new BoardRecommend();

            recommend.setSeq(seq);

            recommend.setMemberSeq(memberSeq);

            boardRecommendRepository.saveAndFlush(recommend);
        }

        boardRecommendRepository.flush();

        return addInfo(seq);
    }

    /**
     * 내가 추천한 게시글 목록 조회
     *
     * @return
     */
    public List<Long> getMyRecommends() {
        if (!memberUtil.isLogin()) return null;
        Member member = memberUtil.getMember();
        Long seq = member.getSeq();

        QBoardRecommend recommend = QBoardRecommend.boardRecommend;

        List<BoardRecommend> recommends = (List<BoardRecommend>) boardRecommendRepository.findAll(recommend.memberSeq.eq(seq));

        if (recommends != null) {
            return recommends.stream().map(BoardRecommend::getSeq).toList();
        }

        return null;
    }

    /**
     * 추천 게시글 모두 추천 해제
     *
     * @param seqs
     */
    public void processAll (List<Long> seqs) {
        if (!memberUtil.isLogin()) return;

        BooleanBuilder builder = new BooleanBuilder();
        QBoardRecommend recommend = QBoardRecommend.boardRecommend;

        if (!memberUtil.isAdmin()) {
            builder.and(recommend.memberSeq.eq(memberUtil.getMember().getSeq()));
        }

        builder.and(recommend.seq.in(seqs));
        List<BoardRecommend> recommends = (List<BoardRecommend>) boardRecommendRepository.findAll(builder);
        boardRecommendRepository.deleteAll(recommends);
        boardRecommendRepository.flush();


        seqs.forEach(this::addInfo);
        /*
        QBoardRecommend recommend = QBoardRecommend.boardRecommend;

        List<BoardRecommend> recommends = (List<BoardRecommend>) boardRecommendRepository.findAll(recommend.seq.eq(seq));

        boardRecommendRepository.deleteAll(recommends);

        boardRecommendRepository.flush();
        */

    }

    /**
     * 추천수 반영 공통 처리
     *
     * @param seq
     * @return
     */
    public long addInfo(Long seq) {

        BoardData item = boardDataRepository.findById(seq).orElse(null);

        if (item == null) throw new BoardDataNotFoundException();

        QBoardRecommend boardRecommend = QBoardRecommend.boardRecommend;

        long total = boardRecommendRepository.count(boardRecommend.seq.eq(seq));

        item.setRecommendCount(total);

        boardDataRepository.saveAndFlush(item);

        return total;
    }
}
