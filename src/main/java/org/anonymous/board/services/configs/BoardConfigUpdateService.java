package org.anonymous.board.services.configs;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestConfig;
import org.anonymous.board.entities.Config;
import org.anonymous.board.repositories.ConfigRepository;
import org.anonymous.global.libs.Utils;
import org.anonymous.member.contants.Authority;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigUpdateService {
    private final ConfigRepository configRepository;
    private final Utils utils;

    public Config process(RequestConfig form) {

        String bid = form.getBid();

        Config config = configRepository.findById(bid).orElseGet(Config::new);

        addInfo(config, form);

        configRepository.saveAndFlush(config);

        return config;
    }

    public List<Config> process(List<RequestConfig> items) {

        if (items == null && items.isEmpty()) return null;

        List<Config> processed = new ArrayList<>();

        for (RequestConfig form : items) {

            Config item = configRepository.findById(form.getBid()).orElseGet(Config::new);

            addInfo(item, form);

            processed.add(item);
        }

        configRepository.saveAllAndFlush(processed);

        return processed;
    }

    private void addInfo(Config config, RequestConfig form) {

        config.setBid(form.getBid());
        config.setName(form.getName());
        config.setOpen(form.isOpen());
        config.setCategory(form.getCategory());
        config.setRowsPerPage(form.getRowsPerPage() < 1 ? 20 : form.getRowsPerPage());
        config.setPageRanges(form.getPageRanges() < 1 ? 10 : form.getPageRanges());
        config.setPageRangesMobile(form.getPageRangesMobile() < 1 ? 5 : form.getPageRangesMobile());
        config.setUseEditor(form.isUseEditor());
        config.setUseEditorImage(form.isUseEditorImage());
        config.setUseAttachFile(form.isUseAttachFile());
        config.setUseComment(form.isUseComment());
        config.setSkin(StringUtils.hasText(form.getSkin()) ? form.getSkin() : "default");
        config.setListAuthority(Objects.requireNonNullElse(form.getListAuthority(), Authority.ALL));
        config.setViewAuthority(Objects.requireNonNullElse(form.getViewAuthority(), Authority.ALL));
        config.setWriteAuthority(Objects.requireNonNullElse(form.getWriteAuthority(), Authority.ALL));
        config.setCommentAuthority(Objects.requireNonNullElse(form.getCommentAuthority(), Authority.ALL));

        String locationAfterWriting = form.getLocationAfterWriting();

        config.setLocationAfterWriting(StringUtils.hasText(locationAfterWriting) ? locationAfterWriting : "list");

        config.setListUnderView(form.isListUnderView());
    }
}