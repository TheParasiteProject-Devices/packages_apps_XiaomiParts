/*
 * Copyright (C) 2021 WaveOS
 * Copyright (C) 2021 Chaldeaprjkt
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

import android.content.Context;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.Display;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RefreshRateTileService extends TileService {

    private Context context;
    private Tile tile;

    private final List<Integer> availableRates = new ArrayList<>();
    private int activeRateMin;
    private int activeRateMax;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Display.Mode mode = context.getDisplay().getMode();
        Display.Mode[] modes = context.getDisplay().getSupportedModes();
        int[] blocklist = context.getResources().getIntArray(R.array.refresh_rate_tile_blocklist);
        for (Display.Mode m : modes) {
            int rate = (int) Math.round(m.getRefreshRate());
            boolean isBlocked = blocklist != null && Arrays.stream(blocklist).anyMatch(x -> x == rate);
            if (m.getPhysicalWidth() == mode.getPhysicalWidth() &&
                    m.getPhysicalHeight() == mode.getPhysicalHeight() && !isBlocked) {
                availableRates.add(rate);
            }
        }
        if (!availableRates.isEmpty()) {
            availableRates.sort(Comparator.naturalOrder());
        }
        syncFromSettings();
    }

    private int getSettingOf(String key) {
        float rate = Settings.System.getFloat(context.getContentResolver(), key, 60);
        int active = availableRates.indexOf((int) Math.round(rate));
        return Math.max(active, 0);
    }

    private void syncFromSettings() {
        activeRateMin = getSettingOf(Constants.KEY_MIN_REFRESH_RATE);
        activeRateMax = getSettingOf(Constants.KEY_PEAK_REFRESH_RATE);
    }

    private void cycleRefreshRate() {
        if(activeRateMax == 0){
    	    if(activeRateMin == 0){
                activeRateMin= availableRates.size();
	    }
	    activeRateMax = activeRateMin;
	    float rate = availableRates.get(activeRateMin - 1);
  	    Settings.System.putFloat(context.getContentResolver(), Constants.KEY_MIN_REFRESH_RATE, rate);
        }
        float rate = availableRates.get(activeRateMax - 1);
        Settings.System.putFloat(context.getContentResolver(), Constants.KEY_PEAK_REFRESH_RATE, rate);
    }

    private void updateTileView() {
        String displayText;
        int min = availableRates.get(activeRateMin);
        int max = availableRates.get(activeRateMax);

        displayText = String.format(Locale.US, min == max ? "%d Hz" : "%d - %d Hz", min, max);
        tile.setContentDescription(displayText);
        tile.setSubtitle(displayText);
        tile.setState(min != max ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        syncFromSettings();
        updateTileView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick() {
        cycleRefreshRate();
        syncFromSettings();
        updateTileView();
        super.onClick();
    }
}
