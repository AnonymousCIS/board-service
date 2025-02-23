package org.anonymous.board.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.anonymous.global.exceptions.UnAuthorizedException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
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
    private final ObjectMapper om;

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

        // BLOCK 으로 변경요청시 관리자가 아니면 권한 없음 예외
        if (status.equals(DomainStatus.BLOCK) && !memberUtil.isAdmin()) throw new UnAuthorizedException();

        T data = null;

        if (mode.equals("board")) {

            BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

            // 게시글의 현재 DomainStatus 가 BLOCK && !isAdmin 이면 권한 없음 예외
            // 즉 ALL 이든 SECRET 이든 UNBLOCK 하는 경우
            if (boardData.getStatus().equals(DomainStatus.BLOCK) && !memberUtil.isAdmin()) throw new UnAuthorizedException();

            boardData.setStatus(status);

            boardDataRepository.saveAndFlush(boardData);

            data = (T) boardData;

        } else if (mode.equals("comment")) {

            CommentData commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

            // 댓글의 현재 DomainStatus 가 BLOCK && !isAdmin 이면 권한 없음 예외
            // 즉 ALL 이든 SECRET 이든 UNBLOCK 하는 경우
            if (commentData.getStatus().equals(DomainStatus.BLOCK) && !memberUtil.isAdmin()) throw new UnAuthorizedException();

            commentData.setStatus(status);

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

            try {
                String token = utils.getAuthToken();
                System.out.println("token: " + token);
                // String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMDFAdGVzdC5vcmciLCJhdXRob3JpdGllcyI6IkFETUlOfHxVU0VSIiwiZXhwIjoxNzM4MjQ1NDQ4fQ.1B2lwAevrJWAbTFEO9ZtQlYmJpRRNSZJF8syn09Y54LUoXAtDRScgLQhoQ9wOsVpGy89p990uf8tl_DswYOK1Q";t

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (StringUtils.hasText(token)) headers.setBearerAuth(token);
                String params = om.writeValueAsString(form);
                System.out.println("params: " + params);
                HttpEntity<String> request = new HttpEntity<>(params, headers);

                String apiUrl = utils.serviceUrl("member-service", "/admin/status");

                ResponseEntity<JSONData> item = restTemplate.exchange(apiUrl, HttpMethod.PATCH, request, JSONData.class);

            } catch (Exception e) {

                e.printStackTrace();
            }
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

    /**
     * 회원 이메일로 모든 컨텐츠 일괄 BLOCK 처리
     *
     * @param email
     */
    public void process(String email) {

        List<BoardData> boardItems = boardDataRepository.findAllByCreatedBy(email);

        for (BoardData item : boardItems) {

            process(item.getSeq(), DomainStatus.BLOCK, "board");
        }

        List<CommentData> commentItems = commentDataRepository.findAllByCreatedBy(email);

        for (CommentData item : commentItems) {

            process(item.getSeq(), DomainStatus.BLOCK, "comment");
        }
    }
}

