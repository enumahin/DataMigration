package org.ccfng.viralload;


import org.ccfng.global.Demographics;

import java.sql.Date;

public class VL extends Demographics {

	private Date artStartDate;

	private Date lastVLDate;

	private String vLStatus;

	private Date vlduedate;

	private Integer vlCount;

	public VL() {
	}

	public Date getArtStartDate() {
		return artStartDate;
	}

	public void setArtStartDate(Date artStartDate) {
		this.artStartDate = artStartDate;
	}

	public Date getLastVLDate() {
		return lastVLDate;
	}

	public void setLastVLDate(Date lastVLDate) {
		this.lastVLDate = lastVLDate;
	}

	public String getvLStatus() {
		return vLStatus;
	}

	public void setvLStatus(String vLStatus) {
		this.vLStatus = vLStatus;
	}

	public Date getVlduedate() {
		return vlduedate;
	}

	public void setVlduedate(Date vlduedate) {
		this.vlduedate = vlduedate;
	}

	public Integer getVlCount() {
		return vlCount;
	}

	public void setVlCount(Integer vlCount) {
		this.vlCount = vlCount;
	}
}
