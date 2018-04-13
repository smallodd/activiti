package com.hengtian.extend.controller;

import java.io.Serializable;

public class TData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String date;
	private String name;
	private String address;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
