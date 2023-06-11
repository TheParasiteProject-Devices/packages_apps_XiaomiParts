/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.device.thermal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.FileUtils;

import vendor.xiaomi.hardware.touchfeature.V1_0.ITouchFeature;

public final class ThermalUtils {

    private boolean mTouchModeChanged;

    private Display mDisplay;
    private ITouchFeature mTouchFeature = null;
    private SharedPreferences mSharedPrefs;

    protected ThermalUtils(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        WindowManager mWindowManager = context.getSystemService(WindowManager.class);
        mDisplay = mWindowManager.getDefaultDisplay();

        try {
            mTouchFeature = ITouchFeature.getService();
        } catch (RemoteException e) {
            // Do nothing
        }
    }

    public static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, ThermalService.class),
                UserHandle.CURRENT);
    }

    private void writeValue(String profiles) {
        mSharedPrefs.edit().putString(Constants.THERMAL_CONTROL, profiles).apply();
    }

    private String getValue() {
        String value = mSharedPrefs.getString(Constants.THERMAL_CONTROL, null);

        if (value == null || value.isEmpty()) {
            value = Constants.THERMAL_BENCHMARK + ":" + Constants.THERMAL_BROWSER + ":" + Constants.THERMAL_CAMERA + ":" + Constants.THERMAL_DIALER + ":" +
                    Constants.THERMAL_GAMING + ":" + Constants.THERMAL_NAVIGATION + ":" + Constants.THERMAL_STREAMING + ":" + Constants.THERMAL_VIDEO;
            writeValue(value);
        }
        return value;
    }

    protected void writePackage(String packageName, int mode) {
        String value = getValue();
        value = value.replace(packageName + ",", "");
        String[] modes = value.split(":");
        String finalString;

        switch (mode) {
            case Constants.STATE_BENCHMARK:
                modes[0] = modes[0] + packageName + ",";
                break;
            case Constants.STATE_BROWSER:
                modes[1] = modes[1] + packageName + ",";
                break;
            case Constants.STATE_CAMERA:
                modes[2] = modes[2] + packageName + ",";
                break;
            case Constants.STATE_DIALER:
                modes[3] = modes[3] + packageName + ",";
                break;
            case Constants.STATE_GAMING:
                modes[4] = modes[4] + packageName + ",";
                break;
            case Constants.STATE_NAVIGATION:
                modes[5] = modes[5] + packageName + ",";
                break;
            case Constants.STATE_STREAMING:
                modes[6] = modes[6] + packageName + ",";
                break;
            case Constants.STATE_VIDEO:
                modes[7] = modes[7] + packageName + ",";
                break;
        }

        finalString = modes[0] + ":" + modes[1] + ":" + modes[2] + ":" + modes[3] + ":" +
                modes[4] + ":" + modes[5] + ":" + modes[6] + ":" + modes[7];

        writeValue(finalString);
    }

    protected int getStateForPackage(String packageName) {
        String value = getValue();
        String[] modes = value.split(":");
        int state = Constants.STATE_DEFAULT;
        if (modes[0].contains(packageName + ",")) {
            state = Constants.STATE_BENCHMARK;
        } else if (modes[1].contains(packageName + ",")) {
            state = Constants.STATE_BROWSER;
        } else if (modes[2].contains(packageName + ",")) {
            state = Constants.STATE_CAMERA;
        } else if (modes[3].contains(packageName + ",")) {
            state = Constants.STATE_DIALER;
        } else if (modes[4].contains(packageName + ",")) {
            state = Constants.STATE_GAMING;
        } else if (modes[5].contains(packageName + ",")) {
            state = Constants.STATE_NAVIGATION;
        } else if (modes[6].contains(packageName + ",")) {
            state = Constants.STATE_STREAMING;
        } else if (modes[7].contains(packageName + ",")) {
            state = Constants.STATE_VIDEO;
        }

        return state;
    }

    protected void setDefaultThermalProfile() {
        FileUtils.writeLine(Constants.THERMAL_SCONFIG, Constants.THERMAL_STATE_DEFAULT);
    }

    protected void setThermalProfile(String packageName) {
        String value = getValue();
        String modes[];
        String state = Constants.THERMAL_STATE_DEFAULT;

        if (value != null) {
            modes = value.split(":");

            if (modes[0].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_BENCHMARK;
            } else if (modes[1].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_BROWSER;
            } else if (modes[2].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_CAMERA;
            } else if (modes[3].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_DIALER;
            } else if (modes[4].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_GAMING;
            } else if (modes[5].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_NAVIGATION;
            } else if (modes[6].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_STREAMING;
            } else if (modes[7].contains(packageName + ",")) {
                state = Constants.THERMAL_STATE_VIDEO;
            }
        }
        FileUtils.writeLine(Constants.THERMAL_SCONFIG, state);

        if (state == Constants.THERMAL_STATE_BENCHMARK || state == Constants.THERMAL_STATE_GAMING) {
            updateTouchModes(packageName);
        } else if (mTouchModeChanged) {
            resetTouchModes();
        }
    }

    private void updateTouchModes(String packageName) {
        String values = mSharedPrefs.getString(packageName, null);
        resetTouchModes();

        if (values == null || values.isEmpty()) {
            return;
        }

        String[] value = values.split(",");
        int gameMode = Integer.parseInt(value[Constants.TOUCH_GAME_MODE]);
        String perfLevel = value[Constants.TOUCH_PERF_LEVEL];
        int touchResponse = Integer.parseInt(value[Constants.TOUCH_RESPONSE]);
        int touchSensitivity = Integer.parseInt(value[Constants.TOUCH_SENSITIVITY]);
        int touchResistant = Integer.parseInt(value[Constants.TOUCH_RESISTANT]);

        try {
            mTouchFeature.setTouchMode(Constants.MODE_TOUCH_GAME_MODE, gameMode);
            mTouchFeature.setTouchMode(Constants.MODE_TOUCH_UP_THRESHOLD, touchResponse);
            mTouchFeature.setTouchMode(Constants.MODE_TOUCH_TOLERANCE, touchSensitivity);
            mTouchFeature.setTouchMode(Constants.MODE_TOUCH_EDGE_FILTER, touchResistant);
        } catch (RemoteException e) {
            // Do nothing
        }
        SystemProperties.set(Constants.PROP_TOUCH_PERF_LEVEL, perfLevel);

        mTouchModeChanged = true;
    }

    protected void resetTouchModes() {
        if (!mTouchModeChanged) {
            return;
        }

        try {
            mTouchFeature.resetTouchMode(Constants.MODE_TOUCH_GAME_MODE);
            mTouchFeature.resetTouchMode(Constants.MODE_TOUCH_UP_THRESHOLD);
            mTouchFeature.resetTouchMode(Constants.MODE_TOUCH_TOLERANCE);
            mTouchFeature.resetTouchMode(Constants.MODE_TOUCH_EDGE_FILTER);
        } catch (RemoteException e) {
            // Do nothing
        }
        SystemProperties.set(Constants.PROP_TOUCH_PERF_LEVEL, "0");

        mTouchModeChanged = false;
    }
}
