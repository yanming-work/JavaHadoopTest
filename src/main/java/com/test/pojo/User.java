package com.test.pojo;

public class User {

	/**
	 * �û���
	 */
	private String userName;
	/**
	 * �ǳ�
	 */
	private String nickName;
	/**
	 * ����
	 */
	private String password;
	/**
	 * �Ƿ��Ծ״̬��1Ϊ��Ծ״̬��0Ϊ����״̬
	 */
	private int alive;
	/**
	 * �û���ݣ�1Ϊ����Ա��0Ϊһ���û�
	 */
	private int role;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAlive() {
		return alive;
	}
	public void setAlive(int alive) {
		this.alive = alive;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	
	
}
