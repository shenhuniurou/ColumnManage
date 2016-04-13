package com.wxx.columnmanage;

import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColumnManage {
	
	public static ColumnManage columnManage;
	/**
	 * 默认的用户选择频道列表
	 * */
	public static List<ColumnItem> defaultUserColumns;
	/**
	 * 默认的其他频道列表
	 * */
	public static List<ColumnItem> defaultOtherColumns;
	private ColumnDao columnDao;
	
	/** 判断数据库中是否存在用户数据 */
	
	private boolean userExist = false;
	
	static {
		defaultUserColumns = new ArrayList<>();
		defaultOtherColumns = new ArrayList<>();
		defaultUserColumns.add(new ColumnItem(1, "推荐", 1, 1));
		defaultUserColumns.add(new ColumnItem(2, "热点", 2, 1));
		defaultUserColumns.add(new ColumnItem(3, "本地", 3, 1));
		defaultUserColumns.add(new ColumnItem(4, "视频", 4, 1));
		defaultUserColumns.add(new ColumnItem(5, "订阅", 5, 1));
		defaultUserColumns.add(new ColumnItem(6, "社会", 6, 1));
		defaultUserColumns.add(new ColumnItem(7, "娱乐", 7, 1));
		defaultUserColumns.add(new ColumnItem(8, "图片", 1, 0));
		defaultUserColumns.add(new ColumnItem(9, "科技", 2, 0));
		defaultUserColumns.add(new ColumnItem(10, "汽车", 3, 0));
		defaultUserColumns.add(new ColumnItem(11, "体育", 4, 0));
		defaultUserColumns.add(new ColumnItem(12, "财经", 5, 0));
		defaultOtherColumns.add(new ColumnItem(13, "女人", 6, 0));
		defaultOtherColumns.add(new ColumnItem(14, "旅游", 7, 0));
		defaultOtherColumns.add(new ColumnItem(15, "健康", 8, 0));
		defaultOtherColumns.add(new ColumnItem(16, "美女", 9, 0));
		defaultOtherColumns.add(new ColumnItem(17, "游戏", 10, 0));
		defaultOtherColumns.add(new ColumnItem(18, "国外", 11, 0));
		defaultOtherColumns.add(new ColumnItem(19, "手机", 12, 0));
		defaultOtherColumns.add(new ColumnItem(20, "星座", 13, 0));
		defaultOtherColumns.add(new ColumnItem(21, "段子", 14, 0));
		defaultOtherColumns.add(new ColumnItem(22, "时尚", 15, 0));
		defaultOtherColumns.add(new ColumnItem(23, "精选", 16, 0));
		defaultOtherColumns.add(new ColumnItem(24, "美文", 17, 0));
		defaultOtherColumns.add(new ColumnItem(25, "故事", 18, 0));
		defaultOtherColumns.add(new ColumnItem(26, "数码", 19, 0));
		defaultOtherColumns.add(new ColumnItem(27, "养生", 20, 0));
		defaultOtherColumns.add(new ColumnItem(28, "历史", 21, 0));
		defaultOtherColumns.add(new ColumnItem(29, "探索", 22, 0));

	}

	private ColumnManage(SQLHelper paramDBHelper) throws SQLException {
		if (columnDao == null)
			columnDao = new ColumnDao(paramDBHelper.getContext());
		return;
	}

	/**
	 * 初始化频道管理类
	 * @param dbHelper
	 * @throws SQLException
	 */
	public static ColumnManage getManage(SQLHelper dbHelper)throws SQLException {
		if (columnManage == null)
			columnManage = new ColumnManage(dbHelper);
		return columnManage;
	}

	/**
	 * 清除所有的频道
	 */
	public void deleteAllColumn() {
		columnDao.clearFeedTable();
	}

	/**
	 * 根据栏目ID来删除某个栏目
	 * @param id
	 */
	public void deleteColumnById(int id) {
		columnDao.deleteCache(SQLHelper.ID + "= ?", new String[]{ String.valueOf(id) });
	}
	
	/**
	 * 获取用户的频道
	 * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
	 */
	public List<ColumnItem> getUserColumn() {
		Object cacheList = columnDao.listCache(SQLHelper.SELECTED + "= ?", new String[] { "1" });
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			userExist = true;
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			List<ColumnItem> list = new ArrayList<ColumnItem>();
			for (int i = 0; i < count; i++) {
				ColumnItem navigate = new ColumnItem();
				navigate.setId(Integer.parseInt(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		initDefaultColumn();
		return defaultUserColumns;
	}
	
	/**
	 * 获取其他的频道
	 * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
	 */
	public List<ColumnItem> getOtherColumn() {
		Object cacheList = columnDao.listCache(SQLHelper.SELECTED + "= ?" ,new String[] { "0" });
		List<ColumnItem> list = new ArrayList<ColumnItem>();
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ColumnItem navigate= new ColumnItem();
				navigate.setId(Integer.parseInt(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		if(userExist){
			return list;
		}
		cacheList = defaultUserColumns;
		return (List<ColumnItem>) cacheList;
	}

	/**
	 * 获取数据库中的所有频道
	 * @return
	 */
	public List<ColumnItem> getAllColumns () {
		Object cacheList = columnDao.listCache(null, null);
		List<ColumnItem> list = new ArrayList<>();
		if (cacheList != null && !((List) cacheList).isEmpty()) {
			List<Map<String, String>> maplist = (List) cacheList;
			int count = maplist.size();
			for (int i = 0; i < count; i++) {
				ColumnItem navigate= new ColumnItem();
				navigate.setId(Integer.parseInt(maplist.get(i).get(SQLHelper.ID)));
				navigate.setName(maplist.get(i).get(SQLHelper.NAME));
				navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
				navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
				list.add(navigate);
			}
			return list;
		}
		cacheList = defaultUserColumns;
		return (List<ColumnItem>) cacheList;
	}
	
	/**
	 * 保存用户频道到数据库
	 * @param userList
	 */
	public void saveUserColumn(List<ColumnItem> userList) {
		for (int i = 0; i < userList.size(); i++) {
			ColumnItem ColumnItem = (ColumnItem) userList.get(i);
			ColumnItem.setOrderId(i);
			ColumnItem.setSelected(Integer.valueOf(1));
			columnDao.addCache(ColumnItem);
		}
	}
	
	/**
	 * 保存其他频道到数据库
	 * @param otherList
	 */
	public void saveOtherColumn(List<ColumnItem> otherList) {
		for (int i = 0; i < otherList.size(); i++) {
			ColumnItem ColumnItem = (ColumnItem) otherList.get(i);
			ColumnItem.setOrderId(i);
			ColumnItem.setSelected(Integer.valueOf(0));
			columnDao.addCache(ColumnItem);
		}
	}
	
	/**
	 * 初始化数据库内的频道数据
	 */
	private void initDefaultColumn(){
		deleteAllColumn();
		saveUserColumn(defaultUserColumns);
		saveOtherColumn(defaultOtherColumns);
	}
	
}
