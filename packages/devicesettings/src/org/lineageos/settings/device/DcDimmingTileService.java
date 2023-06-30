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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.DisplayUtils;

public class DcDimmingTileService extends TileService {

    private SharedPreferences sharedPrefs;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_DCDIMMING_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                updateTile(intent.getBooleanExtra(Constants.DCDIMMING_STATE, false));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void updateTile(boolean enabled) {
        final Tile tile = getQsTile();
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_DCDIMMING_SETTING_CHANGED);
        registerReceiver(stateReceiver, filter);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateTile(sharedPrefs.getBoolean(Constants.KEY_DC_DIMMING, false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
    }

    @Override
    public void onClick() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean enabled = !(sharedPrefs.getBoolean(Constants.KEY_DC_DIMMING, false));
        DisplayUtils.setDcDimmingStatus(enabled);
        sharedPrefs.edit().putBoolean(Constants.KEY_DC_DIMMING, enabled).commit();

        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_DCDIMMING_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.DCDIMMING_STATE, enabled);
        getApplicationContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);

        updateTile(enabled);
        super.onClick();
    }
}
