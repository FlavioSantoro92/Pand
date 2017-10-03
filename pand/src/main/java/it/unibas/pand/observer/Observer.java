package it.unibas.pand.observer;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import it.unibas.pand.Pand;
import it.unibas.pand.Utility;
import it.unibas.pand.model.IModel;

public abstract class Observer {
    public static final String TAG = Observer.class.getName();
    private String beanName;
    private int viewId;

    public int getViewId() {
        return viewId;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public void subscribe(){
        if(beanName != null && !beanName.equals("")){
            Log.i(TAG, this.getClass().getName() + " subscribe with property " + beanName);
            Pand.getInstance().getModel().addObserver(beanName, this);
        }
    }

    public Object getValue(){
        IModel model = Pand.getInstance().getModel();
        String[] beanNameArray = Utility.beanNameToArray(beanName);
        String[] beanTreeArray = Utility.getBeanTreeArray(beanNameArray);
        String propertyName = Utility.getProperty(beanNameArray);

        Object bean = model.getBeanValue(beanTreeArray);
        if(bean == null){
            //This is not a bean, should be a primitive
            return model.getBean(propertyName);
        }

        Object value;

        try{
            Method method = bean.getClass().getMethod("get" + Utility.capitalizeFirstLetter(propertyName));
            method.setAccessible(true);
            value = method.invoke(bean);
        } catch (NoSuchMethodException e) {
            Field field = getField(bean.getClass(), propertyName);
            if(field!= null) {
                field.setAccessible(true);
                try {
                    value = field.get(bean);
                } catch (IllegalAccessException e1) {
                    value = null;
                    Log.e(TAG, "Can't access to property " + beanName);
                }
            } else {
                value = null;
                Log.e(TAG, "No field " + beanName);
            }
            Log.e(TAG, "Can't access to virtualized property " + beanName);
        } catch (IllegalAccessException e) {
            value = null;
            Log.e(TAG, "Can't access to property " + beanName);
        } catch (InvocationTargetException e) {
            value = null;
            Log.e(TAG, "Can't validate virtualized property " + beanName);
        }

        /*try {
            Field field = getField(bean.getClass(), propertyName);
            if(field!= null) {
                field.setAccessible(true);
                value = field.get(bean);
            } else {
                Method method = bean.getClass().getMethod("get" + Utility.capitalizeFirstLetter(propertyName));
                value = method.invoke(bean);
            }
            Log.d(TAG, "Field found: " + value);
        } catch (NullPointerException e) {
            value = null;
            Log.e(TAG, "No bean " + beanName + " found in model");
        } catch (IllegalAccessException e) {
            value = null;
            Log.e(TAG, "Can't access to property " + beanName);
        } catch (NoSuchMethodException e) {
            value = null;
            Log.e(TAG, "Can't access to virtualized property " + beanName);
        } catch (InvocationTargetException e) {
            value = null;
            Log.e(TAG, "Can't validate virtualized property " + beanName);
        }*/
        return value;
    }

    private Field getField(Class<?> clazz, String name) {
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) {
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }

    public abstract void updateValue();
}
