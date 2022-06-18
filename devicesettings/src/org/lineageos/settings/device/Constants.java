package org.lineageos.settings.device;

public class Constants {
    public static final String KEY_REFRESH_RATE_CONFIG = "pref_refresh_rate_config";
    public static final String KEY_REFRESH_RATE_INFO = "pref_refresh_rate_info";
    public static final String KEY_DC_DIMMING = "pref_dc_dimming";
    public static final String REFRESH_RATE_SWITCH_SUMMARY = "enable_high_refresh_rate_summary";
    public static final float[] REFRESH_RATES = {60.0f, 120.0f};
    public static final float DEFAULT_REFRESH_RATE = REFRESH_RATES[1];

    public static final String DC_DIMMING_NODE = "/sys/devices/platform/soc/soc:qcom,dsi-display/anti_flicker";

    public static final String KEY_HBM = "pref_hbm";
    public static final String HBM_NODE = "/sys/devices/platform/soc/soc:qcom,dsi-display/hbm";

    public static final String DEFAULT_REFRESH_RATE_CONFIG = "120-120";

    public static final String KEY_KPROFILES_AUTO = "pref_kprofiles_auto";
    public static final String KPROFILES_AUTO_NODE = "/sys/module/kprofiles/parameters/auto_kprofiles";
    public static final String KEY_KPROFILES_MODES= "pref_kprofiles_modes";
    public static final String KPROFILES_MODES_NODE = "/sys/module/kprofiles/parameters/kp_mode";
}
