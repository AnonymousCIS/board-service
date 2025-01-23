package org.anonymous.board.exceptions;

import org.anonymous.global.exceptions.BadRequestException;

public class GuestPasswordCheckException extends BadRequestException {

    public GuestPasswordCheckException() {

        super("Required.guestPassword");

        setErrorCode(true);
    }
}