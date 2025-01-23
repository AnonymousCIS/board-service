package org.anonymous.board.exceptions;

import org.anonymous.global.exceptions.CommonException;
import org.springframework.http.HttpStatus;

public class ConfigNotFoundException extends CommonException {

    public ConfigNotFoundException() {

        super("NotFound.board", HttpStatus.NOT_FOUND);

        setErrorCode(true);
    }
}