package kusurin.icu.asyntimer;

public class TimerUIState {
    public long TimeStart = 0;
    public long TimeSum = 0;
    public States timeState = States.Reseted;
    public String TimerNameText = "";
    public int TimerIndex = 0;

    TimerUIState(long timeStart, long timeSum, States timeState, String TimerNameText, int TimerIndex) {
        this.TimeStart = timeStart;
        this.TimeSum = timeSum;
        this.timeState = timeState;
        this.TimerNameText = TimerNameText;
        this.TimerIndex = TimerIndex;
    }
}