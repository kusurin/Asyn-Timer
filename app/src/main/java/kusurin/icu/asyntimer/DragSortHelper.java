package kusurin.icu.asyntimer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

public class DragSortHelper {

    private DragSortHelper() {
    }

    public static void setDragSort(View settingView, Vector<TimerUI> timerUIs) {
        settingView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(final View hoveringView, DragEvent event) {
                ViewGroup viewParent = (ViewGroup) hoveringView.getParent();
                View dragingView = (View) event.getLocalState();
                if (viewParent.indexOfChild(dragingView) == viewParent.getChildCount() - 1 || viewParent.indexOfChild(hoveringView) == viewParent.getChildCount() - 1) {
                    return false;
                }
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        if (hoveringView != dragingView) {
                            swapView(viewParent, hoveringView, dragingView, timerUIs);
                        } else {
                            return false;
                        }
                        break;
                }
                return true;
            }
        });
        settingView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.startDragAndDrop(null, new View.DragShadowBuilder(view), view, 0);
                return true;
            }
        });
    }

    private static void swapView(ViewGroup viewParent, View a, View b, Vector<TimerUI> timerUIs) {
        int aIndex = viewParent.indexOfChild(a);
        int bIndex = viewParent.indexOfChild(b);

        //a和b淡出
        ObjectAnimator aAlpha = ObjectAnimator.ofFloat(a, "alpha", 1f, 0.2f).setDuration(120);
        ObjectAnimator bAlpha = ObjectAnimator.ofFloat(b, "alpha", 1f, 0.2f).setDuration(120);

        aAlpha.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                bAlpha.start();
            }
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                viewParent.removeView(a);
                viewParent.removeView(b);

                if (aIndex < bIndex) {
                    viewParent.addView(b, aIndex);
                    viewParent.addView(a, bIndex);

                } else {
                    viewParent.addView(a, bIndex);
                    viewParent.addView(b, aIndex);
                }

                aAlpha.ofFloat(a, "alpha", 0.2f, 1f).setDuration(120).start();
                bAlpha.ofFloat(b, "alpha", 0.2f, 1f).setDuration(120).start();
            }
        });

        aAlpha.start();

        //不会架构是这样的
        TimerUI temp = timerUIs.get(aIndex);
        timerUIs.set(aIndex, timerUIs.get(bIndex));
        timerUIs.set(bIndex, temp);
        timerUIs.get(aIndex).Change();
    }
}