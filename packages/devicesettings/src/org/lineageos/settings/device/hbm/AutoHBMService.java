package org.lineageos.settings.device.hbm;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.UserHandle;
import androidx.preference.PreferenceManager;
import android.provider.Settings;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.DisplayUtils;
import org.lineageos.settings.device.utils.FileUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AutoHBMService extends Service {

    private static boolean mAutoHBMActive = false;
    private ExecutorService mExecutorService;

    private SensorManager mSensorManager;
    Sensor mLightSensor;

    private SharedPreferences mSharedPrefs;

    public void activateLightSensorRead() {
        submit(() -> {
            mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
            mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            mSensorManager.registerListener(mSensorEventListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        });
    }

    public void deactivateLightSensorRead() {
        submit(() -> {
            mSensorManager.unregisterListener(mSensorEventListener);
            mAutoHBMActive = false;
            enableHBM(false);
        });
    }

    private void enableHBM(boolean enable) {
        if (enable) {
            FileUtils.writeLine(Constants.HBM_NODE, "1");
            if (DisplayUtils.isAutoBrightnessEnabled(getContentResolver())) {
                FileUtils.writeLine(Constants.BACKLIGHT_NODE, "2047");
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
            }
        } else {
            FileUtils.writeLine(Constants.HBM_NODE, "0");
        }
        final Intent intent = new Intent(Constants.ACTION_HBM_SETTING_CHANGED);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra(Constants.HBM_STATE, enable);
        getApplicationContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            KeyguardManager km =
                (KeyguardManager) getSystemService(getApplicationContext().KEYGUARD_SERVICE);
            boolean keyguardShowing = km.inKeyguardRestrictedInputMode();
            float threshold = Float.parseFloat(mSharedPrefs.getString(Constants.KEY_AUTO_HBM_THRESHOLD, "20000"));
            if (lux > threshold) {
                if ((!mAutoHBMActive | !DisplayUtils.isHBMCurrentlyEnabled()) && !keyguardShowing) {
                    mAutoHBMActive = true;
                    enableHBM(true);
                }
            }
            if (lux < threshold) {
                if (mAutoHBMActive) {
                    mAutoHBMActive = false;
                    mExecutorService.submit(() -> {
                        try {
                            Thread.sleep(Constants.DELAY_MILLIS);
                        } catch (InterruptedException e) {
                        }
                        if (lux < threshold) {
                            enableHBM(false);
                        }
                    });
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // do nothing
        }
    };

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                activateLightSensorRead();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                deactivateLightSensorRead();
            }
        }
    };

    @Override
    public void onCreate() {
        mExecutorService = Executors.newSingleThreadExecutor();
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isInteractive()) {
            activateLightSensorRead();
        }
    }

    private Future < ? > submit(Runnable runnable) {
        return mExecutorService.submit(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isInteractive()) {
            deactivateLightSensorRead();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
