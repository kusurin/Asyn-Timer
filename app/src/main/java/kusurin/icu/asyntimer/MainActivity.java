package kusurin.icu.asyntimer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private Toolbar Toolbar = null;

    private LinearLayout TimerList = null;

    private long TimeStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(Toolbar);

        TimerList = findViewById(R.id.TimerList);

        TimerUI timerUI = new TimerUI(this, 0, 0, States.Reseted, TimerList);
        TimerUI timerUI1 = new TimerUI(this, 0, 0, States.Reseted, TimerList);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerUI.updateUI();
                            timerUI1.updateUI();
                        }
                    });
                }
            }
        }).start();
    }
}

