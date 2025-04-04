package com.valeriia.pet_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TimerFragment extends Fragment {

    private TextView timerText;
    private Button stopStartButton;
    private boolean timerStarted = false;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        timerText = view.findViewById(R.id.timerValue);
        stopStartButton = view.findViewById(R.id.startStopButton);

        // Запуск сервиса
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().startService(intent);

        stopStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopTapped(v);
            }
        });

        view.findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTapped(v);
            }
        });

        // Регистрация BroadcastReceiver для получения обновленного времени
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("TimerUpdated")) {
                    double time = intent.getDoubleExtra("time", 0.0);
                    timerText.setText(formatTime(time));
                }
            }
        };

        IntentFilter filter = new IntentFilter("TimerUpdated");
        getActivity().registerReceiver(broadcastReceiver, filter);

        return view;
    }

    public void resetTapped(View view) {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(requireContext());
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", (dialogInterface, i) -> {
            Intent intent = new Intent(getActivity(), TimerService.class);
            intent.setAction("RESET_TIMER");
            getActivity().startService(intent);
        });

        resetAlert.setNeutralButton("Cancel", (dialogInterface, i) -> {
            // Ничего не делаем
        });
        resetAlert.show();
    }

    public void startStopTapped(View view) {
        if (!timerStarted) {
            timerStarted = true;
            setButtonUI("STOP", R.color.navBackgroundColorPeach);
        } else {
            timerStarted = false;
            setButtonUI("START", R.color.white);
        }

        Intent intent = new Intent(getActivity(), TimerService.class);
        intent.setAction("TOGGLE_TIMER");
        getActivity().startService(intent);
    }

    private void setButtonUI(String text, int colorResId) {
        stopStartButton.setText(text);
        stopStartButton.setTextColor(ContextCompat.getColor(requireContext(), colorResId));
    }

    private String formatTime(double time) {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
