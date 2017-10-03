package it.unibas.pand.model;

import android.app.Activity;
import android.util.Log;

import com.google.dexmaker.stock.ProxyBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibas.pand.Pand;
import it.unibas.pand.Utility;
import it.unibas.pand.observer.Observer;

public class Model implements IModel{
    public static final String TAG = Model.class.getName();
    final Map<String, Object> beanMap = new HashMap<>();
    Map<Activity, Map<String, List<Observer>>> observerMapActivity = new HashMap<>();

    public Object putBean(String id, Object bean){
        Object proxyBean = getProxyBean(id, bean);
        this.notifyAll(id);
        return proxyBean;
    }

    public Object putBeanWithoutNotify(String id, Object bean){
        return getProxyBean(id, bean);
    }

    private Object getProxyBean(String name, Object bean){
        Object proxyBean = wrapProxyBean(name, bean);
        this.beanMap.put(name, proxyBean);
        return proxyBean;
    }

    Object wrapProxyBean(String name, Object bean) {
        Object proxyBean;
        if (ProxyBuilder.isProxyClass(bean.getClass()) || !bean.getClass().getName().startsWith(Utility.getBasePackage(Pand.getInstance().getApplication()))) {
            proxyBean = bean;
        } else if (bean instanceof Collection){
            proxyBean = bean;
        } else {
            try {
                proxyBean = ProxyBuilder.forClass(bean.getClass())
                        .handler(new BeanInvocationHandler(name))
                        .build();
                copyFields(name, bean, proxyBean, bean.getClass());
                Log.i(TAG, "Put the proxy bean " + proxyBean.getClass() + ", property " + name);
            } catch (IOException e) {
                Log.e(TAG, "IOException, cannot create a proxy for object " + name);
                proxyBean = bean;
            } catch (IncompatibleClassChangeError e) {
                Log.e(TAG, "IncompatibleClassChangeError, cannot create a proxy for object " + name);
                proxyBean = bean;
            }
        }
        return proxyBean;
    }

    void copyFields(String name, Object src, Object dest, Class<?> klass) {
        Log.i(TAG, "Class " + klass.getName());
        Field[] fields = klass.getDeclaredFields();
        for (Field f : fields) {
            Log.i(TAG, "Field " + f.getName());
            f.setAccessible(true);
            copyFieldValue(name, src, dest, f);
            Log.i(TAG, "Copied field value");
        }

        klass = klass.getSuperclass();
        if (klass != null && klass != Object.class) {
            copyFields(name, src, dest, klass);
        }
    }

    private void copyFieldValue(String name, Object src, Object dest, Field f) {
        try {
            Object value = f.get(src);
            if(value == null){
                return;
            }
            if(!f.getType().isPrimitive() && !isPrimitiveWrapper(f.getType())){
                Log.i(TAG, "field not primitive, wrapping");
                value = getProxyBean(name + "." + f.getName(), value);
            }
            f.set(dest, value);
        } catch (Exception e) {
            //throw new RuntimeException(property + "." + f.getName() + ": " + f.getType() + " - "+ e);
        }
    }

    private boolean isPrimitiveWrapper(java.lang.reflect.Type type){
        return type == String.class || type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class;
    }

    public Object getBean(String id){
        return beanMap.get(id);
    }

    public Object popBean(String id){
        Object object = getBean(id);
        removeBean(id);
        return object;
    }

    public void removeBean(String id){
        beanMap.remove(id);
    }

