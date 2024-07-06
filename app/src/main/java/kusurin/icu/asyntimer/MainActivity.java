package kusurin.icu.asyntimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private Toolbar Toolbar = null;

    private LinearLayout TimerList = null;

    private long TimeStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark, null));

        Toolbar = findViewById(R.id.toolbar);
        ImageButton StartAllButton = findViewById(R.id.StartAllButton);
        setSupportActionBar(Toolbar);

        TimerList = findViewById(R.id.TimerList);

        Vector<TimerUIState> timerCaches = loadTimerUICaches(this);

        Vector<TimerUI> timerUIs = new Vector<>();

        //版权信息
        Toolbar.setNavigationOnClickListener(v -> {
            //Toast.makeText(MainActivity.this, "© kusurin.icu", Toast.LENGTH_SHORT).show();
            imgToast("© kusurin.icu", R.drawable.kusurin_icu);
        });

        //Navigation全部开始
        StartAllButton.setOnClickListener(v -> {
            for (TimerUI timerUI : timerUIs.subList(0, timerUIs.size() - 1)){
                timerUI.start();
            }
        });


        for (TimerUIState timerCache : timerCaches) {
            timerUIs.add(new TimerUI(this, timerCache.TimeStart, timerCache.TimeSum, timerCache.timeState, timerCache.TimerNameText, timerCache.TimerIndex, TimerList));

            DragSortHelper.setDragSort(TimerList.getChildAt(TimerList.getChildCount()-1), timerUIs);
        }

        final boolean[] NeedUpdateCache = {false};

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
                            NeedUpdateCache[0] = false;
                            for (TimerUI timerUI : timerUIs) {
                                timerUI.updateUI();
                                if(timerUI.WillChange()){
                                    NeedUpdateCache[0] = true;
                                }
                                timerUI.beenChanged();
                            }
                            if (NeedUpdateCache[0]) {
                                for (int i = 0; i < timerUIs.size(); i++) {
                                    timerCaches.set(i, new TimerUIState(timerUIs.get(i).getTimeStart(), timerUIs.get(i).getTimeSum(), timerUIs.get(i).getState(), timerUIs.get(i).getTimerNameText(), timerUIs.get(i).getTimerIndex()));

                                    if(timerUIs.get(i).isDeleted()){
                                        timerUIs.get(i).delete();
                                        timerUIs.remove(i);
                                        timerCaches.remove(i);
                                    }
                                }

                                saveTimerUICaches(timerCaches, MainActivity.this);
                                NeedUpdateCache[0] = false;
                            }
                            //假如没有TimerUI，创建一个
                            if (timerUIs.size() == 0) {
                                timerUIs.add(new TimerUI(MainActivity.this, 0, 0, States.Reseted, "", timerUIs.size(), TimerList));
                                timerCaches.add(new TimerUIState(0, 0, States.Reseted, "", timerUIs.size()));

                                DragSortHelper.setDragSort(TimerList.getChildAt(TimerList.getChildCount()-1), timerUIs);
                            }
                            //假如最后一个timerUI不在活动状态，创建一个
                            if (timerUIs.lastElement().getState() != States.Reseted || !timerUIs.lastElement().getTimerNameText().equals("")) {
                                timerUIs.add(new TimerUI(MainActivity.this, 0, 0, States.Reseted, "", timerUIs.size(), TimerList));
                                timerCaches.add(new TimerUIState(0, 0, States.Reseted, "", timerUIs.size()));

                                DragSortHelper.setDragSort(TimerList.getChildAt(TimerList.getChildCount()-1), timerUIs);
                            }
                        }
                    });
                }
            }
        }).start();
    }


    public void saveTimerUICaches(Vector<TimerUIState> timerCaches, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(timerCaches);

        SharedPreferences sharedPreferences = context.getSharedPreferences("TimerCachePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TimerCacheList", json);
        editor.apply();
    }

    public Vector<TimerUIState> loadTimerUICaches(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TimerCachePrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("TimerCacheList", null);
        Type type = new TypeToken<Vector<TimerUIState>>() {}.getType();
        Gson gson = new Gson();
        Vector<TimerUIState> timerCaches = gson.fromJson(json, type);

        if (timerCaches == null) {
            timerCaches = new Vector<>();
        }

        return timerCaches;
    }

    public void imgToast(String text, int imgResId) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);

        LinearLayout toastView = (LinearLayout) toast.getView();
        toastView.setOrientation(LinearLayout.HORIZONTAL);
        toastView.setGravity(Gravity.CENTER_VERTICAL);
        toastView.setClipToPadding(false);

        ImageView toastImage = new ImageView(this);
        toastImage.setImageResource(imgResId);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(this.getResources().getDimensionPixelSize(R.dimen.fontSize_toast), this.getResources().getDimensionPixelSize(R.dimen.fontSize_toast));
        imgParams.setMargins(getResources().getDimensionPixelSize(R.dimen.marginLeft_toast_icon), 0, 0, getResources().getDimensionPixelSize(R.dimen.marginBottom_toast_icon));
        toastImage.setLayoutParams(imgParams);
        toastView.addView(toastImage, -1);

        TextView textView = (TextView) toastView.getChildAt(0);
        textView.setTextSize(0,getResources().getDimensionPixelSize(R.dimen.fontSize_toast));
        textView.setLetterSpacing(0.02f);

        toast.show();
    }
}
