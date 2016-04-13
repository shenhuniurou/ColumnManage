package com.wxx.columnmanage;

import android.content.ContentValues;

import java.util.List;
import java.util.Map;

public interface ColumnDaoInface {
	
	public boolean addCache(ColumnItem item);

	public boolean deleteCache(String whereClause, String[] whereArgs);

	public boolean updateCache(ContentValues values, String whereClause, String[] whereArgs);

	public Map<String, String> viewCache(String selection, String[] selectionArgs);

	public List<Map<String, String>> listCache(String selection, String[] selectionArgs);

	public void clearFeedTable();
	
}
