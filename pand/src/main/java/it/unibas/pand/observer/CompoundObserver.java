package it.unibas.pand.observer;

import android.widget.CompoundButton;

public class CompoundObserver extends Observer {
    private CompoundButton compoundButton;

    public CompoundObserver(CompoundButton compoundButton, String beanName){
        this.compoundButton = compoundButton;
        setBeanName(beanName);
        subscribe();
    }

    @Override
    public void updateValue() {
        if(getValue() != null) {
            Boolean value = (Boolean) getValue();
            compoundButton.setChecked(value);
        }
    }
}
