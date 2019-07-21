package org.ccfng.viralload;


import java.sql.Date;
import org.ccfng.global.Demographics;

public class VL extends Demographics {

	private Date artStartDate;

	private Date lastVLDate;

	private String vLStatus;

	private Date vLDueDate;

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

	public Date getvLDueDate() {
		return vLDueDate;
	}

	public void setvLDueDate(Date vLDueDate) {
		this.vLDueDate = vLDueDate;
	}

	public Integer getVlCount() {
		return vlCount;
	}

	public void setVlCount(Integer vlCount) {
		this.vlCount = vlCount;
	}
}
