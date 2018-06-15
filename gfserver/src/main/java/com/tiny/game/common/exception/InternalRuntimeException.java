package com.tiny.game.common.exception;

import com.tiny.game.common.GameConst;

public class InternalRuntimeException extends GameRuntimeException {

	private static final long serialVersionUID = 4596574126406515037L;

	public InternalRuntimeException() {
        super(GameConst.Error_InternalRuntime);
    }

    public InternalRuntimeException(String message) {
        super(GameConst.Error_InternalRuntime, message);
    }

    public InternalRuntimeException(String message, Throwable cause) {
        super(GameConst.Error_InternalRuntime, message, cause);
    }

    public InternalRuntimeException(Throwable cause) {
        super(GameConst.Error_InternalRuntime, cause);
    }
	
}
