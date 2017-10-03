package it.unibas.pand.model;

import android.content.Context;
import android.util.Log;

import com.google.dexmaker.stock.ProxyBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import it.unibas.pand.Utility;
import it.unibas.pand.exception.PandException;
import it.unibas.pand.persistance.DAOException;
import it.unibas.pand.persistance.DAOJson;

public class PersistentModel extends Model {
    public static final String TAG = PersistentModel.class.getName();
    public static final String KEY_CLASS_NAME = "key_class";
    private Context context;
    private DAOJson daoJson;
    private Map<String, String> keyClassMap = new HashMap<>();

    public PersistentModel(Context context) {
        this.context = context;
        daoJson = new DAOJson(context);
        keyClassMap = (Map<String, String>) load(KEY_CLASS_NAME, keyClassMap.getClass());
        if(keyClassMap == null){
            keyClassMap = new HashMap<>();
        }
    }

    @Override
    public Object getBean(String id) {
        Object object = super.getBean(id);
        if(object != null){
            return object;
        }
        Object persistentObject = load(id);
        if(persistentObject != null){
            Object proxyBean = super.wrapProxyBean(id, persistentObject);
            beanMap.put(id, proxyBean);
            return proxyBean;
        }
        return null;
    }

    @Override
    public Object putBean(String id, Object bean) {
        updateKeyClassMap(id, bean);
        save(id, bean);
        return super.putBean(id, bean);
    }

    @Override
    public Object putBeanWithoutNotify(String id, Object bean) {
        updateKeyClassMap(id, bean);
        return super.putBeanWithoutNotify(id, bean);
    }

    @Override
    public void removeBean(String id) {
        remove(id);
        super.removeBean(id);
    }

    @Override
    public void notifyChange(String beanPropertyName) {
        if(beanPropertyName == null || beanPropertyName.equals("")){
            return;
        }
        super.notifyChange(beanPropertyName);
        saveBean(beanPropertyName);

    }

    @Override
    public void notifyAll(String beanName) {
        if(beanName == null || beanName.equals("")){
            return;
        }
        super.notifyAll(beanName);
        saveBean(beanName);
    }

    private void saveBean(String beanDotName){
        String beanName = beanDotName.split("\\.")[0];
        Object bean = getBean(beanName);
        updateKeyClassMap(beanName, bean);
        Log.i(TAG, "NotifyChange and save " + beanName);
        save(beanName, bean);
    }

    private Class getBeanType(String beanName){
        Class type = null;
        try {
            String beanClassName = keyClassMap.get(beanName);
            if(beanClassName == null){
                return null;
            }
            type = Utility.getObjectType(beanClassName);
        } catch (PandException e) {
            return null;
        }
        if(type != null){
            return type;
        }
        return Object.class;
    }

    private void updateKeyClassMap(String key, Object bean){
        String className;
        if(ProxyBuilder.isProxyClass(bean.getClass())){
            className = bean.getClass().getSuperclass().getName();
        } else {
            className = bean.getClass().getName();
        }
        keyClassMap.put(key.split("\\.")[0], className);
        save(KEY_CLASS_NAME, keyClassMap);
    }


    private Object load(String key, Class type) {
        File file = new File(context.getFilesDir(), getFileName(key));
        if (!file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return daoJson.load(key, in, type);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private Object load(String key) {
        Log.d(TAG, "load file " + key);
        Class type = getBeanType(key);
        if(type == null){
            Log.d(TAG, "class null diocan");
            return null;
        }
        Log.d(TAG, "class " + type.getName());
        File file = new File(context.getFilesDir(), getFileName(key));
        if (!file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return daoJson.load(key, in, type);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private void save(String beanName, Object bean) {
        Log.d(TAG, "save into file " + beanName);
        Log.d(TAG, "tostring: " + bean.toString());
        File file = new File(context.getFilesDir(), getFileName(beanName));
        try {
            daoJson.save(bean, new FileOutputStream(file));
            Log.d(TAG, "salvato correttamente in " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            throw new DAOException(e.getLocalizedMessage());
        }
    }

    private void remove(String key){
        String filename = key.split("\\.")[0];
        try {
            File file = new File(context.getFilesDir(), getFileName(filename));
            file.delete();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            throw new DAOException(e.getLocalizedMessage());
        }
    }

    private String getFileName(String key) {
        return key + ".pand";
    }
}
