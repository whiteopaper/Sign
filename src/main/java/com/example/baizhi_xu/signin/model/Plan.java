package com.example.baizhi_xu.signin.model;

import java.io.Serializable;

public class Plan implements Serializable {

	private int pid;
	private String cname;
	private String coursename;
	private String tname;
	private int cnum;
	private String gpsplace;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCoursename() {
		return coursename;
	}
	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public int getCnum() {
		return cnum;
	}
	public void setCnum(int cnum) {
		this.cnum = cnum;
	}
	public String getGpsplace() {
		return gpsplace;
	}
	public void setGpsplace(String gpsplace) {
		this.gpsplace = gpsplace;
	}
	
}
