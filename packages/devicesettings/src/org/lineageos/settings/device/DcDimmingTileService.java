/*
 * Copyright (C) 2021 Paranoid Android
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

package org.lineageos.settings.device;

import static org.lineageos.settings.device.Constants.KEY_DC_DIMMING;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.DisplayUtils;

public class DcDimmingTileService extends TileService {

    private SharedPreferences sharedPrefs;
    private Context context;
    private Tile tile;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    private void updateTile(boolean enabled) {
        final Tile tile = getQsTile();
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        tile = getQsTile();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateTile(sharedPrefs.getBoolean(KEY_DC_DIMMING, false));
        super.onStartListening();
    }

    @Override
    public void onClick() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean enabled = !(sharedPrefs.getBoolean(KEY_DC_DIMMING, false));
        DisplayUtils.setDcDimmingStatus(enabled);
        sharedPrefs.edit().putBoolean(KEY_DC_DIMMING, enabled).commit();
        updateTile(enabled);
        super.onClick();
    }
}
