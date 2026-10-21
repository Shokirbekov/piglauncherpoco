package com.dinopig.piglauncher;

import android.content.SharedPreferences;

final class FeatureSwitches {

    private final SharedPreferences preferences;
    private final SharedPreferences.OnSharedPreferenceChangeListener listener;

    private volatile boolean folderDarkMode = false;
    private volatile boolean advancedTextures = false;
    private volatile boolean folderAdaptIconSize = false;
    private volatile boolean predictiveBackProgress = false;
    private volatile boolean allWidgetAnimation = false;
    private volatile int iconScalePercent = FeatureKeys.ICON_SCALE_MIN;

    FeatureSwitches(SharedPreferences preferences) {
        this.preferences = preferences;
        listener = (sharedPreferences, key) -> reload(sharedPreferences);

        reload(preferences);

        if (preferences != null) {
            preferences.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    boolean isFolderDarkModeEnabled() {
        return folderDarkMode;
    }

    boolean isAdvancedTexturesEnabled() {
        return advancedTextures;
    }

    boolean isFolderAdaptIconSizeEnabled() {
        return folderAdaptIconSize;
    }

    boolean isPredictiveBackProgressEnabled() {
        return predictiveBackProgress;
    }

    boolean isAllWidgetAnimationEnabled() {
        return allWidgetAnimation;
    }

    int getIconScalePercent() {
        return iconScalePercent;
    }

    private void reload(SharedPreferences preferences) {
        if (preferences == null) {
            return;
        }

        folderDarkMode = preferences.getBoolean(
                FeatureKeys.FOLDER_DARK_MODE,
                false
        );

        advancedTextures = preferences.getBoolean(
                FeatureKeys.ADVANCED_TEXTURES,
                false
        );

        folderAdaptIconSize = preferences.getBoolean(
                FeatureKeys.FOLDER_ADAPT_ICON_SIZE,
                false
        );

        predictiveBackProgress = preferences.getBoolean(
                FeatureKeys.PREDICTIVE_BACK_PROGRESS,
                false
        );

        allWidgetAnimation = preferences.getBoolean(
                FeatureKeys.ALL_WIDGET_ANIMATION,
                false
        );

        int scale = preferences.getInt(
                FeatureKeys.ICON_SCALE_PERCENT,
                FeatureKeys.ICON_SCALE_MIN
        );

        iconScalePercent = Math.max(
                FeatureKeys.ICON_SCALE_MIN,
                Math.min(FeatureKeys.ICON_SCALE_MAX, scale)
        );
    }
}
