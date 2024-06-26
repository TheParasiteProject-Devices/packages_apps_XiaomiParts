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

package org.lineageos.settings.device.color;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import org.lineageos.settings.device.R;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

public class KcalSettingsActivity extends CollapsingToolbarBaseActivity {

    private static final String TAG = "kcal_settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_kcal);

        getFragmentManager()
            .beginTransaction()
            .replace(
                R.id.fragment_kcal,
                new KcalSettingsFragment(),
                TAG
            ).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
