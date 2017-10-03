package it.unibas.pand.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.unibas.pand.Pand;

/**
 * Base class for an Adapter
 *
 * This adapter implementation use the ViewHolder design pattern,
 * and provide an easy implementation.
 *
 */
public abstract class PandBaseAdapter<Item, VH extends PandBaseAdapter.ViewHolder> extends PandAdapter<Item> {
    private List<Item> itemList;
    private LayoutInflater layoutInflater = LayoutInflater.from(Pand.getInstance().getCurrentActivity());

    /**
     * Return the ID of the view that will be inflated
     * @return the ID of the xml layout
     */
    public abstract int getViewId();

    /**
     * ViewHolder class, the developer have to exend this class
     * and add the views as field.
     */
    public static abstract class ViewHolder{
        public ViewHolder(View row){}
    }

    /**
     * Return a new instance of a {@link ViewHolder}
     *
     * @param view
     * @return VH
     */
    protected abstract VH getViewHolder(View view);

    /**
     * Bind the {@link ViewHolder} with the item at the position specified.
     *
     * @param holder ViewHolder
     * @param item Item to show
     * @param position
     */
    public abstract void onBindViewHolder(VH holder, Item item, int position);

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        VH holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(getViewId(), viewGroup, false);
            holder = getViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }
        onBindViewHolder(holder, getItem(i), i);
        return convertView;
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
