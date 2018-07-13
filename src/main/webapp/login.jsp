<%@ page language="java" contentType="text/html; charset=UTF-8"
import="java.util.*"
    pageEncoding="UTF-8"%>
    <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="<%=basePath %>user/login" method="post">
		用户名:<input name="userName" id="userName" >
		密    码:<input name="password" id="password" >
		<input type="submit" value="登录">
	</form>
</body>
</html>