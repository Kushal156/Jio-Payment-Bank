package com.jpb.DTO;

public class UserSearchRequestDto {

	private String application_id;
	private String mobile_no;
	
	public String getApplication_id() {
		return application_id;
	}
	public void setApplication_id(String application_id) {
		this.application_id = application_id;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	@Override
	public String toString() {
		return "UserSearchRequestDto [application_id=" + application_id + ", mobile_no=" + mobile_no + "]";
	}
	
	
}
