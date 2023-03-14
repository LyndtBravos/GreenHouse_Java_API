package com.voxtelecom;

public class Client {
	
	private int userID;
	private int planID;
	private int premium;
	private int cover;
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getPlanID() {
		return planID;
	}
	public void setPlanID(int planID) {
		this.planID = planID;
	}
	public int getPremium() {
		return premium;
	}
	public void setPremium(int premium) {
		this.premium = premium;
	}
	public int getCover() {
		return cover;
	}
	public void setCover(int cover) {
		this.cover = cover;
	}
	
	public Client() {
		
	}
	
	public Client(int userID, int planID, int premium, int cover) {
		super();
		this.userID = userID;
		this.planID = planID;
		this.premium = premium;
		this.cover = cover;
	}
}