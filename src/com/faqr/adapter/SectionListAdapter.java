/*
 * Copyright (c) eneve software 2013. All rights reserved.
 */

package com.faqr.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

/**
 * Provides a listview with section each with a header
 * 
 * @author eneve
 */
public class SectionListAdapter extends BaseAdapter {

    public class IndexPath {
        public int section;
        public int row;
    }

    public interface SectionListAdapterDelegate {
        public int sectionCount();
        public int rowsInSection(int section);
        public View viewForRowAtIndexPath(IndexPath path, ViewGroup parent);
        public View viewForHeaderInSection(int section);
        public void itemSelectedAtIndexPath(IndexPath path);
    }

    public SectionListAdapterDelegate delegate;

    public OnItemClickListener itemClickListener = new OnItemClickListener() {

        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            IndexPath path = SectionListAdapter.this.indexPathForPosition(position);

            if (path.row >= 0) {
                delegate.itemSelectedAtIndexPath(path);
            }

        }

    };

    public int getCount() {
        int count = 0;
        int sections = delegate.sectionCount();
        for (int i = 0; i < sections; i++) {
            count += delegate.rowsInSection(i);
        }
        return count + sections;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public IndexPath indexPathForPosition(int position) {
        IndexPath path = new IndexPath();
        for (int i = 0; i < delegate.sectionCount(); i++) {
            int rows = delegate.rowsInSection(i);
            if (position - rows - 1 >= 0) {
                position -= (rows + 1);
            } else {
                path.section = i;
                path.row = position - 1;
                return path;
            }
        }
        return path;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        IndexPath path = indexPathForPosition(position);
        if (path.row == -1) {
            return delegate.viewForHeaderInSection(path.section);
        } else {
            return delegate.viewForRowAtIndexPath(path, parent);
        }
    }
}