package com.wxx.columnmanage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DragAdapter extends BaseAdapter {
	
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 列表数据是否改变 */
	private boolean isListChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的频道列表） */
	public List<ColumnItem> channelList;
	/** TextView 频道内容 */
	private TextView item_text;
	/** 删除图标 */
	private ImageView deleteView;
	/** 要删除的position */
	public int remove_position = -1;

	private boolean isShowDelete = false;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示

	public void setIsShowDelete(boolean isShowDelete) {
		this.isShowDelete = isShowDelete;
		notifyDataSetChanged();
	}

	public boolean isShowDelete() {
		return this.isShowDelete;
	}

	public DragAdapter(Context context, List<ColumnItem> channelList) {
		this.context = context;
		this.channelList = channelList;
	}
	
	@Override
	public int getCount() {
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ColumnItem getItem(int position) {
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.text_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		deleteView = (ImageView) view.findViewById(R.id.ivDelete);
		ColumnItem channel = getItem(position);
		item_text.setText(channel.getName());
		//设置删除按钮是否显示
		if(position != 0) {
			deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);
		}
		if (position == 0) {
			item_text.setEnabled(false);
		}
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + channelList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}
		return view;
	}

	/** 添加频道列表 */
	public void addItem(ColumnItem channel) {
		channelList.add(channel);
		isListChanged = true;
		notifyDataSetChanged();
	}

	/** 拖动变更频道排序 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		ColumnItem dragItem = getItem(dragPostion);
		if (dragPostion < dropPostion) {
			channelList.add(dropPostion + 1, dragItem);
			channelList.remove(dragPostion);
		} else {
			channelList.add(dropPostion, dragItem);
			channelList.remove(dragPostion + 1);
		}
		isChanged = true;
		isListChanged = true;
		notifyDataSetChanged();
	}
	
	/** 获取频道列表 */
	public List<ColumnItem> getColumnList() {
		return channelList;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public void remove() {
		channelList.remove(remove_position);
		remove_position = -1;
		isListChanged = true;
		notifyDataSetChanged();
	}
	
	/** 设置频道列表 */
	public void setListDate(List<ColumnItem> list) {
		channelList = list;
	}
	
	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/** 排序是否发生改变 */
	public boolean isListChanged() {
		return isListChanged;
	}
	
	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
	
	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}
}