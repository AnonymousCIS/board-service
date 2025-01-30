package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.constants.DomainStatus;
import org.anonymous.board.entities.BlockData;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.CommentData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.exceptions.CommentNotFoundException;
import org.anonymous.board.repositories.BlockDataRepository;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.MemberUtil;
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

    private final MemberUtil memberUtil;

    private final RestTemplate restTemplate;

    private final BlockDataRepository blockDataRepository;

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
    public <T> T process(Long seq, DomainStatus status, String mode) {

        // 잘못된 요청
        if ((!mode.equals("board") && !mode.equals("comment")) || seq == null || status == null) throw new BadRequestException();

        // if (!memberUtil.isAdmin()) throw new UnAuthorizedException();

        T data = null;

        if (mode.equals("board")) {

            BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

            // 게시글 찾아서 현태 스테이터스가 BLOCK이고 && !isAdmin이면 UNat머시기 권한

            boardData.setDomainStatus(status);

            boardDataRepository.saveAndFlush(boardData);

            data = (T) boardData;

        } else if (mode.equals("comment")) {

            CommentData commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

            commentData.setDomainStatus(status);

            commentDataRepository.saveAndFlush(commentData);

            data = (T) commentData;
        }

        /* Member 도메인에게 차단 게시글/댓글 정보 등록 요청 S */

        if (status.equals(DomainStatus.BLOCK)) {

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

            form.setStatus(DomainStatus.BLOCK);
            form.setType(mode);
            form.setSeq(blockContentSeq);
            form.setEmail(email);
            form.setStatus(status);

            String token = utils.getAuthToken();

            HttpHeaders headers = new HttpHeaders();

            if (StringUtils.hasText(token)) headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);

            String apiUrl = utils.serviceUrl("member-service", "/admin/status/" + form);

            ResponseEntity<String> item = restTemplate.exchange(apiUrl, HttpMethod.PATCH, request, String.class);

            blockDataRepository.saveAndFlush(form);
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
    public <T> List<T> process(List<Long> seqs, DomainStatus status, String mode) {

        List<T> processed = new ArrayList<>();

        for (Long seq : seqs) {

            T data = process(seq, status, mode);

            if (data != null) processed.add(data);
        }

        return processed;
    }
}

