package com.aliyun.openservices.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an element as an SDK extension point: intended for implementors who
 * extend the SDK (template methods, SPI-style interfaces), not for end users
 * of the client API.
 *
 * <p>Elements annotated with {@code @ProtectedApi} have a weaker compatibility
 * guarantee than the public client API and may change between minor releases.
 * If you override or implement them, review the release notes when upgrading.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface ProtectedApi {
}
