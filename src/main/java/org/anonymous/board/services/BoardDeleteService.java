package org.anonymous.board.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.global.libs.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardDeleteService {

    private final BoardInfoService infoService;

    private final BoardDataRepository boardDataRepository;

    private final RestTemplate restTemplate;

    private final Utils utils;

    public void delete(Long seq) {

        BoardData item = infoService.get(seq);

        String gid = item.getGid();

        /* 파일 삭제 처리 요청 S */

        HttpEntity<Void> request = new HttpEntity<>(utils.getRequestHeader());

        String apiUrl = utils.serviceUrl("file-service", "/deletes/" + item.getGid());

        restTemplate.exchange(URI.create(apiUrl), HttpMethod.DELETE, request, Void.class);

        /* 파일 삭제 처리 요청 E */


        boardDataRepository.delete(item);
        boardDataRepository.flush();

        // 비회원 인증 정보 삭제
        utils.deleteValue(utils.getUserHash() + "_board_" + seq);
    }
}