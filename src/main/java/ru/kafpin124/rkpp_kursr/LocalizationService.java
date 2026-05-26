package ru.kafpin124.rkpp_kursr;

import java.util.*;
import java.util.function.Consumer;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LocalizationService {
    private static Locale currentLocale = new Locale("ru", "RU");

    public static Locale getCurrentLocale() {
        return currentLocale;
    }


    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("text", currentLocale);
    }


    public static void changeLocale(Locale locale) {
        currentLocale = locale;
        // HKEY_CURRENT_USER\Software\JavaSoft\Prefs\myapp
        Preferences.userRoot().node("myapp").put("locale", locale.toLanguageTag());
    }

    public static void initFromPreferences() {
        String saved = Preferences.userRoot().node("myapp").get("locale", "ru-RU");
        currentLocale = Locale.forLanguageTag(saved);
    }
}