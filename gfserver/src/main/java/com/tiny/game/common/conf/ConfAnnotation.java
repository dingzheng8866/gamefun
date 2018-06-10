package com.tiny.game.common.conf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfAnnotation {

	public Class<?> confClass();
	
	public String path();
	
	public boolean enable() default true;
	
}

