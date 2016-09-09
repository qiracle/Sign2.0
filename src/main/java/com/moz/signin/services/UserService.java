package com.moz.signin.services;



public interface UserService {
	public boolean userLogin(String loginId, String loginPwd, int identity) throws Exception;
		
	public boolean modifyPwd(String loginId, String oldPwd, String newPwd, int identity) throws Exception;
	
	public boolean signIn(String scanResult, String loginId) throws Exception;

	public String getName(String loginId,int identity) throws Exception;

	public boolean techerQRCode(String classId,String stringQRCode) throws Exception;
}