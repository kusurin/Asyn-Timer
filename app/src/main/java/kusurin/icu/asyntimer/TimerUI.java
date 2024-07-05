package kusurin.icu.asyntimer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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

    private States lastState = null;

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
        TimerUnitParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.marginLeft_timerUnit),context.getResources().getDimensionPixelSize(R.dimen.marginTop_timerUnit),context.getResources().getDimensionPixelSize(R.dimen.marginRight_timerUnit),context.getResources().getDimensionPixelSize(R.dimen.marginBottom_timerUnit));

        RelativeLayout.LayoutParams TimerTimeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerTimeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);

        RelativeLayout.LayoutParams TimerNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TimerNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        TimerNameParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.marginLeft_timerName),context.getResources().getDimensionPixelSize(R.dimen.marginTop_timerName),0,0);

        RelativeLayout.LayoutParams ButtonSwitchParams = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.size_timerButton), context.getResources().getDimensionPixelSize(R.dimen.size_timerButton));
        ButtonSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        ButtonSwitchParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        ButtonSwitchParams.setMargins(0,0,context.getResources().getDimensionPixelSize(R.dimen.marginRight_timerSwitch),0);

        //动态的
        buttonSwitch.setId(View.generateViewId());
        RelativeLayout.LayoutParams buttonResetParams = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.size_timerButton), context.getResources().getDimensionPixelSize(R.dimen.size_timerButton));
        buttonResetParams.addRule(RelativeLayout.LEFT_OF, buttonSwitch.getId());
        buttonResetParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        buttonResetParams.setMargins(0,0,context.getResources().getDimensionPixelSize(R.dimen.marginRight_timerReset),0);

        TimerUnit.setLayoutParams(TimerUnitParams);
        timerTime.setLayoutParams(TimerTimeParams);
        timerName.setLayoutParams(TimerNameParams);
        buttonSwitch.setLayoutParams(ButtonSwitchParams);
        buttonReset.setLayoutParams(buttonResetParams);

        buttonSwitch.setBackground(context.getDrawable(R.drawable.button_start));
        buttonReset.setBackground(context.getDrawable(R.drawable.button_delete));

        ObjectAnimator Animator = ObjectAnimator.ofFloat(TimerUnit, "alpha", 0f, 1f).setDuration(300);
        Animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                updateUI();
            }
        });
        TimerUnit.setAlpha(0f);

        container.addView(TimerUnit);

        Animator.start();


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
                if(!getTimerNameText().equals("")) {
                    timerName.setText("");
                    return;
                }
                //再点一下删除UI
                IsDeleted = true;
            }
        });
    }

    public boolean updateUI(){
        if (lastState != timer.getState()) {
            switch (timer.getState()) {
                case Running:
                    changeButton(buttonSwitch, R.drawable.button_pause);
                    if(buttonReset.getBackground().getConstantState() != context.getDrawable(R.drawable.button_reset).getConstantState()){
                        changeButton(buttonReset, R.drawable.button_reset);
                    }
                    break;
                case Stopped:
                    changeButton(buttonSwitch, R.drawable.button_start);
                    break;
                case Reseted:
                    changeButton(buttonReset, R.drawable.button_delete);
                    if(buttonSwitch.getBackground().getConstantState() != context.getDrawable(R.drawable.button_start).getConstantState()){
                        changeButton(buttonSwitch, R.drawable.button_start);
                    }
                    break;
            }
        }

        timerTime.post(new Runnable() {
            @Override
            public void run() {
                timerTime.setText(timer.getTime());
            }
        });

        lastState = timer.getState();

        return true;
    }

    public void delete() {
        final int originalHeight = TimerUnit.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = TimerUnit.getLayoutParams();
                params.height = (Integer) animation.getAnimatedValue();
                TimerUnit.setLayoutParams(params);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(TimerUnit);
            }
        });

        animator.start();
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

    private void changeButton(ImageButton button, int drawable) {
        //防止buttonReset闪动
        if(lastState == null){
            button.setBackground(context.getDrawable(drawable));
            return;
        }
        ObjectAnimator Animator = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f).setDuration(80);
        Animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                button.setBackground(context.getDrawable(drawable));
                ObjectAnimator.ofFloat(button, "alpha", 0f, 1f).setDuration(80).start();
            }
        });
        Animator.start();
    }
}

