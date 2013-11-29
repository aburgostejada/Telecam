package com.abs.telecam.absctract;

public interface IListAdapter {
    public void reloadList();
    public Object getItemFromPosition(int position);
    public android.widget.ListAdapter getAdapter();
}
