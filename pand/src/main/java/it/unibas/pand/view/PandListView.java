package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

import it.unibas.pand.R;
import it.unibas.pand.adapter.PandBaseAdapter;
import it.unibas.pand.observer.ListViewObserver;


public class PandListView extends ListView {
    public static final String TAG = PandListView.class.getName();
    private String observeBean;
    private String adapterName;

    private PandBaseAdapter baseAdapter;

    public PandListView(Context context) {
        super(context);
    }

    public PandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PandListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttributes(context, attrs);
    }

    public void setObserveBean(String observeBean){
        this.observeBean = observeBean;
        if(observeBean != null && adapterName != null) {
            Log.d(TAG, "set adapter with " + observeBean + " - " + adapterName);
            ListViewObserver observer = new ListViewObserver(this, this.observeBean, this.adapterName);
            observer.updateValue();
        }
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandListView, 0, 0);
        observeBean = a.getString(R.styleable.PandListView_observeBean);
        adapterName = a.getString(R.styleable.PandListView_adapter);
        a.recycle();
        if(observeBean != null && adapterName != null) {
            Log.d(TAG, "set adapter 2 with " + observeBean + " - " + adapterName);
            new ListViewObserver(this, observeBean, adapterName);
        }
    }
}
