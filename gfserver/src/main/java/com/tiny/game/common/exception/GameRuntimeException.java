package com.tiny.game.common.exception;

public class GameRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 736910993812481568L;
	protected int errorCode;
	
	public int getErrorCode(){
		return errorCode;
	}
	
	public GameRuntimeException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public GameRuntimeException(int errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GameRuntimeException(int errorCode,String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GameRuntimeException(int errorCode,Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
	
}
