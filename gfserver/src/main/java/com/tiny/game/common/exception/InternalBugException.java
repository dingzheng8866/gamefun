package com.tiny.game.common.exception;

import com.tiny.game.common.GameConst;

public class InternalBugException extends GameRuntimeException {

	private static final long serialVersionUID = 1341334491703352543L;

	public InternalBugException() {
        super(GameConst.Error_InternalBug);
    }

    public InternalBugException(String message) {
        super(GameConst.Error_InternalBug, message);
    }

    public InternalBugException(String message, Throwable cause) {
        super(GameConst.Error_InternalBug, message, cause);
    }

    public InternalBugException(Throwable cause) {
        super(GameConst.Error_InternalBug, cause);
    }
	
}
