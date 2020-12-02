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

import static org.lineageos.settings.device.Constants.KEY_HBM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import java.lang.IllegalArgumentException;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.DisplayUtils;

public class HbmTileService extends TileService {

    private Intent mHbmIntent;
    private SharedPreferences sharedPrefs;
    private Context context;
    private Tile tile;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        tryStopService();
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        updateState();
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        boolean enabled = DisplayUtils.isHBMEnabled(this);
        // NOTE: reverse logic, enabled reflects the state before press
        DisplayUtils.setHBMStatus(!enabled);
        if (!enabled == true) {
            mHbmIntent = new Intent(this, HbmService.class);
            this.startService(mHbmIntent);
        }
        updateState();
        super.onClick();
    }

    private void updateState() {
        boolean enabled = DisplayUtils.isHBMEnabled(this);
        if (enabled == false) tryStopService();
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    private void tryStopService() {
        if (mHbmIntent == null) return;
        this.stopService(mHbmIntent);
        mHbmIntent = null;
    }
}
