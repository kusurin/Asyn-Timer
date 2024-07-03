package kusurin.icu.asyntimer;

public class Timer  {
    private long TimeStart = 0;
    private long TimeSum = 0;
    private States timeState = States.Reseted;

    //把Timer和元素解耦，让TimerUI耦合
    Timer(long timeStart, long timeSum, States timeState) {
        this.TimeStart = timeStart;
        this.TimeSum = timeSum;
        this.timeState = timeState;
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

    public String getTime() {
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

        return TimeHourString + TimeMinuteString + ":" + TimeSecondString;
    }

    public States getState() {
        return timeState;
    }
}