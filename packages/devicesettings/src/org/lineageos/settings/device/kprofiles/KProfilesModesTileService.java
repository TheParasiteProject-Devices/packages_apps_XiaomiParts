package org.lineageos.settings.device.kprofiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.FileUtils;

public class KProfilesModesTileService extends TileService {
    private KProfilesUtils mKProfilesUtils;
    private SharedPreferences mSharedPrefs;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_KPROFILE_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                updateTileContent();
            }
        }
    };

    @Override
    public void onStartListening() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_KPROFILE_SETTING_CHANGED);
        registerReceiver(stateReceiver, filter);

        updateTileContent();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        unregisterReceiver(stateReceiver);
    }

    @Override
    public void onClick() {
        String mode = getMode();
        switch (mode) {
            case "0":
                mode = "1"; // Set mode from none to battery
                break;
            case "1":
                mode = "2"; // Set mode from battery to balanced
                break;
            case "2":
                mode = "3"; // Set mode from balanced to performance
                break;
            case "3":
                mode = "0"; // Set mode from performance to none
                break;
        }

        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_KPROFILE_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        getApplicationContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);

        setMode(mode);
        updateTileContent();
        super.onClick();
    }

    private void setMode(String mode) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSharedPrefs.edit().putString(Constants.KEY_KPROFILES_MODES, mode).apply();
        FileUtils.writeLine(Constants.KPROFILES_MODES_NODE, mode);
    }

    private String getMode() {
        return mKProfilesUtils.getCurrentKProfilesMode(getApplicationContext());
    }

    private void updateTileContent() {
        boolean isActive;
        Tile tile = getQsTile();
        String mode = getMode();
        isActive = !mode.equals("0");

        tile.setState(isActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        switch (mode) {
            case "0":
                tile.setContentDescription(getResources().getString(R.string.kprofiles_modes_none));
                tile.setSubtitle(getResources().getString(R.string.kprofiles_modes_none));
                break;
            case "1":
                tile.setContentDescription(getResources().getString(R.string.kprofiles_modes_battery));
                tile.setSubtitle(getResources().getString(R.string.kprofiles_modes_battery));
                break;
            case "2":
                tile.setContentDescription(getResources().getString(R.string.kprofiles_modes_balanced));
                tile.setSubtitle(getResources().getString(R.string.kprofiles_modes_balanced));
                break;
            case "3":
                tile.setContentDescription(getResources().getString(R.string.kprofiles_modes_performance));
                tile.setSubtitle(getResources().getString(R.string.kprofiles_modes_performance));
                break;
        }
        tile.updateTile();
    }
}
