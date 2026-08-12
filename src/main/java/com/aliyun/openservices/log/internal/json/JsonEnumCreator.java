package com.aliyun.openservices.log.internal.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.aliyun.openservices.log.annotation.InternalApi;

/** Marks the static factory method that creates an enum from its JSON string value. */
@InternalApi
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonEnumCreator {
}
