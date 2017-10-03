package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import it.unibas.pand.Pand;
import it.unibas.pand.R;
import it.unibas.pand.linker.LinkerUtility;
import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.IValidator;
import it.unibas.pand.linker.ILinker;
import it.unibas.pand.observer.DatePickerObserver;

public class PandDatePicker extends DatePicker implements ILinker {
    public static final String TAG = PandDatePicker.class.getName();
    private List<IValidator> convalidatorList = new ArrayList<>();
    private String linkToBean;

    public PandDatePicker(Context context) {
        super(context);
    }

    public PandDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandDatePicker, 0, 0);
        linkToBean = a.getString(R.styleable.PandDatePicker_linkToBean);
        String observeBean = a.getString(R.styleable.PandDatePicker_observeBean);
        a.recycle();
        new DatePickerObserver(this, observeBean);
    }

    @Override
    public void addValidator(IValidator c) {
        convalidatorList.add(c);
    }

    @Override
    public List<ErrorMessage> checkAllValidator() {
        List<ErrorMessage> errorMessageList = LinkerUtility.checkAllConvalidator(convalidatorList, getValue().toString());
        if(errorMessageList != null){
            Log.e(TAG, "Got " + errorMessageList.size() + " errors, not linked to the model");
            StringBuilder errorString = new StringBuilder();
            boolean isFirst = true;
            for(ErrorMessage errorMessage : errorMessageList){
                if(!isFirst){
                    errorString.append("\n");
                } else {
                    isFirst = false;
                }
                errorString.append(errorMessage.getMessage());
            }
            return errorMessageList;
        }
        return null;
    }

    @Override
    public List<ErrorMessage> link() {
        Log.d(TAG, "Set the value " + getValue() + " into the model - " + linkToBean);
        Pand.getInstance().getModel().setBeanValue(linkToBean, getValue());
        return null;
    }

    @Override
    public GregorianCalendar getValue() {
        return new GregorianCalendar(this.getYear(), this.getMonth(), this.getDayOfMonth());
    }

    @Override
    public void clear() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        updateDate(year, month, day);
    }
}
