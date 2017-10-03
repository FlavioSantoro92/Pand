package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

import it.unibas.pand.R;
import it.unibas.pand.observer.Observer;
import it.unibas.pand.observer.ProgressBarObserver;

public class PandProgressBar extends ProgressBar {
    public static final String TAG = PandProgressBar.class.getName();
    private Observer observer;

    public PandProgressBar(Context context) {
        super(context);
    }

    public PandProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context,attrs);
    }

    public PandProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context,attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(observer != null){
            observer.updateValue();
        }
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandProgressBar, 0, 0);
        String observeBean = a.getString(R.styleable.PandProgressBar_observeBean);
        a.recycle();
        observer = new ProgressBarObserver(this, observeBean);
    }
}
