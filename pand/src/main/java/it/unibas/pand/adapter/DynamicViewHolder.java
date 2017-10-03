package it.unibas.pand.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DynamicViewHolder {
    public static final String TAG = DynamicViewHolder.class.getName();
    private List<DynamicAdapterView> itemList = new ArrayList<>();

    public DynamicViewHolder(ViewGroup viewGroup) {
        navigateTree(viewGroup);
    }

    private void navigateTree(ViewGroup viewGroup){
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if(view instanceof DynamicAdapterView){
                itemList.add((DynamicAdapterView) view);
            } else if(view instanceof ViewGroup){
                navigateTree((ViewGroup) view);
            }
        }
    }

    public List<DynamicAdapterView> getItemList() {
        return itemList;
    }
}
