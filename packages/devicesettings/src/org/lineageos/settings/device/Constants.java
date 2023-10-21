package org.lineageos.settings.device;

import java.util.UUID;

public class Constants {
    /* Intent Actions and Extras */
    public static final String ACTION_DCDIMMING_SETTING_CHANGED = "devicesettings.intent.action.dcdimming_setting_changed";
    public static final String DCDIMMING_STATE = "devicesettings.extra.dcdimming.STATE";
    public static final String ACTION_DIRAC_SETTING_CHANGED = "devicesettings.intent.action.dirac_setting_changed";
    public static final String DIRAC_STATE = "devicesettings.extra.dirac.STATE";
    public static final String ACTION_HBM_SETTING_CHANGED = "devicesettings.intent.action.hbm_setting_changed";
    public static final String HBM_STATE = "devicesettings.extra.hbm.STATE";
    public static final String ACTION_KPROFILE_SETTING_CHANGED = "devicesettings.intent.action.kprofile_setting_changed";

    /* Refresh Rate Tile */
    public static final String KEY_MIN_REFRESH_RATE = "min_refresh_rate";
    public static final String KEY_PEAK_REFRESH_RATE = "peak_refresh_rate";

    /* DC Dimming */
    public static final String KEY_DC_DIMMING = "pref_dc_dimming";
    public static final String DC_DIMMING_NODE = "/sys/devices/platform/soc/soc:qcom,dsi-display/anti_flicker";

    /* KProfiles */
    public static final String KEY_KPROFILES_AUTO = "pref_kprofiles_auto";
    public static final String KPROFILES_AUTO_NODE = "/sys/module/kprofiles/parameters/auto_kp";
    public static final String KEY_KPROFILES_MODES = "pref_kprofiles_modes";
    public static final String KPROFILES_MODES_NODE = "/sys/kernel/kprofiles/kp_mode";
    public static final String KPROFILES_MODES_INFO = "pref_kprofiles_modes_info";

    /* Audio amplification */ 
    public static final String KEY_HEADPHONE_GAIN = "headphone_gain";
    public static final String HEADPHONE_GAIN_NODE = "/sys/kernel/sound_control/headphone_gain";
    public static final String KEY_MICROPHONE_GAIN = "microphone_gain";
    public static final String MICROPHONE_GAIN_NODE = "/sys/kernel/sound_control/mic_gain";
    public static final String KEY_SPEAKER_GAIN = "speaker_gain";
    public static final String SPEAKER_GAIN_NODE = "/sys/kernel/sound_control/speaker_gain";
    
    /* Mi Sound Enhancer */
    public static final String KEY_DIRAC_HEADSET = "dirac_headset_pref";
    public static final String KEY_DIRAC_HIFI = "dirac_hifi_pref";
    public static final String KEY_DIRAC_PRESET = "dirac_preset_pref";
    public static final String KEY_DIRAC_SCENE = "dirac_scenario_pref";
    public static final String KEY_DIRAC_ENABLE = "dirac_enable_pref";

    public static final int DIRACSOUND_PARAM_HEADSET_TYPE = 1;
    public static final int DIRACSOUND_PARAM_EQ_LEVEL = 2;
    public static final int DIRACSOUND_PARAM_MUSIC = 4;
    public static final int DIRACSOUND_PARAM_SCENE = 15;
    public static final int DIRACSOUND_PARAM_HIFI = 8;

    public static final UUID EFFECT_TYPE_DIRACSOUND =
            UUID.fromString("5b8e36a5-144a-4c38-b1d7-0002a5d5c51b");
    
    /* Flashlight Brightness Settings */
    public static final String KEY_FLASHLIGHT_BRIGHTNESS = "flashlight_brightness_pref";
    public static final String FLASHLIGHT_BRIGHTNESS_NODE = "/sys/class/leds/led:torch_0/max_brightness";

    public static final int FLASHLIGHT_MIN_BRIGHTNESS = 10; // Min(10), Max(200)
    public static final int FLASHLIGHT_MAX_BRIGHTNESS = 200; // Min(10), Max(200)

    /* FPS Counter */
    public static final String KEY_FPS_INFO = "fps_info";
    public static final String MEASURED_FPS_NODE = "/sys/class/drm/card0/sde-crtc-0/measured_fps";
    
    /* Haptic Level Settings */
    public final static String KEY_HAPTIC_LEVEL = "haptic_level_pref";
    public final static String HAPTIC_LEVEL_NODE = "/sys/devices/platform/soc/a8c000.i2c/i2c-2/2-005a/ulevel";

    public final static int HAPTIC_MIN_LEVEL = 1;
    public final static int HAPTIC_MAX_LEVEL = 128;

    /* Clear Speaker */
    public static final String KEY_CLEAR_SPEAKER = "clear_speaker_pref";

    /* Thermal Profiles */
    public static final String THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig";

