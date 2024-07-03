package kusurin.icu.asyntimer;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Timer  {
    private long TimeStart = 0;
    private long TimeSum = 0;
    private States timeState = States.Reseted;

    private TextView TimerTime = null;
    private ImageButton ButtonSwitch = null;
    private ImageButton ButtonReset = null;

    //记得最后把Timer和元素解耦，让TimerUI耦合
    Timer(ImageButton ButtonSwitch, ImageButton ButtonReset, long timeStart, long timeSum, States timeState, TextView TimerTime) {
        this.ButtonSwitch = ButtonSwitch;
        this.ButtonReset = ButtonReset;
        this.TimerTime = TimerTime;
        this.TimeStart = timeStart;
        this.TimeSum = timeSum;
        this.timeState = timeState;

        ButtonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchState();
            }
        });

        ButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    public String switchState() {
        switch (timeState) {
            case Stopped:
            case Reseted:
                TimeStart = System.currentTimeMillis();
                timeState = States.Running;
                return "Running";
            case Running:
                TimeSum = TimeSum + (System.currentTimeMillis() - TimeStart);
                TimeStart = 0;
                timeState = States.Stopped;
                return "Stopped";
            default :
                return "Failed";
        }
    }

    public String reset() {
        TimeStart = 0;
        TimeSum = 0;
        timeState = States.Reseted;
        return "Reseted";
    }

    public boolean UpdateTime() {
        if (timeState != States.Running) {
            if(timeState == States.Reseted) {
                TimerTime.post(new Runnable() {
                    @Override
                    public void run() {
                        TimerTime.setText("");
                    }
                });
            }
            return false;
        }

        long TimeEnd = System.currentTimeMillis();
        long TimeDiff = TimeEnd - TimeStart + TimeSum;

        long TimeHour = 0;
        long TimeMinute = TimeDiff / 60000;
        long TimeSecond = ((TimeDiff % 3600000) % 60000) / 1000;

        if (TimeMinute >= 60) {
            TimeHour = TimeMinute / 60;
            TimeMinute = TimeMinute % 60;
        }

        String TimeHourString = TimeHour == 0 ? "" : TimeHour + ":";
        String TimeMinuteString = String.format("%02d", TimeMinute);
        String TimeSecondString = String.format("%02d", TimeSecond);

        final String time = TimeHourString + TimeMinuteString + ":" + TimeSecondString;

        TimerTime.post(new Runnable() {
            @Override
            public void run() {
                TimerTime.setText(time);
            }
        });

        return true;
    }

    public States getState() {
        return timeState;
    }
}