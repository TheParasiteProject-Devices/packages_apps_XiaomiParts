package org.lineageos.settings.device.audioamplification;

import android.content.SharedPreferences;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.device.Constants;
import org.lineageos.settings.device.utils.FileUtils;

public class AudioAmplificationUtils {

    public static void restoreAudioAmplification(final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int mHeadphoneGain = sharedPreferences.getInt(Constants.KEY_HEADPHONE_GAIN, 4);
        int mMicrophoneGain = sharedPreferences.getInt(Constants.KEY_MICROPHONE_GAIN, 0);
        int mSpeakerGain = sharedPreferences.getInt(Constants.KEY_SPEAKER_GAIN, 0);

        FileUtils.writeLine(Constants.HEADPHONE_GAIN_NODE, mHeadphoneGain + " " + mHeadphoneGain);
        FileUtils.writeLine(Constants.MICROPHONE_GAIN_NODE, mMicrophoneGain);
        FileUtils.writeLine(Constants.SPEAKER_GAIN_NODE, mSpeakerGain);
    }
}
