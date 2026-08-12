package com.aliyun.openservices.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an element as SDK-internal. Elements annotated with {@code @InternalApi}
 * are implementation details of this SDK: they may be changed, moved or removed
 * in any release without notice, and no compatibility guarantee is provided.
 *
 * <p>User code must not depend on them. Use the documented public entry points
 * instead (e.g. {@code toJsonString()} / {@code fromJsonString(String)} and
 * getters/setters on model classes).</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface InternalApi {
}
