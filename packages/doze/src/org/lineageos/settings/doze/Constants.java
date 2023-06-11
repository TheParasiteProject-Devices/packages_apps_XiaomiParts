package org.lineageos.settings.doze;

public class Constants {
    /* Doze Utils */
    public static final String DOZE_INTENT = "com.android.systemui.doze.pulse";

    public static final String KEY_DOZE_SETTINGS = "doze_settings";
    public static final String KEY_FIRST_HELP_SHOWN = "first_help_shown";

    public static final String KEY_DOZE_ENABLE = "doze_enable";
    public static final String CATEG_PROX_SENSOR = "proximity_sensor";
    
    public static final String KEY_ALWAYS_ON_DISPLAY = "always_on_display";
    public static final String KEY_GESTURE_PICK_UP = "gesture_pick_up";
    public static final String KEY_GESTURE_RAISE_TO_WAKE = "gesture_raise_to_wake";
    public static final String KEY_GESTURE_HAND_WAVE = "gesture_hand_wave";
    public static final String KEY_GESTURE_POCKET = "gesture_pocket";
    public static final String KEY_WAKE_ON_GESTURE = "wake_on_gesture";

    public static final String PACKAGE_SYSTEMUI = "com.android.systemui";
    public static final String CONFIG_PROX_CHECK = "doze_proximity_check_before_pulse";
    public static final String CONFIG_PROX_CHECK_VAR = "bool";

    /* Pickup Gestures */
    public static final int MIN_PULSE_INTERVAL_MS = 2500;
    public static final int MIN_WAKEUP_INTERVAL_MS = 500;
    public static final int WAKELOCK_TIMEOUT_MS = 150;
    public static final String PICKUP_SENSOR = "xiaomi.sensor.pickup";

    /* Proximity Gestures */
    // Maximum time for the hand to cover the sensor: 1s
    public static final int HANDWAVE_MAX_DELTA_NS = 1000 * 1000 * 1000;

    // Minimum time until the device is considered to have been in the pocket: 2s
    public static final int POCKET_MIN_DELTA_NS = 2000 * 1000 * 1000;
}
