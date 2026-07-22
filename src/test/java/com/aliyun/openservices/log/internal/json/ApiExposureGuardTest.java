package com.aliyun.openservices.log.internal.json;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.annotation.ProtectedApi;
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
 * the internal JSON shim types must be annotated with either
 * {@link InternalApi} or {@link ProtectedApi}, so that the exposure is always
 * explicit and new accidental exposure fails CI.
 */
public class ApiExposureGuardTest {

    private static final String SHIM_PACKAGE = "com.aliyun.openservices.log.internal.json.";
    private static final String BASE_PACKAGE = "com.aliyun.openservices.log.";

    @Test
    public void allShimExposuresAreAnnotated() throws IOException {
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
            if (isAnnotated(cls)) {
                continue;
            }
            checkMembers(cls, violations);
        }

        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append(violations.size())
                    .append(" public/protected member(s) expose ")
                    .append(SHIM_PACKAGE)
                    .append("* types without @InternalApi/@ProtectedApi:\n");
            for (String violation : violations) {
                message.append("  ").append(violation).append('\n');
            }
            fail(message.toString());
        }
    }

    @Test
    public void annotationsAreRuntimeVisible() {
        assertFalse(InternalApi.class.getAnnotation(java.lang.annotation.Retention.class)
                .value() != java.lang.annotation.RetentionPolicy.RUNTIME);
        assertFalse(ProtectedApi.class.getAnnotation(java.lang.annotation.Retention.class)
                .value() != java.lang.annotation.RetentionPolicy.RUNTIME);
    }

    private static void checkMembers(Class<?> cls, List<String> violations) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isSynthetic() || method.isBridge()
                    || !isPublicOrProtected(method.getModifiers())) {
                continue;
            }
            if (referencesShim(method.getGenericReturnType())
                    || referencesShim(method.getGenericParameterTypes())) {
                if (!isAnnotated(method.getAnnotation(InternalApi.class),
                        method.getAnnotation(ProtectedApi.class))) {
                    violations.add(cls.getName() + "#" + method.getName()
                            + describeParams(method.getGenericParameterTypes()));
                }
            }
        }
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            if (constructor.isSynthetic() || !isPublicOrProtected(constructor.getModifiers())) {
                continue;
            }
            if (referencesShim(constructor.getGenericParameterTypes())) {
                if (!isAnnotated(constructor.getAnnotation(InternalApi.class),
                        constructor.getAnnotation(ProtectedApi.class))) {
                    violations.add(cls.getName() + "#<init>"
                            + describeParams(constructor.getGenericParameterTypes()));
                }
            }
        }
        for (Field field : cls.getDeclaredFields()) {
            if (field.isSynthetic() || !isPublicOrProtected(field.getModifiers())) {
                continue;
            }
            if (referencesShim(field.getGenericType())) {
                if (!isAnnotated(field.getAnnotation(InternalApi.class),
                        field.getAnnotation(ProtectedApi.class))) {
                    violations.add(cls.getName() + "." + field.getName());
                }
            }
        }
    }

    private static boolean isPublicOrProtected(int modifiers) {
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
    }

    private static boolean isAnnotated(Class<?> cls) {
        return cls.getAnnotation(InternalApi.class) != null
                || cls.getAnnotation(ProtectedApi.class) != null;
    }

    private static boolean isAnnotated(InternalApi internal, ProtectedApi extension) {
        return internal != null || extension != null;
    }

    private static boolean referencesShim(Type... types) {
        for (Type type : types) {
            if (type != null && type.getTypeName().contains(SHIM_PACKAGE)) {
                return true;
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
