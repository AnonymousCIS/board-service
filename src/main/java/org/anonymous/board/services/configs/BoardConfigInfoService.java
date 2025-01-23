package org.anonymous.board.services.configs;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.BoardConfigSearch;
import org.anonymous.board.controllers.RequestBoardData;
import org.anonymous.board.entities.Config;
import org.anonymous.board.entities.QConfig;
import org.anonymous.board.exceptions.ConfigNotFoundException;
import org.anonymous.board.repositories.ConfigRepository;
import org.anonymous.global.paging.ListData;
import org.anonymous.global.paging.Pagination;
import org.anonymous.member.MemberUtil;
import org.anonymous.member.contants.Authority;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigInfoService {

    private final ConfigRepository configRepository;

    private final HttpServletRequest request;

    private final MemberUtil memberUtil;

    private final ModelMapper modelMapper;

    /**
     * 게시판 설정 단일 조회
     *
     * @param bid
     * @return
     */
    public Config get(String bid) {

        Config item = configRepository.findById(bid).orElseThrow(ConfigNotFoundException::new);

        addInfo(item);

        return item;
    }

    /**
     * 수정시에만 사용되는
     *
     * 커맨드 객체로 치환해 반환하는 편의 기능
     *
     * @param bid
     * @return
     */
    public RequestBoardData getForm(String bid) {

        Config item = get(bid);

        RequestBoardData form = modelMapper.map(item, RequestBoardData.class);

        form.setMode("edit");

        return form;
    }

    /**
     * 게시판 설정 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<Config> getList(BoardConfigSearch search) {

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();

        limit = limit < 1 ? 20 : limit;

        BooleanBuilder andBuilder = new BooleanBuilder();

        QConfig config = QConfig.config;

        /* 검색 처리 S */

        String sopt = search.getSopt();

        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";

        if (StringUtils.hasText(skey)) {
            StringExpression condition;

            // 게시판 아이디 검색
            if (sopt.equals("BID")) condition = config.bid;

            // 게시판명 검색
            else if (sopt.equals("NAME")) condition = config.name;

            // 통합 검색 (게시판 아이디 + 게시판명)
            else condition = config.bid.concat(config.name);

            andBuilder.and(condition.contains(skey.trim()));
        }

        List<String> bids = search.getBid();

        if (bids != null && !bids.isEmpty()) andBuilder.and(config.bid.in(bids));

        /* 검색 처리 E */

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));

        Page<Config> data = configRepository.findAll(andBuilder, pageable);

        List<Config> items = data.getContent();

        items.forEach(this::addInfo);

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request);

        return new ListData<>(items, pagination);
    }

    public void addInfo(Config item) {

        String category = item.getCategory();

        if (StringUtils.hasText(category)) {

            List<String> categories = Arrays.stream(category.split("\\n"))
                    .map(s -> s.replaceAll("\\r", ""))
                    .filter(s -> !s.isBlank())
                    .map(String::trim)
                    .toList();

            item.setCategories(categories);
        }

        /* listable, writable S */
        Authority listAuthority = item.getListAuthority();

        boolean listable = listAuthority == Authority.ALL || (listAuthority == Authority.USER && memberUtil.isLogin()) || (listAuthority == Authority.ADMIN && memberUtil.isAdmin());

        Authority writeAuthority = item.getWriteAuthority();

        boolean writeable = writeAuthority == Authority.ALL ||  (writeAuthority == Authority.USER && memberUtil.isLogin()) || (writeAuthority == Authority.ADMIN && memberUtil.isAdmin());

        item.setListable(listable);
        item.setWritable(writeable);
        /* listable, writable E */
    }
}