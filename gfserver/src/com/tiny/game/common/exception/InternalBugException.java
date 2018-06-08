package com.tiny.game.common.exception;

public class InternalBugException extends RuntimeException {

	private static final long serialVersionUID = 1341334491703352543L;

	public InternalBugException() {
        super();
    }

    public InternalBugException(String message) {
        super(message);
    }

    public InternalBugException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalBugException(Throwable cause) {
        super(cause);
    }
	
}
