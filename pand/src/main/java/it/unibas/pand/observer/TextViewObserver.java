package it.unibas.pand.observer;

import android.widget.TextView;

import it.unibas.pand.view.PandTextView;

public class TextViewObserver extends Observer {
    private TextView textView;

    public TextViewObserver(TextView textView, String beanName){
        this.textView = textView;
        setBeanName(beanName);
        subscribe();
    }

    @Override
    public void updateValue() {
        Object value = getValue();
        if(value != null) {
            if(textView instanceof PandTextView){
                ((PandTextView) textView).setFormattedText(value);
            } else {
                textView.setText(String.valueOf(value));
            }
        }
    }
}
