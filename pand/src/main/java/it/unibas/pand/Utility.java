package it.unibas.pand;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Arrays;

import it.unibas.pand.exception.PandException;

public class Utility {
    public static final String TAG = Utility.class.getName();

    public static String getBasePackage(Context context){
        return context.getPackageName();
    }

    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> tmpClass = clazz;
        do {
            try {
                Field f = tmpClass.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                tmpClass = tmpClass.getSuperclass();
            }
        } while (tmpClass != null);

        throw new NoSuchFieldException("Field '" + fieldName
                + "' not found on class " + clazz);
    }

    public static String[] beanNameToArray(String beanName){
        if(beanName == null)
            return new String[0];
        return beanName.split("\\.");
    }

    public static String[] getBeanTreeArray(String[] beanArray){
        if(beanArray.length <= 1)
            return new String[0];
        return Arrays.copyOfRange(beanArray, 0, beanArray.length - 1);
    }

    public static String getProperty(String[] beanArray){
        if(beanArray.length == 0){
            return "";
        }
        return beanArray[beanArray.length - 1];
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static Class getObjectType(String className) throws PandException{
        Class object = null;
        try {
            object = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class " + className + " not found ");
            throw new PandException("Class " + className + " not found ");
        } catch (NullPointerException e) {
            Log.e(TAG, "Nullpointer exception creating " + className);
            throw new PandException("Nullpointer exception creating " + className + " - " + e);
        }
        Log.i(TAG, "Create " + className);
        return object;
    }

    public static Object createObject(String className) throws PandException {
        Object object = null;
        try {
            object = Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "Class " + className + " not found ");
            throw new PandException("Class " + className + " not found ");
        } catch (InstantiationException e) {
            Log.e(TAG, e.toString());
            throw new PandException("Can't istantiate the class " + className + " - " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.toString());
            throw new PandException("Illegal access to class " + className + " - " + e);
        } catch (NullPointerException e) {
            Log.e(TAG, e.toString());
            throw new PandException("Nullpointer exception creating " + className + " - " + e);
        }
        Log.i(TAG, "Create " + className);
        return object;
    }

    /**
     * Convert a generic object to relative field type
     *
     * @param value value to convert
     * @param fieldType type
     * @return Object value
     */
    public static Object convertType(Object value, Class<?> fieldType){
        if (String.class.equals(fieldType)) {
            return value;
        }
        boolean defaultValue = false;
        if(value.toString().equals("")){
            defaultValue = true;
        }
        if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
            if(defaultValue)
                value = (double) 0.0d;
            else
                value = Double.parseDouble(value.toString());
        } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
            if(defaultValue)
                value = (float) 0.0f;
            else
                value = Float.parseFloat(value.toString());
        } else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            if(defaultValue)
                value = (long) 0;
            else
                value = Long.parseLong(value.toString());
        } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            if(defaultValue)
                value = (int) 0;
            else
                value = Integer.parseInt(value.toString());
        } else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
            if(defaultValue)
                value = (short) 0;
            else
                value = Short.parseShort(value.toString());
        } else if (Character.class.equals(fieldType) || char.class.equals(fieldType)) {
            if(defaultValue)
                value = (char) '\u0000';
            else
                value = value.toString().charAt(0);
        } else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
            if(defaultValue)
                value = (byte) 0;
            else
                value = Byte.parseByte(value.toString());
        } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            if(defaultValue)
                value = false;
            else
                value = Boolean.parseBoolean(value.toString());
        }
        return value;
    }
}
