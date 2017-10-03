package it.unibas.pand.model;

import android.util.Log;

import com.google.dexmaker.stock.ProxyBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import it.unibas.pand.Pand;

public class BeanInvocationHandler implements InvocationHandler {
    public static final String TAG = BeanInvocationHandler.class.getName();
    private String beanName;

    public BeanInvocationHandler(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = ProxyBuilder.callSuper(proxy, method, args);
        if(proxy instanceof Collection || proxy instanceof Map){
            Log.d(TAG, "Proxy of a list/map, ignore");
            return result;
        }
        PointCut annotation = method.getAnnotation(PointCut.class);
        if(annotation != null){
            Log.d(TAG, "Intercepted pointcut method " + method.getName() + " - " + this.beanName);
            String propertyName = annotation.property();
            if(!propertyName.equals("")) {
                Pand.getInstance().getModel().notifyChange(this.beanName + "." + propertyName);
            } else {
                Pand.getInstance().getModel().notifyAll(this.beanName);
            }
        } else {
            String startWith = startWithAny(method.getName());
            if (startWith != null) {
                Log.d(TAG, "Intercepted method " + method.getName() + " - " + this.beanName);
                String propertyName = getPropertyName(method.getName(), startWith);
                Log.d(TAG, "PointCut property " + propertyName);
                Pand.getInstance().getModel().notifyChange(this.beanName + "." + propertyName);
            }
        }
        return result;
    }

    private String startWithAny(String name){
        for(String prefix : Pand.getInstance().getMethodStartWithList()){
            if(name.startsWith(prefix))
                return prefix;
        }
        return null;
    }

    private String getPropertyName(String methodName, String prefix){
        Log.d(TAG, "Method property: " + methodName);
        Log.d(TAG, "Prefix: " + prefix);
        String propertyName = methodName.substring(prefix.length());
        if(propertyName.length() == 0){
            return "";
        }
        if(propertyName.length() == 1){
            return propertyName.substring(0, 1).toLowerCase();
        }
        return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
    }
}