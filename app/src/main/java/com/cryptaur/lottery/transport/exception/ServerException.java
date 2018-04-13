package com.cryptaur.lottery.transport.exception;

import java.io.IOException;

public class ServerException extends IOException {
    public final int errorCode;
    public final String errorMessage;

    public ServerException(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
