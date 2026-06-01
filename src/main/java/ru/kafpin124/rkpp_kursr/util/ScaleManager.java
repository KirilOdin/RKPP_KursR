package ru.kafpin124.rkpp_kursr.util;

import javafx.scene.Scene;
import java.util.prefs.Preferences;

public class ScaleManager {
    private static final String SCALE_PREF_KEY = "scale_factor";
    private static final double DEFAULT_SCALE = 1.0;
    private static final double BASE_FONT_SIZE = 14.0;
    private static double currentScale = DEFAULT_SCALE;

    static {
        // Загружаем сохранённый масштаб при старте приложения
        String saved = Preferences.userRoot().node("myapp").get(SCALE_PREF_KEY, String.valueOf(DEFAULT_SCALE));
        try {
            double factor = Double.parseDouble(saved);
            if (factor >= 0.5 && factor <= 2.0) {
                currentScale = factor;
            }
        } catch (NumberFormatException ignored) { }
    }

    /** Возвращает текущий масштаб (1.0 = 100%) */
    public static double getScale() {
        return currentScale;
    }

    /** Устанавливает новый масштаб и сохраняет в Preferences */
    public static void setScale(double factor) {
        if (Math.abs(currentScale - factor) < 0.001) return;
        currentScale = factor;
        Preferences.userRoot().node("myapp").put(SCALE_PREF_KEY, String.valueOf(factor));
        // Применять к уже открытым сценам можно вручную (см. ниже)
    }

    /** Применяет текущий масштаб к указанной сцене */
    public static void applyToScene(Scene scene) {
        if (scene != null) {
            scene.getRoot().setStyle(String.format("-fx-font-size: %.1fpx;", BASE_FONT_SIZE * currentScale));
        }
    }
}