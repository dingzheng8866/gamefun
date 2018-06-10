package com.tiny.game.common.exception;

import com.tiny.game.common.error.ErrorCode;

public class InvalidRequestParameter extends RuntimeException {

	private ErrorCode errorCode;
	
	public ErrorCode getErrorCode(){
		return errorCode;
	}
	
	public InvalidRequestParameter(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public InvalidRequestParameter(ErrorCode errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InvalidRequestParameter(ErrorCode errorCode,String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public InvalidRequestParameter(ErrorCode errorCode,Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
	
}
