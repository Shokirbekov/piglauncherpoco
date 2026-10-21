package com.dinopig.piglauncher;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;

/**
 * Scales the workspace icon size between the launcher default (100%) and 150%.
 *
 * Hooks GridConfig$IconConfig#getIconSize() and multiplies whatever the
 * launcher itself computed, so the launcher's own icon-size setting and
 * per-device defaults are preserved as the 100% baseline.
 */
final class IconScale {

    // Newer POCO Launcher builds (common module) first, legacy location second.
    private static final String[] ICON_CONFIG_CLASSES = {
            "com.miui.home.common.gridconfig.GridConfig$IconConfig",
            "com.miui.home.launcher.GridConfig$IconConfig"
    };

    private IconScale() {
    }

    static void install(XposedModule module, ClassLoader classLoader, FeatureSwitches features) {
        for (String className : ICON_CONFIG_CLASSES) {
            Class<?> iconConfig = MainHook.findClass(className, classLoader);

            if (iconConfig == null) {
                continue;
            }

            Method getIconSize = findMethodRecursive(iconConfig, "getIconSize");

            if (getIconSize == null) {
                continue;
            }

            Class<?> returnType = getIconSize.getReturnType();

            if (returnType != int.class && returnType != float.class) {
                continue;
            }

            final boolean isInt = returnType == int.class;

            module.hook(getIconSize).intercept(chain -> {
                Object result = chain.proceed();

                int percent = features.getIconScalePercent();

                if (percent <= FeatureKeys.ICON_SCALE_MIN || !(result instanceof Number)) {
                    return result;
                }

                float scaled = ((Number) result).floatValue() * percent / 100f;

                return isInt ? (Object) Math.round(scaled) : (Object) scaled;
            });

            // Only hook the first matching class to avoid scaling twice.
            return;
        }
    }

    private static Method findMethodRecursive(
            Class<?> clazz,
            String name,
            Class<?>... parameterTypes
    ) {
        Class<?> current = clazz;

        while (current != null) {
            try {
                Method method = current.getDeclaredMethod(
                        name,
                        parameterTypes
                );
                method.setAccessible(true);
                return method;
            } catch (Throwable ignored) {
                current = current.getSuperclass();
            }
        }

        return null;
    }
}
