package com.moz.signin.services;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
//10.105.2.44
public class SystemServiceImpl implements SystemService{
	//public static final String URL = "http://153vg35032.imwork.net:11406//AndroidServer/";
	public static final String URL = "http://qiracle.qicp.io:15660/AndroidServer/";
	//public static final String URL = "http://10.105.2.44:8080/AndroidServer/";
	@Override
	public boolean setStringQRCode(String classId,String stringQRCode) throws Exception {
		HttpClient client = new DefaultHttpClient();
		String uri = URL+"ReceiveQRCode";
		HttpPost post = new HttpPost(uri);

		/**QRCode{"StringQRCode":"stringQRCode"}
		 * TeacherQRCode{"ClassId":"classId","StringQRCode":"stringQRCode"}
		 */
		System.out.println("-----" + classId + "-----" + stringQRCode + "====");
		JSONObject object = new JSONObject();
		object.put("StringQRCode", stringQRCode);
		object.put("ClassId", classId);
		
	BasicNameValuePair parameter = new BasicNameValuePair("TeacherQRCode", object.toString());
		
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(parameter);

		post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

		HttpResponse response = client.execute(post);


		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode != HttpStatus.SC_OK) {

			throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);
			
		}
		String result = EntityUtils.toString(response.getEntity(),"UTF-8");
		System.out.println("---"+result);
		if(result.equals("isOk")){
			return true;
		}else{
			
		return false;
		}

	}

	@Override
	public String getName(String loginId, int identity) throws Exception {
		HttpClient client = new DefaultHttpClient();
		String uri = URL+"GetName";
		HttpPost post = new HttpPost(uri);

		/**AskForName{"LoginId":"loginId","Identity":"identity"}
		 */
		JSONObject object = new JSONObject();
		object.put("LoginId", loginId);
		object.put("Identity",identity);

		BasicNameValuePair parameter = new BasicNameValuePair("AskForName", object.toString());

		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(parameter);

		post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

		HttpResponse response = client.execute(post);

		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode != HttpStatus.SC_OK) {

			throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);

		}
		String result = EntityUtils.toString(response.getEntity(),"UTF-8");
		System.out.println("---"+result);
		if(result.equals("CheckNameError")){
			return null;
		}else {
			return result;
		}
	}

}
