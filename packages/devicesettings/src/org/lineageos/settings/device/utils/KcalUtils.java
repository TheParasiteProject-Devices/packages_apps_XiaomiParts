/*
 * Copyright (C) 2020 ArrowOS
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

package org.lineageos.settings.device.utils;

import android.content.SharedPreferences;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.FileUtils;

public final class KcalUtils {

    private static final String[] COLOR_PROFILE_SETTINGS = {"red", "green", "blue", "saturation", "contrast"};

    private static final int[][] COLOR_PROFILES_VALUES = {
        {256, 256, 256, 255, 255}, // Default
        {240, 240, 256, -1, -1}, // Cold
        {256, 256, 240, -1, -1}, // Warm
        {-1, -1, -1, 265, -1}}; // Saturated

    // Write the given value to the given position on the KCAL node
    public static void writeConfigToNode(String node, int position, int value) {
        String mDefaultRgbFormat = "R G B";
        String mNewNodeData = "";

        switch(position) {
            case 0:
                mNewNodeData = String.valueOf(value);
                break;
            case 1:
                mNewNodeData = mDefaultRgbFormat.replace("R", String.valueOf(value));
                break;
            case 2:
                mNewNodeData = mDefaultRgbFormat.replace("G", String.valueOf(value));
                break;
            case 3:
                mNewNodeData = mDefaultRgbFormat.replace("B", String.valueOf(value));
                break;
        }

         mNewNodeData = mNewNodeData
            .replace("R", getNodeData(Constants.KCAL_RGB_NODE, 1))
            .replace("G", getNodeData(Constants.KCAL_RGB_NODE, 2))
            .replace("B", getNodeData(Constants.KCAL_RGB_NODE, 3));

        FileUtils.writeLine(node, mNewNodeData);
    }

    // Get the value of the given position
    // 0 is the full node value
    // 1, 2 and 3 will return the first, second and third part of the node data divided by a space
    public static String getNodeData(String node, int position) {
        String mNodeData = FileUtils.readOneLine(node);
        switch(position) {
            case 0:
                return mNodeData;
            case 1:
            case 2:
            case 3:
                return mNodeData.split(" ")[position - 1];
            default:
                return null;
        }
    }

    public static void writeCurrentSettings(SharedPreferences sharedPrefs) {
        FileUtils.writeLine(Constants.KCAL_ENABLE_NODE,
            sharedPrefs.getBoolean(Constants.KEY_KCAL_ENABLE, false) ? "1" : "0");

        KcalUtils.writeConfigToNode(Constants.KCAL_RGB_NODE, 1, sharedPrefs.getInt(Constants.KEY_KCAL_RED, 256));
        KcalUtils.writeConfigToNode(Constants.KCAL_RGB_NODE, 2, sharedPrefs.getInt(Constants.KEY_KCAL_GREEN, 256));
        KcalUtils.writeConfigToNode(Constants.KCAL_RGB_NODE, 3, sharedPrefs.getInt(Constants.KEY_KCAL_BLUE, 256));
        KcalUtils.writeConfigToNode(Constants.KCAL_SATURATION_NODE, 0, sharedPrefs.getInt(Constants.KEY_KCAL_SATURATION, 255));
        KcalUtils.writeConfigToNode(Constants.KCAL_CONTRAST_NODE, 0, sharedPrefs.getInt(Constants.KEY_KCAL_CONTRAST, 255));
    }

    public static boolean isKcalSupported() {
        return FileUtils.fileExists(Constants.KCAL_ENABLE_NODE);
    }

    public static int getMin(String key) {
        switch (key) {
             case Constants.KEY_KCAL_SATURATION:
             case Constants.KEY_KCAL_CONTRAST:
                 return 224;
             default:
                 return 1;
        }
    }

    public static void setColorProfile(int profileIndex, SharedPreferences sharedPrefs) {
        try {
            int[] profileSettings = COLOR_PROFILES_VALUES[profileIndex];
            if (profileSettings.length != COLOR_PROFILE_SETTINGS.length) {
                return;
            }

            for (int i = 0; i < profileSettings.length; i++) {
                if (profileSettings[i] != -1) {
                    sharedPrefs.edit().putInt(COLOR_PROFILE_SETTINGS[i] + "_slider", profileSettings[i]).apply();
                }
            }

            writeCurrentSettings(sharedPrefs);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
