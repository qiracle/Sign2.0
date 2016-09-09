package com.moz.signin.services;

public interface SystemService {
	
	public boolean setStringQRCode(String classId,String stringQRCode) throws Exception;

	public String getName(String loginId,int identity)throws Exception;

}
