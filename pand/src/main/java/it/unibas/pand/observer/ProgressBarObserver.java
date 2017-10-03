package it.unibas.pand.observer;

import android.util.Log;
import android.widget.ProgressBar;

public class ProgressBarObserver extends Observer{
    private ProgressBar progressBar;

    public ProgressBarObserver(ProgressBar progressBar, String beanName){
        this.progressBar = progressBar;
        setBeanName(beanName);
        subscribe();
    }

    @Override
    public void updateValue() {
        if(getValue() != null) {
            Log.i("ProgressBarObserver", "getvalue = " + getValue());
            int value = (Integer) getValue();
            progressBar.setProgress(value);
        }
    }
}
