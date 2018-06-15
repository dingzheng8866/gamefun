package com.tiny.game.common.server;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

public class ServerTimer {
	private Timer mTimer;

	private static class SingletonHolder {
		private static ServerTimer instance = new ServerTimer();
	}

	public static ServerTimer getInstance() {
		return SingletonHolder.instance;
	}

	private ServerTimer() {
		mTimer = new HashedWheelTimer(1, TimeUnit.SECONDS, 600);
	}

	/**
	 * 以秒为单位，加入定时运算
	 * 
	 * @param task
	 * @param delay
	 * @return
	 */
	public Timeout newTimeout(TimerTask task, long delay) {
		return mTimer.newTimeout(task, delay, TimeUnit.SECONDS);
	}
	
	public Timeout newTimeoutMilliseconds(TimerTask task, int delay) {
		return mTimer.newTimeout(task, delay, TimeUnit.MILLISECONDS);
	}

	public Set<Timeout> stop() {
		return mTimer.stop();
	}

	public void shutdown() {
		if (mTimer != null) {
			mTimer.stop();
		}
	}

}
