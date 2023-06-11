/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
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

package org.lineageos.settings.doze;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.PowerManager;
import android.os.SystemClock;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;

import static android.provider.Settings.Secure.DOZE_ALWAYS_ON;
import static android.provider.Settings.Secure.DOZE_ENABLED;

import org.lineageos.settings.doze.Constants;

public final class DozeUtils {

    private static final String TAG = "DozeUtils";
    private static final boolean DEBUG = false;

    protected static void startService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, DozeService.class),
                UserHandle.CURRENT);
    }

    protected static void stopService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping service");
        context.stopServiceAsUser(new Intent(context, DozeService.class),
                UserHandle.CURRENT);
    }

    protected static void checkDozeService(Context context) {
        if (isDozeEnabled(context) && !isAlwaysOnEnabled(context) && sensorsEnabled(context)) {
            startService(context);
        } else {
            stopService(context);
        }
    }

    protected static boolean getProxCheckBeforePulse(Context context) {
        try {
            Context con = context.createPackageContext(Constants.PACKAGE_SYSTEMUI, 0);
            int id = con.getResources().getIdentifier(Constants.CONFIG_PROX_CHECK,
                    Constants.CONFIG_PROX_CHECK_VAR, Constants.PACKAGE_SYSTEMUI);
            return con.getResources().getBoolean(id);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected static boolean isDozeEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                DOZE_ENABLED, 1) != 0;
    }

    protected static boolean enableDoze(Context context, boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                DOZE_ENABLED, enable ? 1 : 0);
    }

    protected static void wakeOrLaunchDozePulse(Context context) {
        if (isWakeOnGestureEnabled(context)) {
            if (DEBUG) Log.d(TAG, "Wake up display");
            PowerManager powerManager = context.getSystemService(PowerManager.class);
            powerManager.wakeUp(SystemClock.uptimeMillis(), PowerManager.WAKE_REASON_GESTURE, TAG);
        } else {
            if (DEBUG) Log.d(TAG, "Launch doze pulse");
            context.sendBroadcastAsUser(
                    new Intent(Constants.DOZE_INTENT), new UserHandle(UserHandle.USER_CURRENT));
        }
    }

    protected static boolean enableAlwaysOn(Context context, boolean enable) {
        return Settings.Secure.putIntForUser(context.getContentResolver(),
                DOZE_ALWAYS_ON, enable ? 1 : 0, UserHandle.USER_CURRENT);
    }

    protected static boolean isAlwaysOnEnabled(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(),
                DOZE_ALWAYS_ON, 1, UserHandle.USER_CURRENT) != 0;
    }

    protected static boolean alwaysOnDisplayAvailable(Context context) {
        return new AmbientDisplayConfiguration(context).alwaysOnAvailable();
    }

    protected static void enableGesture(Context context, String gesture, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(gesture, enable).apply();
    }

    protected static boolean isGestureEnabled(Context context, String gesture) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(gesture, false);
    }

    protected static boolean isWakeOnGestureEnabled(Context context) {
        return isGestureEnabled(context, Constants.KEY_WAKE_ON_GESTURE);
    }

    protected static boolean isPickUpEnabled(Context context) {
        return isGestureEnabled(context, Constants.KEY_GESTURE_PICK_UP);
    }

    protected static void setPickUp(Preference preference, boolean value) {
        SwitchPreference pickup = (SwitchPreference)preference;
        pickup.setChecked(value);
        pickup.setEnabled(!value);
    }

    protected static boolean isRaiseToWakeEnabled(Context context) {
        return isGestureEnabled(context, Constants.KEY_GESTURE_RAISE_TO_WAKE);
    }

    protected static boolean isHandwaveGestureEnabled(Context context) {
        return isGestureEnabled(context, Constants.KEY_GESTURE_HAND_WAVE);
    }

    protected static boolean isPocketGestureEnabled(Context context) {
        return isGestureEnabled(context, Constants.KEY_GESTURE_POCKET);
    }

    protected static boolean sensorsEnabled(Context context) {
        return isPickUpEnabled(context) || isRaiseToWakeEnabled(context) ||
                isHandwaveGestureEnabled(context) || isPocketGestureEnabled(context);
    }

    protected static Sensor getSensor(SensorManager sm, String type) {
        for (Sensor sensor : sm.getSensorList(Sensor.TYPE_ALL)) {
            if (type.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }
}