    public static final int TOUCH_GAME_MODE = 0;
    public static final int TOUCH_PERF_LEVEL = 1;
    public static final int TOUCH_RESPONSE = 2;
    public static final int TOUCH_SENSITIVITY = 3;
    public static final int TOUCH_RESISTANT = 4;

    public static final String PROP_TOUCH_PERF_LEVEL = "sys.performance.level";
    public static final int MODE_TOUCH_GAME_MODE = 0;
    public static final int MODE_TOUCH_UP_THRESHOLD = 2;
    public static final int MODE_TOUCH_TOLERANCE = 3;
    public static final int MODE_TOUCH_EDGE_FILTER = 7;

    public static final String KEY_TOUCH_GAME_MODE = "touch_game_mode";
    public static final String KEY_TOUCH_PERF_LEVEL = "touch_perf_level";
    public static final String KEY_TOUCH_RESISTANT = "touch_resistant";
    public static final String KEY_TOUCH_RESPONSE = "touch_response";
    public static final String KEY_TOUCH_SENSITIVITY = "touch_sensitivity";

    public static final String THERMAL_CONTROL = "thermal_control";

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_BENCHMARK = 1;
    public static final int STATE_BROWSER = 2;
    public static final int STATE_CAMERA = 3;
    public static final int STATE_DIALER = 4;
    public static final int STATE_GAMING = 5;
    public static final int STATE_NAVIGATION = 6;
    public static final int STATE_STREAMING = 7;
    public static final int STATE_VIDEO = 8;

    public static final String THERMAL_STATE_DEFAULT = "0";
    public static final String THERMAL_STATE_BENCHMARK = "10";
    public static final String THERMAL_STATE_BROWSER = "11";
    public static final String THERMAL_STATE_CAMERA = "12";
    public static final String THERMAL_STATE_DIALER = "8";
    public static final String THERMAL_STATE_GAMING = "13";
    public static final String THERMAL_STATE_NAVIGATION = "19";
    public static final String THERMAL_STATE_STREAMING = "14";
    public static final String THERMAL_STATE_VIDEO = "21";

    public static final String THERMAL_BENCHMARK = "thermal.benchmark=";
    public static final String THERMAL_BROWSER = "thermal.browser=";
    public static final String THERMAL_CAMERA = "thermal.camera=";
    public static final String THERMAL_DIALER = "thermal.dialer=";
    public static final String THERMAL_GAMING = "thermal.gaming=";
    public static final String THERMAL_NAVIGATION = "thermal.navigation=";
    public static final String THERMAL_STREAMING = "thermal.streaming=";
    public static final String THERMAL_VIDEO = "thermal.video=";

    /* KCal */
    public static final String KEY_KCAL_ENABLE = "kcal_enable";
    public static final String KEY_KCAL_RESET_DEFAULT = "reset_default_button";
    public static final String KEY_KCAL_COLOR_PROFILES = "color_profiles";
    public static final String KEY_KCAL_RED = "red_slider";
    public static final String KEY_KCAL_GREEN = "green_slider";
    public static final String KEY_KCAL_BLUE = "blue_slider";
    public static final String KEY_KCAL_SATURATION = "saturation_slider";
    public static final String KEY_KCAL_CONTRAST = "contrast_slider";

    public static final String KCAL_ENABLE_NODE = "/sys/devices/platform/kcal_ctrl.0/kcal_enable";
    public static final String KCAL_RGB_NODE = "/sys/devices/platform/kcal_ctrl.0/kcal";
    public static final String KCAL_SATURATION_NODE = "/sys/devices/platform/kcal_ctrl.0/kcal_sat";
    public static final String KCAL_CONTRAST_NODE = "/sys/devices/platform/kcal_ctrl.0/kcal_cont";

    /* Hight Brightness Mode */
    public static final String KEY_HBM_SWITCH = "pref_hbm";
    public static final String KEY_AUTO_HBM_SWITCH = "pref_auto_hbm";
    public static final String KEY_AUTO_HBM_THRESHOLD = "pref_auto_hbm_threshold";
    public static final int DELAY_MILLIS = 7000; // 7 seconds
    public static final String HBM_NODE = "/sys/devices/platform/soc/soc:qcom,dsi-display/hbm";
    public static final String BACKLIGHT_NODE = "/sys/class/backlight/panel0-backlight/brightness";

    /* USB fastcharge */
    public static final String KEY_USB_FASTCHARGE = "pref_usb_charge";
    public static final String USB_FASTCHARGE_NODE = "/sys/kernel/fast_charge/force_fast_charge";

    /* Tile Entry Activity */
    public static final String DIRAC_TILE = "org.lineageos.settings.device.dirac.DiracTileService";
    public static final String KPROFILES_MODES_TILE = "org.lineageos.settings.device.kprofiles.KProfilesModesTileService";
    public static final String HBM_TILE = "org.lineageos.settings.device.hbm.HBMModeTileService";
    public static final String DCDIMMING_TILE = "org.lineageos.settings.device.DcDimmingTileService";
}
