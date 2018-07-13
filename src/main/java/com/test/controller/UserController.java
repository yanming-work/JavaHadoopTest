package com.test.controller;


import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.test.pojo.ResultJson;
import com.test.pojo.User;
import com.test.service.UserService;
import com.test.util.CodeConfig;

/**
 * 用户请求控制器
 * 
 */
@Controller
// 声明当前类为控制器
@RequestMapping("/user")
// 声明当前类的路径
public class UserController {
	@Resource(name = "userService")
	private UserService userService;// 由Spring容器注入一个UserService实例

	/**
	 * 登录
	 * 
	 * @param user
	 *            用户
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/login")
	// 声明当前方法的路径
	public String login(User user, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		response.setContentType("application/json");// 设置响应内容格式为json
		User result = userService.login(user);// 调用UserService的登录方法
		request.getSession().setAttribute("user", result);
		if (result != null) {
			createHadoopFSFolder(result);
			return "console";
		}
		return "login";
		
		
	}

	/**
	 * 注册（只允许注册普通用户）
	 * 
	 * @param user
	 *            用户
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/register")
	public ResultJson register(User user, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		user.setRole(CodeConfig.GENERUSER);// 不允许通过注册方式注册管理员
		int lines = userService.register(user);
		ResultJson result = new ResultJson();
		if (lines == CodeConfig.SUCCESS) {
			result.setCode(CodeConfig.SUCCESS);
			result.setMsg("注册成功");
		} else {
			result.setCode(CodeConfig.FAILED);
			result.setMsg("注册失败");
		}
		return result;
	}

	public void createHadoopFSFolder(User user) throws IOException {
		Configuration conf = new Configuration();
		conf.addResource(new Path("/usr/lib/hadoop/hadoop-2.8.4/etc/hadoop/core-site.xml"));
		conf.addResource(new Path("/usr/lib/hadoop/hadoop-2.8.4/etc/hadoop/hdfs-site.xml"));

		FileSystem fileSystem = FileSystem.get(conf);
		System.out.println(fileSystem.getUri());

		Path file = new Path("/user/" + user.getUserName());
		if (fileSystem.exists(file)) {
			System.out.println("haddop hdfs user foler  exists.");
			fileSystem.delete(file, true);
			System.out.println("haddop hdfs user foler  delete success.");
		}
		fileSystem.mkdirs(file);
		System.out.println("haddop hdfs user foler  creat success.");
	}

}