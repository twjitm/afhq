package com.example.afhq.entity;

public class TrafficMessage {
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getApplyed() {
		return applyed;
	}
	public void setApplyed(String applyed) {
		this.applyed = applyed;
	}
	public String getSurplus() {
		return surplus;
	}
	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}
	public String getAll() {
		return all;
	}
	public void setAll(String all) {
		this.all = all;
	}
	
	
	public String getTypeContext() {
		return typeContext;
	}
	public void setTypeContext(String typeContext) {
		this.typeContext = typeContext;
	}

       /**
        * 流量说明
        */
	private String typeContext;
	private Integer id;
	/**
	 * 使用
	 */
	private String applyed;//使用了
	/**
	 * 剩余
	 */
	private String surplus;//剩余
	/**
	 * 一共
	 */
	private String all;//一共
	

}
