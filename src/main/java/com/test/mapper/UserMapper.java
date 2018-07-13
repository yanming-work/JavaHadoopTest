package com.test.mapper;

import com.test.pojo.User;

public interface UserMapper
{
	User findByUserName(String userName);
	User findByIdAndPwd(User user);
	int register(User user);
}
