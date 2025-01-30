package org.anonymous.board.validators;

import lombok.RequiredArgsConstructor;
import org.anonymous.board.controllers.RequestBoardData;
import org.anonymous.board.entities.BoardData;
import org.anonymous.board.repositories.BoardDataRepository;
import org.anonymous.board.repositories.CommentDataRepository;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.validators.PasswordValidator;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor
public class BoardDataValidator implements Validator, PasswordValidator {

    private final Utils utils;

    private final MemberUtil memberUtil;

    private final PasswordEncoder passwordEncoder;

    private final BoardDataRepository boardDataRepository;

    private final CommentDataRepository commentDataRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestBoardData.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestBoardData form = (RequestBoardData) target;

        // 비회원 비밀번호 검증
        if (!memberUtil.isLogin()) {

            // 필수 항목 검증
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "guestPw", "NotBlank");

            // 대소문자 구분없는 알파벳 1자 이상, 숫자 1자 이상 포함
            String guestPw = form.getGuestPw();

            if (StringUtils.hasText(guestPw) && (!alphaCheck(guestPw, true) || !numberCheck(guestPw))) {

                errors.rejectValue("guestPw", "Complexity");
            }
        }
        // 수정일때 게시글 번호(seq) 필수 항목

        String mode = form.getMode();

        Long seq = form.getSeq();

        if (mode != null && mode.equals("edit") && (seq == null || seq < 1L)) {

            errors.rejectValue("seq", "NotNull");
        }
    }

    /**
     * 비회원 비밀번호 체크
     *
     * @param password
     * @param seq
     * @return
     */
    public boolean checkGuestPassword(String password, Long seq) {

        if (seq == null) return false;

        BoardData item = boardDataRepository.findById(seq).orElse(null);

        if (item != null && StringUtils.hasText(item.getGuestPw())) {
            return passwordEncoder.matches(password, item.getGuestPw());
        }

        return false;
    }

    /**
     * 게시글 삭제 가능 여부 체크
     *  - 댓글이 존재하면 삭제 불가
     * @param seq

    public void checkDelete(Long seq) {

        QCommentData commentData = QCommentData.commentData;

        if (commentDataRepository.count(commentData.data.seq.eq(seq)) > 0L) {
            throw new BadRequestException(utils.getMessage("Exist.comment"));
        }
    }
    */
}