    public Object getBeanValue(String beanName) {
        String[] beanNameArray = beanName.split("\\.");
        Object bean = null;
        for(String dotName : beanNameArray) {
            Log.d(TAG, "Retrieving " + dotName);
            if(bean == null){
                Log.d(TAG, "Bean null, get from the model");
                bean = getBean(dotName);
            } else {
                Log.d(TAG, "Bean not null, search for child");
                try {
                    Class c = bean.getClass();
                    Field field = Utility.getField(c, dotName);
                    if(field!= null) {
                        field.setAccessible(true);
                        bean = field.get(bean);
                    } else {
                        try {
                            Method method = bean.getClass().getMethod("get" + Utility.capitalizeFirstLetter(dotName));
                            bean = method.invoke(bean);
                        } catch (InvocationTargetException e) {
                            Log.e(TAG, "InvocationTargetException: " + e.getLocalizedMessage());
                            return null;
                        } catch (NoSuchMethodException e) {
                            Log.e(TAG, "NoSuchMethodException: " + e.getLocalizedMessage());
                            return null;
                        }
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "IllegalAccessException: " + e.getLocalizedMessage());
                    return null;
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, "NoSuchFieldException: " + e.getLocalizedMessage() + " in bean " + bean.getClass().getName());
                    return null;
                }
            }
        }
        return bean;
    }

    public Object getBeanValue(String[] beanNameArray) {
        Object bean = null;
        for(String beanName : beanNameArray) {
            Log.d(TAG, "Retrieving " + beanName);
            if(bean == null){
                Log.d(TAG, "Bean null, get from the model");
                bean = getBean(beanName);
            } else {
                Log.d(TAG, "Bean not null, search for child");
                try {
                    Class c = bean.getClass();
                    Field field = Utility.getField(c, beanName);
                    bean = field.get(bean);
                } catch (IllegalAccessException e) {
                    Log.d(TAG, "IllegalAccessException: " + e.getLocalizedMessage());
                    return null;
                } catch (NoSuchFieldException e) {
                    Log.d(TAG, "NoSuchFieldException: " + e.getLocalizedMessage() + " in bean " + bean.getClass().getName());
                    return null;
                }
            }
        }
        return bean;
    }

    public void setBeanValue(String name, Object value){
        String[] beanNameArray = Utility.beanNameToArray(name);
        String[] beanTreeArray = Utility.getBeanTreeArray(beanNameArray);
        String propertyName = Utility.getProperty(beanNameArray);

        if(beanTreeArray.length == 0){
            Log.d(TAG, "No bean wrapper, should be direct into the model");
            beanMap.put(propertyName, value);
            Log.d(TAG, "Field " + propertyName + " changed to: " + getBean(propertyName));
            notifyChange(name);
            return;
        }
        Object bean = getBeanValue(Utility.getBeanTreeArray(beanNameArray));
        //Object bean = getBeanValue(property);
        try {
            Field field = Utility.getField(bean.getClass(), propertyName);
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if(fieldType.isPrimitive() || isPrimitiveWrapper(fieldType)) {
                value = Utility.convertType(value, fieldType);
            }
            field.set(bean, value);
            Log.d(TAG, "Field changed to: " + value);
            notifyChange(name);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "No field " + propertyName + " in bean " + name);
        } catch (NullPointerException e) {
            Log.e(TAG, "No bean " + name + " found in model");
            for(StackTraceElement line : e.getStackTrace()) {
                Log.e(TAG, line.toString());
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Can't access to property " + propertyName + " in " + name);
        }
    }

    public void addObserver(String beanDotName, Observer observer){
        Activity currentActivity = Pand.getInstance().getCurrentActivity();
        Map<String, List<Observer>> observerMap = observerMapActivity.get(currentActivity);
        if(observerMap == null){
            observerMap = new HashMap<>();
            observerMapActivity.put(currentActivity, observerMap);
        }
        List<Observer> observerList = observerMap.get(beanDotName);
        if(observerList == null){
            observerList = new ArrayList();
            observerMap.put(beanDotName, observerList);
        }
        observerList.add(observer);
    }

    public Map<String, List<Observer>> getObserverMap() {
        Activity currentActivity = Pand.getInstance().getCurrentActivity();
        return observerMapActivity.get(currentActivity);
    }

    public Map<Activity, Map<String, List<Observer>>> getObserverMapActivity() {
        return observerMapActivity;
    }

    public void cleanObserverForActivity(Activity activity){
        observerMapActivity.remove(activity);
    }

    public void removeObserver(String beanName, Observer observer){
        Activity currentActivity = Pand.getInstance().getCurrentActivity();
        List<Observer> observerList = observerMapActivity.get(currentActivity).get(beanName);
        if(observerList != null){
            observerList.remove(observer);
        }
    }

    public void notifyChange(final String beanName){
        Log.i(TAG, "Notify change: " + beanName);
        final Activity currentActivity = Pand.getInstance().getCurrentActivity();
        if(currentActivity == null){
            return;
        }
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, List<Observer>> observeMap = observerMapActivity.get(currentActivity);
                if(observeMap == null){
                    return;
                }
                List<Observer> observerList = observeMap.get(beanName);
                if(observerList != null){
                    for(Observer o : observerList){
                        o.updateValue();
                    }
                }
            }
        });
    }

    public void notifyAll(final String beanName){
        Log.i(TAG, "Notify add: " + beanName);
        final Activity currentActivity = Pand.getInstance().getCurrentActivity();
        if(currentActivity == null){
            return;
        }
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, List<Observer>> observerMap = observerMapActivity.get(currentActivity);
                if(observerMap == null){
                    return;
                }
                for (Map.Entry<String, List<Observer>> entry : observerMap.entrySet()){
                    if(entry.getKey().startsWith(beanName)){
                        for(Observer o : entry.getValue()){
                            o.updateValue();
                        }
                    }
                }
            }
        });
    }

    public void refreshActivity(final Activity activity){
        if(activity == null){
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, List<Observer>> observerMap = observerMapActivity.get(activity);
                if(observerMap == null){
                    return;
                }
                for (Map.Entry<String, List<Observer>> entry : observerMap.entrySet()){
                    for(Observer o : entry.getValue()){
                        o.updateValue();
                    }

                }
            }
        });
    }
}
