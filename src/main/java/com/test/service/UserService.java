package com.test.service;

import com.test.pojo.User;

public interface UserService {
	/**
	 * 登录
	 * @param user 用户名和密码的简单封装
	 * @return 登录成功返回完整信息
	 */
	public User login(User user);

	/**
	 * 注册
	 * @param user 
	 * @return
	 */
	public int register(User user);
}