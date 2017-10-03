package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;
import it.unibas.pand.R;
import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.IValidator;
import it.unibas.pand.linker.ILinker;
import it.unibas.pand.linker.LinkerUtility;
import it.unibas.pand.observer.CompoundObserver;

public class PandSwitch extends Switch implements ILinker {
    public static final String TAG = PandSwitch.class.getName();
    private List<IValidator> convalidatorList = new ArrayList<>();
    private String linkToBean;

    public PandSwitch(Context context) {
        super(context);
    }

    public PandSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandSwitch, 0, 0);
        linkToBean = a.getString(R.styleable.PandSwitch_linkToBean);
        String observeBean = a.getString(R.styleable.PandSwitch_observeBean);
        a.recycle();
        new CompoundObserver(this, observeBean);
    }

    @Override
    public void addValidator(IValidator c) {
        convalidatorList.add(c);
    }

    @Override
    public List<ErrorMessage> checkAllValidator() {
        List<ErrorMessage> errorMessageList = LinkerUtility.checkAllConvalidator(convalidatorList, isChecked());
        if(errorMessageList != null){
            Log.e(TAG, "Got " + errorMessageList.size() + " errors, not linked to the model");
            return errorMessageList;
        }
        return null;
    }

    @Override
    public List<ErrorMessage> link() {
        Log.d(TAG, "Set the value " + isChecked() + " into the model");
        Pand.getInstance().getModel().setBeanValue(linkToBean, isChecked());
        return null;
    }

    @Override
    public Boolean getValue() {
        return isChecked();
    }

    @Override
    public void clear() {
        setChecked(false);
    }
}
