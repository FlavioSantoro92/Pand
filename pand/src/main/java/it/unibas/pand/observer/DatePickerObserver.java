package it.unibas.pand.observer;

import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerObserver extends Observer {
    private DatePicker datePicker;

    public DatePickerObserver(DatePicker datePicker, String beanName){
        this.datePicker = datePicker;
        setBeanName(beanName);
        subscribe();
    }

    @Override
    public void updateValue() {
        Calendar calendar = (Calendar) getValue();
        if(calendar != null) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePicker.updateDate(year, month, day);
        }
    }
}
