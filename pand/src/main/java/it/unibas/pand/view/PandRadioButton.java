package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.unibas.pand.R;

public class PandRadioButton extends android.support.v7.widget.AppCompatRadioButton {
    public static final String TAG = PandRadioButton.class.getName();
    private String value;

    public PandRadioButton(Context context) {
        super(context);
    }

    public PandRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandRadioButton, 0, 0);
        value = a.getString(R.styleable.PandRadioButton_value);
        a.recycle();
        Log.d(TAG, "Value: " + value);
    }

    public String getValue(){
        return value;
    }
}
