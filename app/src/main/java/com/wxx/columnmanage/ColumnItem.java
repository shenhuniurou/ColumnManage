package com.wxx.columnmanage;

import java.io.Serializable;

/** 
 * 栏目
 *  */
public class ColumnItem implements Serializable {
	
	private static final long serialVersionUID = -6465237897027410019L;
	
	public int id;
	
	public String name;
	
	public Integer orderId;
	
	public Integer selected;

	public ColumnItem() {}

	public ColumnItem(int id, String name, int orderId, int selected) {
		this.id = id;
		this.name = name;
		this.orderId = Integer.valueOf(orderId);
		this.selected = Integer.valueOf(selected);
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getOrderId() {
		return this.orderId.intValue();
	}

	public Integer getSelected() {
		return this.selected;
	}

	public void setId(int paramInt) {
		this.id = paramInt;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public void setOrderId(int paramInt) {
		this.orderId = Integer.valueOf(paramInt);
	}

	public void setSelected(Integer paramInteger) {
		this.selected = paramInteger;
	}

	public String toString() {
		return "ColumnItem [id=" + this.id + ", name=" + this.name + ", selected=" + this.selected + "]";
	}
	
}