package kusurin.icu.asyntimer;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerUI {
    private Context context;

    private Timer timer = null;

    private LinearLayout container = null;
    private RelativeLayout TimerUnit = null;
    private ImageButton buttonSwitch = null;
    private ImageButton buttonReset = null;
    private TextView timerTime = null;



    TimerUI(Context context, long timeStart, long timeSum, States timeState, LinearLayout container){
        this.context = context;

        this.TimerUnit = new RelativeLayout(context,null,0,R.style.TimerUnit);
        this.buttonSwitch = new ImageButton(context,null,0,R.style.TimerSwitch);
        this.buttonReset = new ImageButton(context,null,0,R.style.TimerReset);
        this.timerTime = new TextView(context,null,0,R.style.TimerTime);

        this.container = container;

        buildUI();

        this.timer = new Timer(buttonSwitch, buttonReset, timeStart, timeSum, timeState, timerTime);
    }

    private void buildUI(){
        //布局
        TimerUnit.addView(timerTime);
        TimerUnit.addView(buttonSwitch);
        TimerUnit.addView(buttonReset);

        //一些静态的LayoutParams
        LinearLayout.LayoutParams TimerUnitParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerUnitParams.setMargins(dp2Px(10), dp2Px(10), dp2Px(10), dp2Px(10));

        RelativeLayout.LayoutParams TimerTimeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerTimeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);

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
        buttonSwitch.setLayoutParams(ButtonSwitchParams);
        buttonReset.setLayoutParams(buttonResetParams);

        container.addView(TimerUnit);

        //绑定
        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.switchState();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.reset();
            }
        });
    }

    public void updateUI(){
        timer.UpdateTime();
    }

    public int dp2Px(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
