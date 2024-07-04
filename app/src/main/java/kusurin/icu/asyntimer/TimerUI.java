package kusurin.icu.asyntimer;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerUI {
    private Context context;

    private Timer timer = null;

    private int TimerID = 0;
    private LinearLayout container = null;
    private RelativeLayout TimerUnit = null;
    private ImageButton buttonSwitch = null;
    private ImageButton buttonReset = null;
    private TextView timerTime = null;
    private EditText timerName = null;

    private boolean WillChange = false;

    private boolean IsDeleted = false;

    TimerUI(Context context, long timeStart, long timeSum, States timeState,String TimerNameText,int TimerID, LinearLayout container){
        this.context = context;

        this.TimerID = TimerID;
        this.TimerUnit = new RelativeLayout(context,null,0,R.style.TimerUnit);
        this.buttonSwitch = new ImageButton(context,null,0,R.style.TimerSwitch);
        this.buttonReset = new ImageButton(context,null,0,R.style.TimerReset);
        this.timerTime = new TextView(context,null,0,R.style.TimerTime);
        this.timerName = new EditText(context,null,0,R.style.TimerName);

        timerName.setText(TimerNameText);

        this.container = container;

        buildUI();

        this.timer = new Timer(timeStart, timeSum, timeState); //woc神奇

        timerTime.post(new Runnable() {
            @Override
            public void run() {
                timerTime.setText(timer.getTime());
            }
        });
    }

    private void buildUI(){
        //布局
        TimerUnit.addView(timerTime);
        TimerUnit.addView(timerName);
        TimerUnit.addView(buttonSwitch);
        TimerUnit.addView(buttonReset);

        //一些静态的LayoutParams
        LinearLayout.LayoutParams TimerUnitParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerUnitParams.setMargins(dp2Px(10), dp2Px(10), dp2Px(10), dp2Px(10));

        RelativeLayout.LayoutParams TimerTimeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerTimeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);

        RelativeLayout.LayoutParams TimerNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        TimerNameParams.setMargins(dp2Px(10),dp2Px(-30),0,0);

        RelativeLayout.LayoutParams ButtonSwitchParams = new RelativeLayout.LayoutParams(dp2Px(50), dp2Px(50));
        ButtonSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        ButtonSwitchParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        ButtonSwitchParams.setMargins(0,0,dp2Px(10),0);

        //动态的
        buttonSwitch.setId(View.generateViewId());
        RelativeLayout.LayoutParams buttonResetParams = new RelativeLayout.LayoutParams(dp2Px(50), dp2Px(50));
        buttonResetParams.addRule(RelativeLayout.LEFT_OF, buttonSwitch.getId());
        buttonResetParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        buttonResetParams.setMargins(0,0,dp2Px(20),0);

        TimerUnit.setLayoutParams(TimerUnitParams);
        timerTime.setLayoutParams(TimerTimeParams);
        timerName.setLayoutParams(TimerNameParams);
        buttonSwitch.setLayoutParams(ButtonSwitchParams);
        buttonReset.setLayoutParams(buttonResetParams);

        container.addView(TimerUnit);

        //绑定
        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.switchState();
                WillChange = true;
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WillChange = true;
                if(timer.getState() != States.Reseted) {
                    timer.reset();
                    return;
                }
                //再点一下重置名字
                if(timer.getState() == States.Reseted && !getTimerNameText().equals("")) {
                    timerName.setText("");
                    return;
                }
                //再点一下删除UI
                IsDeleted = true;
            }
        });
    }

    public boolean updateUI(){
        if (timer.getState() != States.Running) {
            if(timer.getState() == States.Reseted) {
                timerTime.post(new Runnable() {
                    @Override
                    public void run() {
                        timerTime.setText("");
                    }
                });
            }
            return false;
        }

        timerTime.post(new Runnable() {
            @Override
            public void run() {
                timerTime.setText(timer.getTime());
            }
        });
        return true;
    }

    public void delete() {
        container.removeView(TimerUnit);
    }

    public void beenChanged() {
        WillChange = false;
    }

    //timerUI的状态，淦不想写链式
    public boolean WillChange() {
        return WillChange;
    }

    public long getTimeStart() {
        return timer.getTimeStart();
    }

    public long getTimeSum() {
        return timer.getTimeSum();
    }

    public States getState() {
        return timer.getState();
    }

    public String getTimerNameText() {
        return timerName.getText().toString();
    }

    public int getTimerID() {
        return TimerID;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public int dp2Px(int dp) {
        if(dp > 0)
            return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
        return (int) (dp * context.getResources().getDisplayMetrics().density - 0.5f);
    }
}

