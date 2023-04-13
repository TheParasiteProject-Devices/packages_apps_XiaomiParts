/*
 * Copyright (C) 2018 The LineageOS Project
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

import android.media.audiofx.AudioEffect;

import java.util.UUID;

import org.lineageos.settings.device.Constants;

public class DiracSound extends AudioEffect {

    private static final String TAG = "DiracSound";

    public DiracSound(int priority, int audioSession) {
        super(EFFECT_TYPE_NULL, Constants.EFFECT_TYPE_DIRACSOUND, priority, audioSession);
    }

    public int getMusic() throws IllegalStateException,
            IllegalArgumentException, UnsupportedOperationException {
        int[] value = new int[1];
        checkStatus(getParameter(Constants.DIRACSOUND_PARAM_MUSIC, value));
        return value[0];
    }

    public void setMusic(int enable) throws IllegalStateException,
            IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(Constants.DIRACSOUND_PARAM_MUSIC, enable));
    }

    public void setHeadsetType(int type) throws IllegalStateException,
            IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(Constants.DIRACSOUND_PARAM_HEADSET_TYPE, type));
    }

    public void setLevel(int band, float level) throws IllegalStateException,
            IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(new int[]{Constants.DIRACSOUND_PARAM_EQ_LEVEL, band},
                String.valueOf(level).getBytes()));
    }

    public void setScenario(int scene) throws IllegalStateException,
            IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(Constants.DIRACSOUND_PARAM_SCENE, scene));
    }

    public void setHifiMode(int mode) throws IllegalStateException,
            IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(Constants.DIRACSOUND_PARAM_HIFI, mode));
    }
}
