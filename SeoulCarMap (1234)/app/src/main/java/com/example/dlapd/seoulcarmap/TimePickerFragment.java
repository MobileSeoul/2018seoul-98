package com.example.dlapd.seoulcarmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //현재 시간을 타임 피커의 초기값으로 사용
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_DARK, this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));

        //타임 피커의 타이틀 설정
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("TimepickerDialog 타이틀");
        tvTitle.setBackgroundColor(Color.parseColor("#ffEEE8AA"));
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tv1 = (TextView) getActivity().findViewById(R.id.optimeStart);
        TextView tv2 = (TextView) getActivity().findViewById(R.id.optimeFinish);
        String aMpM = "AM";
        if(hourOfDay > 11){
            aMpM = "PM";
        }
        int currentHour;
        if(hourOfDay > 11){
            currentHour = hourOfDay-12;
        }
        else{
            currentHour = hourOfDay;
        }

        tv1.setText(String.valueOf(hourOfDay) + "시 ");
    }
}
