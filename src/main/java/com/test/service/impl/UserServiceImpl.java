package com.test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.test.mapper.UserMapper;
import com.test.pojo.User;
import com.test.service.UserService;

@Component("userService")
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserMapper userMapper;
	
	public User login(User user)
	{
		return userMapper.findByIdAndPwd(user);
	}

	public int register(User user)
	{
		return userMapper.register(user);
	}

}
