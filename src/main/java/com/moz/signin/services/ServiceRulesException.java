package com.moz.signin.services;

public class ServiceRulesException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9079248040147308963L;
	private static final String MSG_REGISTER_ERROR = "注册出错。";
	private static final String MSG_REGISTER_SUCCESS = "注册成功。";
    public static final String MSG_LOGIN_FAILED = "登录失败";
	public static final String MSG_SERVER_ERROR = "请求服务器错误。";
    public static final String MSG_REQUEST_TIMEOUT = "请求服务器超时。";
    public static final String MSG_RESPONSE_TIMEOUT = "服务器响应超时。";
	


	public ServiceRulesException(String message) {
		super(message);
	}
}
