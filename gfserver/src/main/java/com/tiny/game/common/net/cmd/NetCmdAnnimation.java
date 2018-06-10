package com.tiny.game.common.net.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetCmdAnnimation {

	public Class<?> cmd();
	
	public boolean enable() default true;
	
}