package com.hehongdan.anr.plan3;

import android.app.Application;
import android.util.Log;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 类描述：。
 *
 * @author HeHongdan
 * @date 7/15/21
 * @since v7/15/21
 */
public class ANRWatchdogTestApplication extends Application {

    ANRWatchDog anrWatchDog = new ANRWatchDog(2000);

    int duration = 4;

    final ANRWatchDog.ANRListener silentListener = new ANRWatchDog.ANRListener() {
        @Override
        public void onAppNotResponding(ANRError error) {
            Log.e("ANR-Watchdog-Demo", "", error);
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();

        initAnrWatchDog();
    }

    private void initAnrWatchDog() {
        anrWatchDog
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        Log.e("ANR-Watchdog-Demo", "Detected Application Not Responding!");

                        // Some tools like ACRA are serializing the exception, so we must make sure the exception serializes correctly
                        try {
                            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(error);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        Log.i("ANR-Watchdog-Demo", "Error was successfully serialized");

                        throw error;
                    }
                })
                .setANRInterceptor(new ANRWatchDog.ANRInterceptor() {
                    @Override
                    public long intercept(long duration) {
                        long ret = ANRWatchdogTestApplication.this.duration * 1000 - duration;
                        if (ret > 0)
                            Log.w("ANR-Watchdog-Demo", "Intercepted ANR that is too short (" + duration + " ms), postponing for " + ret + " ms.");
                        return ret;
                    }
                })
        ;

        anrWatchDog.start();
    }
}
