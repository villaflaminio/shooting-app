package org.rconfalonieri.nzuardi.shootingapp.exception;

public class UserException extends RuntimeException {

    public UserException(userExceptionCode message) {
        super(message.toString());
    }

    public enum userExceptionCode {
        PARAMETER_NULL,
        IDTESSERINO_NOT_EXIST,
        USER_ALREADY_EXISTS,
        AUTHORITY_NOT_EXIST,
        USER_NOT_LOGGED_IN,
        USER_NOT_FOUND,
        EMAIL_NOT_FOUND,
        AUTHORITY_UNREACHABALE
    }

}
