package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import it.unibas.pand.R;
import it.unibas.pand.adapter.DynamicAdapterView;
import it.unibas.pand.observer.Observer;
import it.unibas.pand.observer.TextViewObserver;

public class PandTextView extends android.support.v7.widget.AppCompatTextView implements DynamicAdapterView {
    public static final String TAG = PandTextView.class.getName();
    private Observer observer;
    private String formatter;
    private String itemProperty;

    public PandTextView(Context context) {
        super(context);
    }

    public PandTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(observer != null){
            observer.updateValue();
        }
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandTextView, 0, 0);
        String observeBean = a.getString(R.styleable.PandTextView_observeBean);
        formatter = a.getString(R.styleable.PandTextView_formatText);
        itemProperty = a.getString(R.styleable.PandTextView_itemProperty);
        a.recycle();
        if(observeBean != null) {
            observer = new TextViewObserver(this, observeBean);
        }
    }

    public void setFormattedText(Object text){
        if(formatter != null){
            text = String.format(formatter, text);
        }
        setText(String.valueOf(text));
    }

    @Override
    public String getItemProperty() {
        return itemProperty;
    }

    @Override
    public void setValue(Object value) {
        setFormattedText(value);
    }
}
