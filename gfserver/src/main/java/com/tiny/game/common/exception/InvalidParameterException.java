package com.tiny.game.common.exception;

import com.tiny.game.common.GameConst;

public class InvalidParameterException extends GameRuntimeException{
 
	public InvalidParameterException() {
        super(GameConst.Error_InvalidRequestParameter);
    }

    public InvalidParameterException(String message) {
        super(GameConst.Error_InvalidRequestParameter, message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(GameConst.Error_InvalidRequestParameter, message, cause);
    }

    public InvalidParameterException(Throwable cause) {
        super(GameConst.Error_InvalidRequestParameter, cause);
    }
}
