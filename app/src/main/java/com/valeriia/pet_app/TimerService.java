package com.valeriia.pet_app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private double time = 0.0;
    private boolean isTimerRunning = false;
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if ("RESET_TIMER".equals(intent.getAction())) {
                resetTimer();
            } else if ("TOGGLE_TIMER".equals(intent.getAction())) {
                toggleTimer();
            }
        }
        return START_STICKY;
    }

    private void toggleTimer() {
        if (!isTimerRunning) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    private void startTimer() {
        isTimerRunning = true;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                handler.post(() -> sendTimeToUI());
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        isTimerRunning = false;
    }

    private void resetTimer() {
        stopTimer();
        time = 0.0;
        sendTimeToUI();
    }

    private void sendTimeToUI() {
        Intent intent = new Intent("TimerUpdated");
        intent.putExtra("time", time);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
