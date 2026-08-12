package com.aliyun.openservices.log.internal.json;

import com.aliyun.openservices.log.annotation.InternalApi;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Architecture guard: every public/protected member whose signature references
 * an SDK-internal or JSON-implementation type must be annotated with
 * {@link InternalApi}, so that the exposure is always explicit and new
 * accidental exposure fails CI.
 */
public class ApiExposureGuardTest {

    private static final String BASE_PACKAGE = "com.aliyun.openservices.log.";
    private static final String[] UNSUPPORTED_API_TYPE_PREFIXES = {
            "com.aliyun.openservices.log.internal.",
            "com.google.gson.",
            "com.alibaba.fastjson."
    };

    @Test
    public void allInternalImplementationTypeExposuresAreAnnotated() throws IOException {
        Path classesDir = Paths.get("target", "classes");
        assertTrue("run 'mvn compile' first: " + classesDir.toAbsolutePath(),
                Files.isDirectory(classesDir));

        List<String> violations = new ArrayList<String>();
        for (String className : collectClassNames(classesDir)) {
            if (!className.startsWith(BASE_PACKAGE)
                    || className.startsWith(BASE_PACKAGE + "internal.")
                    || className.startsWith(BASE_PACKAGE + "sample.")) {
                continue;
            }
            Class<?> cls;
            try {
                cls = Class.forName(className, false, getClass().getClassLoader());
            } catch (Throwable t) {
                continue;
            }
            if (cls.isSynthetic() || cls.isAnonymousClass() || cls.isLocalClass()) {
                continue;
            }
            if (cls.getAnnotation(InternalApi.class) != null) {
                continue;
            }
            checkMembers(cls, violations);
        }

        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append(violations.size())
                    .append(" public/protected member(s) expose SDK-internal or ")
                    .append("JSON-implementation types without @InternalApi:\n");
            for (String violation : violations) {
                message.append("  ").append(violation).append('\n');
            }
            fail(message.toString());
        }
    }

    @Test
    public void annotationIsRuntimeVisible() {
        assertFalse(InternalApi.class.getAnnotation(java.lang.annotation.Retention.class)
                .value() != java.lang.annotation.RetentionPolicy.RUNTIME);
    }

    private static void checkMembers(Class<?> cls, List<String> violations) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isSynthetic() || method.isBridge()
                    || !isPublicOrProtected(method.getModifiers())) {
                continue;
            }
            if (referencesUnsupportedType(method.getGenericReturnType())
                    || referencesUnsupportedType(method.getGenericParameterTypes())) {
                if (method.getAnnotation(InternalApi.class) == null) {
                    violations.add(cls.getName() + "#" + method.getName()
                            + describeParams(method.getGenericParameterTypes()));
                }
            }
        }
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            if (constructor.isSynthetic() || !isPublicOrProtected(constructor.getModifiers())) {
                continue;
            }
            if (referencesUnsupportedType(constructor.getGenericParameterTypes())) {
                if (constructor.getAnnotation(InternalApi.class) == null) {
                    violations.add(cls.getName() + "#<init>"
                            + describeParams(constructor.getGenericParameterTypes()));
                }
            }
        }
        for (Field field : cls.getDeclaredFields()) {
            if (field.isSynthetic() || !isPublicOrProtected(field.getModifiers())) {
                continue;
            }
            if (referencesUnsupportedType(field.getGenericType())) {
                if (field.getAnnotation(InternalApi.class) == null) {
                    violations.add(cls.getName() + "." + field.getName());
                }
            }
        }
    }

    private static boolean isPublicOrProtected(int modifiers) {
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
    }

    private static boolean referencesUnsupportedType(Type... types) {
        for (Type type : types) {
            if (type == null) {
                continue;
            }
            String typeName = type.getTypeName();
            for (String prefix : UNSUPPORTED_API_TYPE_PREFIXES) {
                if (typeName.contains(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String describeParams(Type[] types) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            String name = types[i].getTypeName();
            builder.append(name.substring(name.lastIndexOf('.') + 1));
        }
        return builder.append(')').toString();
    }

    private static List<String> collectClassNames(final Path classesDir) throws IOException {
        final List<String> classNames = new ArrayList<String>();
        Files.walkFileTree(classesDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String relative = classesDir.relativize(file).toString();
                if (relative.endsWith(".class")) {
                    classNames.add(relative
                            .substring(0, relative.length() - ".class".length())
                            .replace(java.io.File.separatorChar, '.'));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classNames;
    }
}
