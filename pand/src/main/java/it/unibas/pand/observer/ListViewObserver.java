package it.unibas.pand.observer;

import android.util.Log;
import android.widget.ListView;

import java.util.List;

import it.unibas.pand.Utility;
import it.unibas.pand.adapter.PandAdapter;
import it.unibas.pand.adapter.PandBaseAdapter;
import it.unibas.pand.exception.PandException;

public class ListViewObserver extends Observer {
    public static final String TAG = ListViewObserver.class.getName();
    private PandAdapter adapter;

    public ListViewObserver(ListView listView, String beanName, String adapterName) {
        try {
            this.adapter = (PandAdapter) Utility.createObject(adapterName);
        } catch (PandException e) {
            throw new RuntimeException("No adapter found, check what you've written: " + adapterName);
        }
        setBeanName(beanName);
        listView.setAdapter(adapter);
        subscribe();
    }

    public ListViewObserver(ListView listView, String beanName, PandBaseAdapter adapter) {
        this.adapter = adapter;
        setBeanName(beanName);
        listView.setAdapter(adapter);
        subscribe();
    }

    @Override
    public void updateValue() {
        Log.d("ListViewObserver", "listview update");
        if(adapter == null) return;
        if(adapter.getItemList() == null){
            adapter.setItemList((List<?>) getValue());
        }
        adapter.notifyDataSetChanged();
    }
}
