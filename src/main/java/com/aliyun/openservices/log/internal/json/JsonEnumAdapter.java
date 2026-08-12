package com.aliyun.openservices.log.internal.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.aliyun.openservices.log.annotation.InternalApi;

/** Explicitly enables the SDK's annotation-driven JSON mapping for an enum. */
@InternalApi
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonEnumAdapter {
}
