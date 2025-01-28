package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.BoardStatus;
import org.anonymous.board.entities.BlockData;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardStatusService {

    private final Utils utils;

    private final RestTemplate restTemplate;

    private final BoardDataRepository boardDataRepository;

    private final CommentDataRepository commentDataRepository;

    /**
     * 게시글 & 댓글 상태 단일 변경
     *
     * Base Method
     * @param seq
     * @param status
     * @param mode
     */
    public <T> T process(Long seq, BoardStatus status, String mode) {

        // 잘못된 요청
        if ((!mode.equals("board") && !mode.equals("comment")) || seq == null || status == null) throw new BadRequestException();

        T data = null;

        if (mode.equals("board")) {

            BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

            boardData.setBoardStatus(status);

            boardDataRepository.saveAndFlush(boardData);

            data = (T) boardData;

        } else if (mode.equals("comment")) {

            CommentData commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

            commentData.setBoardStatus(status);

            commentDataRepository.saveAndFlush(commentData);

            data = (T) commentData;
        }

        /* Member 도메인에게 차단 게시글/댓글 정보 등록 요청 S */

        if (status.equals(BoardStatus.BLOCK)) {

            BlockData form = new BlockData();

            Long blockContentSeq = null;

            String email = "";

            if (data instanceof BoardData) {

                blockContentSeq = ((BoardData) data).getSeq();

                email = ((BoardData) data).getCreatedBy();

            } else if (data instanceof CommentData) {

                blockContentSeq = ((CommentData) data).getSeq();

                email = ((CommentData) data).getCreatedBy();
            }

            form.setStatus(BoardStatus.BLOCK);
            form.setType(mode);
            form.setSeq(blockContentSeq);
            form.setEmail(email);

            // 근데 이거 항상 BLOCK 이라서 안넘겨줘도 될 것 같은데??
            // 차단 컨텐츠 이외의 비밀 컨텐츠도 멤버가 받을 것인지 물어보기
            form.setStatus(status);

            String token = utils.getAuthToken();

            HttpHeaders headers = new HttpHeaders();

            if (StringUtils.hasText(token)) headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);

            String apiUrl = utils.serviceUrl("member-service", "/admin/status/" + form);

            ResponseEntity<String> item = restTemplate.exchange(apiUrl, HttpMethod.PATCH, request, String.class);
        }
        /* Member 도메인에게 차단 게시글/댓글 정보 등록 요청 E */

        return data;
    }

    /**
     * 게시글 & 댓글 상태 목록 변경
     *
     * @param seqs
     * @param status
     * @param mode
     */
    public <T> List<T> process(List<Long> seqs, BoardStatus status, String mode) {

        List<T> processed = new ArrayList<>();

        for (Long seq : seqs) {

            T data = process(seq, status, mode);

            if (data != null) processed.add(data);
        }

        /* 여기서 Member 도메인에게 목록 /admin/statues/로 넘겨야하나? 아니면 단일쪽에서 다 처리되도록..? */

        return processed;
    }
}

