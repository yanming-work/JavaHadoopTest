package com.test.service;

import com.test.pojo.User;

public interface UserService {
	/**
	 * ��¼
	 * @param user �û���������ļ򵥷�װ
	 * @return ��¼�ɹ�����������Ϣ
	 */
	public User login(User user);

	/**
	 * ע��
	 * @param user 
	 * @return
	 */
	public int register(User user);
}