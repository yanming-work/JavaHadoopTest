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
 * �û����������
 * 
 */
@Controller
// ������ǰ��Ϊ������
@RequestMapping("/user")
// ������ǰ���·��
public class UserController {
	@Resource(name = "userService")
	private UserService userService;// ��Spring����ע��һ��UserServiceʵ��

	/**
	 * ��¼
	 * 
	 * @param user
	 *            �û�
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/login")
	// ������ǰ������·��
	public String login(User user, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		response.setContentType("application/json");// ������Ӧ���ݸ�ʽΪjson
		User result = userService.login(user);// ����UserService�ĵ�¼����
		request.getSession().setAttribute("user", result);
		if (result != null) {
			createHadoopFSFolder(result);
			return "console";
		}
		return "login";
		
		
	}

	/**
	 * ע�ᣨֻ����ע����ͨ�û���
	 * 
	 * @param user
	 *            �û�
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/register")
	public ResultJson register(User user, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		user.setRole(CodeConfig.GENERUSER);// ������ͨ��ע�᷽ʽע�����Ա
		int lines = userService.register(user);
		ResultJson result = new ResultJson();
		if (lines == CodeConfig.SUCCESS) {
			result.setCode(CodeConfig.SUCCESS);
			result.setMsg("ע��ɹ�");
		} else {
			result.setCode(CodeConfig.FAILED);
			result.setMsg("ע��ʧ��");
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