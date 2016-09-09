package com.moz.signin.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class UserInfoUtils {
public static boolean saveInfo(Context context,String name,String pwd){
	try {
	String result = name+"##"+pwd;
	
	
	FileOutputStream fos = context.openFileOutput("infoo.txt", 0);
		fos.write(result.getBytes());
		fos.close();
		return true;
	} catch (Exception e) {
		
		e.printStackTrace();
		return false;
	}
	
	}


public static Map<String,String> readInfo(Context context){
	
	try {
		Map<String,String> map = new HashMap<String,String>();
		FileInputStream fis = context.openFileInput("infoo.txt");
		BufferedReader bufr = new BufferedReader(new InputStreamReader(fis));
		
		String content = bufr.readLine();
		String[] splits = content.split("##");
		String name = splits[0];
		String pwd = splits[1];
		
		map.put("name",name);
		map.put("pwd",pwd );
		
		fis.close();
		
		return map;
		
		
	} catch (Exception e) {
	
		e.printStackTrace();
		return null;
	}
	
	

	
}



}

