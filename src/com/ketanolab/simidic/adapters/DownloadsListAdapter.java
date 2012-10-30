package com.ketanolab.simidic.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ketanolab.simidic.R;

public class DownloadsListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<String> names;
	private ArrayList<String> authors;
	private ArrayList<String> descriptions;
	private ArrayList<String> sizes;

	public DownloadsListAdapter(Context contexto) {
		inflater = LayoutInflater.from(contexto);
		names = new ArrayList<String>();
		authors = new ArrayList<String>();
		descriptions = new ArrayList<String>();
		sizes = new ArrayList<String>();
	}

	public int getCount() {
		return names.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.download_list_item, null);
			holder = new ViewHolder();
			holder.nameTextView = (TextView) convertView.findViewById(R.id.nameItem);
			holder.authorTextView = (TextView) convertView.findViewById(R.id.authorItem);
			holder.descriptionTextView = (TextView) convertView.findViewById(R.id.descriptionItem);
			holder.sizeTextView = (TextView) convertView.findViewById(R.id.sizeItem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameTextView.setText(names.get(position));
		holder.authorTextView.setText(authors.get(position));
		holder.descriptionTextView.setText(descriptions.get(position));
		holder.sizeTextView.setText(sizes.get(position));
		return convertView;
	}

	static class ViewHolder {
		TextView nameTextView;
		TextView authorTextView;
		TextView descriptionTextView;
		TextView sizeTextView;
	}

	public void adicionarItem(String name, String author, String description, String size) {
		names.add(name);
		authors.add(author);
		descriptions.add(description);
		sizes.add(size);
		notifyDataSetChanged();
	}

	public void eliminarTodo() {
		names.clear();
		authors.clear();
		descriptions.clear();
		notifyDataSetChanged();
	}

}
