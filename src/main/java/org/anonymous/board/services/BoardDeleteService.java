package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.global.libs.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDeleteService {

    private final Utils utils;

    private final BoardInfoService infoService;

    private final BoardDataRepository boardDataRepository;

    private final RestTemplate restTemplate;

    /**
     * 게시글 단일 삭제 (일반 사용자)
     *
     * DB 에서 삭제 X
     * 현재 시간으로 DeletedAt 할당
     *
     * Base Method
     *
     * @param seq
     * @return
     */
    public BoardData userDelete(Long seq) {

        BoardData item = infoService.get(seq);

        if (item == null) throw new BoardDataNotFoundException();

        item.setDeletedAt(LocalDateTime.now());

        boardDataRepository.saveAndFlush(item);

        return item;
    }

    /**
     * 게시글 목록 삭제 (일반 사용자)
     *
     * DB 에서 삭제 X
     * 현재 시간으로 DeletedAt 할당
     *
     * @param seqs
     * @return
     */
    public List<BoardData> userDelete(List<Long> seqs) {

        List<BoardData> userDeleted = new ArrayList<>();

        for (Long seq : seqs) {

            BoardData item = userDelete(seq);

            if (item != null) {

                userDeleted.add(item);
            }
        }
        return userDeleted;
    }

    /**
     * 게시글 단일 삭제
     * DB 에서 삭제
     *
     * 관리자만 가능
     *
     * Base Method
     *
     * @param seq
     * @return
     */
    public BoardData delete(Long seq) {

        BoardData item = infoService.get(seq);

        if (item == null) throw new BoardDataNotFoundException();

        /* 파일 삭제 처리 요청 S */

        HttpEntity<Void> request = new HttpEntity<>(utils.getRequestHeader());

        String apiUrl = utils.serviceUrl("file-service", "/deletes/" + item.getGid());

        restTemplate.exchange(URI.create(apiUrl), HttpMethod.DELETE, request, Void.class);

        /* 파일 삭제 처리 요청 E */

        boardDataRepository.delete(item);

        boardDataRepository.flush();

        // 비회원 인증 정보 삭제
        utils.deleteValue(utils.getUserHash() + "_board_" + seq);

        return item;
    }

    /**
     * 게시글 목록 삭제
     *
     * @param seqs
     * @return
     */
    public List<BoardData> delete(List<Long> seqs) {

        List<BoardData> deleted = new ArrayList<>();

        for (Long seq : seqs) {

            BoardData item = delete(seq);

            if (item != null) {

                deleted.add(item);
            }
        }
        return deleted;
    }
}