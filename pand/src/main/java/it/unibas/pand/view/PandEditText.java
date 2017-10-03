package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;
import it.unibas.pand.R;
import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.IValidator;
import it.unibas.pand.linker.ILinker;
import it.unibas.pand.linker.LinkerUtility;
import it.unibas.pand.observer.TextViewObserver;

public class PandEditText extends android.support.v7.widget.AppCompatEditText implements ILinker {
    public static final String TAG = PandEditText.class.getName();
    private List<IValidator> validatorList = new ArrayList<>();
    private String linkToBean;

    public PandEditText(Context context) {
        super(context);
    }

    public PandEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandEditText, 0, 0);
        linkToBean = a.getString(R.styleable.PandEditText_linkToBean);
        String observeBean = a.getString(R.styleable.PandEditText_observeBean);
        a.recycle();
        new TextViewObserver(this, observeBean);
    }

    @Override
    public void addValidator(IValidator c) {
        validatorList.add(c);
    }

    @Override
    public List<ErrorMessage> checkAllValidator() {
        List<ErrorMessage> errorMessageList = LinkerUtility.checkAllConvalidator(validatorList, getText().toString());
        if(errorMessageList != null){
            Log.e(TAG, "Got " + errorMessageList.size() + " errors, not linked to the model");
            StringBuilder errorString = new StringBuilder();
            boolean isFirst = true;
            for(ErrorMessage errorMessage : errorMessageList){
                errorMessage.setCustomErrorView(true);
                if(!isFirst){
                    errorString.append("\n");
                } else {
                    isFirst = false;
                }
                errorString.append(errorMessage.getMessage());
            }
            this.setError(errorString.toString());
            return errorMessageList;
        }
        return null;
    }

    @Override
    public List<ErrorMessage> link() {
        Log.d(TAG, "Set the value " + getText() + " into the model - " + linkToBean);
        Pand.getInstance().getModel().setBeanValue(linkToBean, getValue());
        return null;
    }

    @Override
    public String getValue() {
        return getText().toString();
    }

    public int getIntegerValue(){
        try {
            return Integer.parseInt(getText().toString());
        } catch (NumberFormatException e){
            return 0;
        }
    }

    @Override
    public void clear() {
        setText("");
    }
}
