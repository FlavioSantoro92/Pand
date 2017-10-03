package it.unibas.pand.adapter;

import android.widget.BaseAdapter;

import java.util.List;

public abstract class PandAdapter<Item> extends BaseAdapter{
    private List<Item> itemList;

    public void setItemList(List<Item> itemList){
        this.itemList = itemList;
    }

    public List<?> getItemList() {
        return itemList;
    }
}
