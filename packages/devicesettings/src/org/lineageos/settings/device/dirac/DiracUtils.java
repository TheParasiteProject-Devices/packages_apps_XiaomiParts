/*
 * Copyright (C) 2018,2020 The LineageOS Project
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

package org.lineageos.settings.device.dirac;

import android.content.Context;
import android.media.AudioManager;


public class DiracUtils {

    private static DiracUtils mInstance;
    protected static DiracSound mDiracSound;
    private static boolean mInitialized;
    private static Context mContext;

    public DiracUtils(Context context) {
        mContext = context;
        mDiracSound = new DiracSound(0, 0);
    }

    public static synchronized DiracUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DiracUtils(context);
        }

        return mInstance;
    }

    public void setMusic(boolean enable) {
        mDiracSound.setMusic(enable ? 1 : 0);
    }

    public boolean isDiracEnabled() {
        return mDiracSound != null && mDiracSound.getMusic() == 1;
    }

    public void setLevel(String preset) {
        String[] level = preset.split("\\s*,\\s*");

        for (int band = 0; band <= level.length - 1; band++) {
            mDiracSound.setLevel(band, Float.valueOf(level[band]));
        }
    }

    public void setHeadsetType(int paramInt) {
        mDiracSound.setHeadsetType(paramInt);
    }

    protected static void setScenario(int sceneInt) {
        mDiracSound.setScenario(sceneInt);
    }

    public boolean getHifiMode() {
        AudioManager audioManager = mContext.getSystemService(AudioManager.class);
        return audioManager.getParameters("hifi_mode").contains("true");
    }

    public void setHifiMode(int paramInt) {
        AudioManager audioManager = mContext.getSystemService(AudioManager.class);
        audioManager.setParameters("hifi_mode=" + (paramInt == 1 ? true : false));
        mDiracSound.setHifiMode(paramInt);
    }
}
