package it.unibas.pand.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.dexmaker.stock.ProxyBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;

public abstract class PandDynamicAdapter<Item> extends PandAdapter<Item> {
    public static final String TAG = PandDynamicAdapter.class.getName();
    private List<Item> itemList;
    private LayoutInflater layoutInflater = LayoutInflater.from(Pand.getInstance().getCurrentActivity());

    /**
     * Return the ID of the view that will be inflated
     * @return the ID of the xml layout
     */
    public abstract int getViewId();

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        DynamicViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(getViewId(), viewGroup, false);
            holder = new DynamicViewHolder((ViewGroup) convertView);
            convertView.setTag(holder);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, getItem(i));
        return convertView;
    }

    private void onBindViewHolder(DynamicViewHolder holder, Item item){
        for(DynamicAdapterView view : holder.getItemList()){
            String itemProperty = view.getItemProperty();
            if(itemProperty == null || itemProperty.equals("")){
                continue;
            }
            Log.d(TAG, "Item found, property: " + itemProperty);
            Object value = getGetterCall(this, itemProperty, item);
            if(value == null){
                value = getFieldValue(this, itemProperty);
            }
            if(value == null) {
                value = getGetterCall(item, itemProperty);
            }
            if(value == null){
                value = getFieldValue(item, itemProperty);
            }
            if(value != null){
                Log.d(TAG, "setValue " + String.valueOf(value));
                view.setValue(value);
            } else {
                Log.d(TAG, "null value");
            }
        }
    }

    private Object getGetterCall(PandDynamicAdapter customViewHolder, String itemProperty, Item item){
        try {
            Class c;
            if(ProxyBuilder.isProxyClass(item.getClass())){
                c = item.getClass().getSuperclass();
            } else {
                c = item.getClass();
            }
            Method getter = customViewHolder.getClass().getDeclaredMethod(obtainGetterMethod(itemProperty), c);
            getter.setAccessible(true);
            return getter.invoke(customViewHolder, item);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException: " + e.getLocalizedMessage());
            return null;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getLocalizedMessage());
            return null;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException: " + e.getLocalizedMessage());
            return null;
        }
    }

    private Object getGetterCall(Object item, String itemProperty){
        try {
            Method getter = item.getClass().getDeclaredMethod(obtainGetterMethod(itemProperty));
            getter.setAccessible(true);
            return getter.invoke(item);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException: " + e.getLocalizedMessage());
            return null;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getLocalizedMessage());
            return null;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException: " + e.getLocalizedMessage());
            return null;
        }
    }

    private Object getFieldValue(Object item, String itemProperty){
        try {
            Field field = item.getClass().getDeclaredField(itemProperty);
            field.setAccessible(true);
            Object value = field.get(item);
            return value;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getLocalizedMessage());
            return null;
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException: " + e.getLocalizedMessage());
            return null;
        }
    }

    private String obtainGetterMethod(String fieldName){
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    @Override
    public Item getItem(int i) {
        if(itemList == null)
            return null;
        return itemList.get(i);
    }

    @Override
    public int getCount() {
        if(itemList == null)
            return 0;
        return itemList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setItemList(List<Item> itemList){
        this.itemList = itemList;
    }

    public List<?> getItemList() {
        return itemList;
    }
}
