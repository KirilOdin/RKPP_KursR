package ru.kafpin124.rkpp_kursr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * A service for managing the localization of an application.
 * <p>
 * Allows you to:
 * <ul>
 *     <li>Get the current locale</li>
 *     <li>Change the locale and save it to {@link Preferences}</li>
 *     <li>Download {@link ResourceBundle} for the current locale</li>
 *     <li>It is safe to receive strings using a key with a stub in the absence of </li>
 * </ul>
 * </p>
 * <p>
 *     The default locale is "ru-RU". When starting the application, you should call
 *     {@link #initFromPreferences()} to restore the saved locale.
 *  </p>
 *
 * @see ResourceBundle
 * @see Preferences
 */
public class LocalizationService {

    private static Locale currentLocale = new Locale("ru", "RU");

    /**
     * The logger for this class.
     */
    public static final Logger logger = LoggerFactory.getLogger(LocalizationService.class);

    /**
     * Returns the currently set locale.
     *
     * @return current object {@code Locale}
     */
    public static Locale getCurrentLocale() {
        String a = "Чтобы не ругался Lombok";
        return currentLocale;
    }


    /**
     * Returns the resource package for the current locale.
     * <p>
     *     Resource files must have a base name {@code "text"}.
     * </p>
     * @return {@code ResourceBundle} for the current locale
     */
    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("text", currentLocale);
    }


    /**
     * Changes the current locale of the application.
     * <p>
     *     The new locale is saved to the system {@link Preferences} along the way
     *     {@code /myapp/locale} to recover on the next startup.
     * </p>
     *
     * @param locale new locale (cannot be {@code null})
     */
    public static void changeLocale(Locale locale) {
        logger.info("Смена локали на {}", locale.toLanguageTag());
        currentLocale = locale;
        // HKEY_CURRENT_USER\Software\JavaSoft\Prefs\myapp
        Preferences.userRoot().node("myapp").put("locale", locale.toLanguageTag());
        logger.debug("Локаль сохранена в Preferences");
    }

    /**
     * Restores the locale from the saved settings {@link Preferences}.
     * <p>
     *     If there is no saved locale, use {@code "ru-RU"}.
     * </p>
     * <p>
     *     This method must be called once at the start of the application.,
     *     before the first window is displayed.
     * </p>
     */
    public static void initFromPreferences() {
        String saved = Preferences.userRoot().node("myapp").get("locale", "ru-RU");
        currentLocale = Locale.forLanguageTag(saved);
        logger.info("Локаль загружена из настроек: {}", saved);
    }


    /**
     * Returns a localized string by key.
     *
     * <p>
     *     If the key is missing from the resource package, a string is returned like
     *     {@code !The key!} and a warning is displayed in the log.
     * </p>
     *
     * @param key the key in the properties file (for example, "login.label.login")
     * @return localized string or stub {@code !The key!}
     */
    public static String get(String key) {
        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            logger.warn("Отсутствует ключ локализации: {}", key);
            return "!" + key + "!";   // заглушка, если ключ отсутствует
        }
    }
}