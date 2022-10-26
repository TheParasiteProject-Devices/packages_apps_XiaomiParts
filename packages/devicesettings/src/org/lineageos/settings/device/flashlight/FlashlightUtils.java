/*
 * Copyright (C) 2021 chaldeaprjkt
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
 * limitations under the License
 */

package org.lineageos.settings.device.flashlight;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import java.lang.Math;

import org.lineageos.settings.device.utils.FileUtils;

public final class FlashlightUtils {

    final static String PREF_BRIGHTNESS = "flashlight_brightness_pref";
    final static String PATH_BRIGHTNESS = "/sys/class/leds/led:torch_0/max_brightness";

    // Min(10), Max(200)
    final static int MIN_BRIGHTNESS = 10;

    public static void applyBrightness(Context context, int value) {
        if (FileUtils.fileExists(PATH_BRIGHTNESS)) {
            int newValue = value+MIN_BRIGHTNESS;
            FileUtils.writeLine(PATH_BRIGHTNESS, String.valueOf(value));
        }
    }

    public static void restoreBrightness(Context context) {
        applyBrightness(context, Settings.Secure.getInt(
            context.getContentResolver(), PREF_BRIGHTNESS, 100));
    }
}
