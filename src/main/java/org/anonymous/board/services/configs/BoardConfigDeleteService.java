package org.anonymous.board.services.configs;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.entities.Config;
import org.anonymous.board.exceptions.ConfigNotFoundException;
import org.anonymous.board.repositories.ConfigRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class BoardConfigDeleteService {

    private final ConfigRepository configRepository;

    /**
     * 게시판 단일 삭제
     *
     * Base Method
     *
     * @param bid
     * @return
     */
    public Config process(String bid) {

        Config config = configRepository.findById(bid).orElseThrow(ConfigNotFoundException::new);

        if (config == null) {

            configRepository.delete(config);

            configRepository.flush();
        }

        return config;
    }

    /**
     * 게시판 목록 삭제
     *
     * @param bids
     * @return
     */
    public List<Config> process(List<String> bids) {

        List<Config> deleted = new ArrayList<>();

        for (String bid : bids) {

            Config item = process(bid);

            if (item != null) {

                // 삭제된 게시판 정보
                deleted.add(item);
            }
        }

        return deleted;
    }
}
