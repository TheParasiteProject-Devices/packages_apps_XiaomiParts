package org.lineageos.settings.device.dirac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.lineageos.settings.device.Constants;

public class DiracTileService extends TileService {

    private DiracUtils mDiracUtils;

    private boolean mSelfChange = false;

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_DIRAC_SETTING_CHANGED)) {
                if (mSelfChange) {
                    mSelfChange = false;
                    return;
                }
                updateUI(intent.getBooleanExtra(Constants.DIRAC_STATE, false));
            }
        }
    };

    private void updateUI(boolean enabled) {
        final Tile tile = getQsTile();
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_DIRAC_SETTING_CHANGED);
        registerReceiver(stateReceiver, filter);

        mDiracUtils = DiracUtils.getInstance(getApplicationContext());
        updateUI(mDiracUtils.isDiracEnabled());
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        unregisterReceiver(stateReceiver);
    }

    @Override
    public void onClick() {
        final boolean enabled = !mDiracUtils.isDiracEnabled();
        mDiracUtils.setMusic(enabled);

        mSelfChange = true;
        final Intent intent = new Intent(Constants.ACTION_DIRAC_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.DIRAC_STATE, enabled);
        getApplicationContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);

        updateUI(enabled);
        super.onClick();
    }
}
