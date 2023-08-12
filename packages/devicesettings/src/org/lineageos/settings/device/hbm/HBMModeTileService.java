/*
* Copyright (C) 2018 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.lineageos.settings.device.hbm;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;
import android.provider.Settings;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.DisplayUtils;
import org.lineageos.settings.device.utils.FileUtils;

public class HBMModeTileService extends TileService {
    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPrefs.edit().putBoolean(Constants.KEY_HBM_SWITCH, false).commit();
                updateUI(false);
            } else if (intent.getAction().equals(Constants.ACTION_HBM_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                updateUI(intent.getBooleanExtra(Constants.HBM_STATE, false));
            }
        }
    };

    private void updateUI(boolean enabled) {
        final Tile tile = getQsTile();
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Constants.ACTION_HBM_SETTING_CHANGED);
        registerReceiver(stateReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateUI(sharedPrefs.getBoolean(Constants.KEY_HBM_SWITCH, false));
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean enabled = !(sharedPrefs.getBoolean(Constants.KEY_HBM_SWITCH, false));
        FileUtils.writeLine(Constants.HBM_NODE, enabled ? "1" : "0");
        if (enabled && DisplayUtils.isAutoBrightnessEnabled(getContentResolver())) {
            FileUtils.writeLine(Constants.BACKLIGHT_NODE, "2047");
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        }
        sharedPrefs.edit().putBoolean(Constants.KEY_HBM_SWITCH, enabled).commit();
        updateUI(enabled);

        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_HBM_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.HBM_STATE, enabled);
        getApplicationContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }
}
