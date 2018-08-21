package cn.yclin.pedometer;


import java.util.ArrayList;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 */
public class StepDisplayer implements StepListener {
    private int mSteps = 0;

    public StepDisplayer(){
        super();
    }

    @Override
    public void onStep() {
        ++mSteps;
        notifyListener();
    }

    public void setSteps(int steps) {
        this.mSteps = steps;
    }

    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }

    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener lsr) {
        mListeners.add(lsr);
    }

    private void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged(mSteps);
        }
    }


}
