package org.anonymous.board.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestBoardData;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.entities.Config;
import org.anonymous.board.exceptions.BoardDataNotFoundException;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.services.configs.BoardConfigInfoService;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@Lazy
@Service
@Transactional // Board Entity 영속성 보장 위해 추가, 특히 OneToMany 때에는 필수
@RequiredArgsConstructor
public class BoardUpdateService {

    private final BoardConfigInfoService configInfoService;
    
    private final BoardDataRepository boardDataRepository;

    private final MemberUtil memberUtil;

    private final PasswordEncoder passwordEncoder;

    private final HttpServletRequest request;

    private final RestTemplate restTemplate;

    private final Utils utils;

    public BoardData process(RequestBoardData form) {

        Long seq = Objects.requireNonNullElse(form.getSeq(), 0L);

        String mode = Objects.requireNonNullElse(form.getMode(), "write");

        BoardData data = null;

        if (mode.equals("edit")) { // 게시글 수정

            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        } else { // 게시글 작성

            /**
             * 신규 게시글 등록될때만 최초 한번 기록되는 데이터
             * - 게시판 설정, 회원
             * - gid
             * - 작성자 ip 정보, UserAgent (Browser 정보)
             */

            Config config = configInfoService.get(form.getBid());

            data = new BoardData();

            data.setConfig(config);
            data.setGid(form.getGid());
            data.setIpAddr(request.getRemoteAddr());
            data.setUserAgent(request.getHeader("User-Agent"));
        }

        String guestPw = form.getGuestPw();

        /* 게시글 작성 & 수정 공통 반영 사항 S */

        // 비회원 비밀번호가 있는 경우 해시화
        // 게시글 작성 & 수정 모두 가능하기때문에 if문 밖에
        if (StringUtils.hasText(guestPw)) data.setGuestPw(passwordEncoder.encode(guestPw));

        data.setPoster(form.getPoster());

        // 공지사항 여부는 관리자만 반영 가능
        if (memberUtil.isAdmin()) data.setNotice(form.isNotice());

        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setExternalLink(form.getExternalLink());
        data.setYoutubeUrl(form.getYoutubeUrl());
        data.setCategory(form.getCategory());
        data.setBoardStatus(form.getStatus());

        boardDataRepository.saveAndFlush(data);

        /* 게시글 파일 첨부 작업 완료(Done) 처리 S */

        HttpEntity<Void> request = new HttpEntity<>(utils.getRequestHeader());

        String apiUrl = utils.serviceUrl("file-service", "/done/" + data.getGid());

        restTemplate.exchange(URI.create(apiUrl), HttpMethod.GET, request, Void.class);

        /* 게시글 파일 첨부 작업 완료(Done) 처리 E */

        // 비회원 게시글 인증 정보 삭제
        utils.deleteValue(utils.getUserHash() + "_board_" + seq);

        return data;

        /* 게시글 작성 & 수정 공통 반영 사항 E */
    }
}