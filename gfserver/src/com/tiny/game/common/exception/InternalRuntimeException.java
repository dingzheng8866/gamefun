package com.tiny.game.common.exception;

public class InternalRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4596574126406515037L;

	public InternalRuntimeException() {
        super();
    }

    public InternalRuntimeException(String message) {
        super(message);
    }

    public InternalRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalRuntimeException(Throwable cause) {
        super(cause);
    }
	
}
