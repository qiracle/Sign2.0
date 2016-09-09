package com.moz.signin.services;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
public class UserServiceImpl implements UserService {
    //public static final String URL = "http://153vg35032.imwork.net:11406//AndroidServer/";
    public static final String URL = "http://qiracle.qicp.io:15660/AndroidServer/";
   // public static final String URL = "http://10.105.2.44:8080/AndroidServer/";


    @Override
    public boolean userLogin(String loginId, String loginPwd, int identity) throws Exception {


        HttpClient client = new DefaultHttpClient();
        String uri = URL + "LogIn";
        HttpPost post = new HttpPost(uri);
        HttpParams params = new BasicHttpParams();

        // 设置客户端和服务器连接的超时时间 －－－》 ConnectionTimeoutException
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        // 设置服务器响应的超时时间 －－－》 SocketTimeoutException
        HttpConnectionParams.setSoTimeout(params, 3000);
         /**
            JSON
            UserLogin{"LoginId":"loginId","LoginPwd":"loginPwd","Identity":"identity"}
         */



        JSONObject logInObj = new JSONObject();
        logInObj.put("LoginId", loginId);
        logInObj.put("LoginPwd", loginPwd);
        logInObj.put("Identity", identity);

        BasicNameValuePair parameter = new BasicNameValuePair("UserLogin", logInObj.toString());

        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(parameter);

        post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();


        if (statusCode != HttpStatus.SC_OK) {

            Log.i("msg",statusCode+"");
            throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);

        }
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        if (result.equals("LogInSuccess")) {
            return true;
        } else {

            return false;
        }


    }

    @Override
    public boolean modifyPwd(String loginId, String oldPwd, String newPwd, int identity) throws Exception {

        /**
         ModifyPwd{"LoginId":"loginId","ModifyOldPwd":"oldPwd","ModifyNewPwd":"newPwd","Identity":"identity"}
        */
        HttpClient client = new DefaultHttpClient();
        String uri = URL+"ModifyPwd";
        HttpPost post = new HttpPost(uri);
        JSONObject object = new JSONObject();
        object.put("LoginId", loginId);
        object.put("ModifyOldPwd", oldPwd);
        object.put("ModifyNewPwd", newPwd);
        object.put("Identity", identity);
        BasicNameValuePair parameter = new BasicNameValuePair("ModifyPwd", object.toString());

        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(parameter);

        post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {

            throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);

        }
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        if (result.equals("ModifySuccess")) {
            return true;
        } else {

            return false;
        }
    }

    public boolean signIn(String scanResult, String loginId) throws Exception {
        /**
         * SignIn{"LoginId":"loginId","ScanResult":"scanResult"}
         */
        HttpClient client = new DefaultHttpClient();
        String uri = URL+"SignIn";
        HttpPost post = new HttpPost(uri);
        /**
         *
         * JSON数据的封装
         */

        JSONObject object = new JSONObject();
        object.put("LoginId", loginId);
        object.put("ScanResult", scanResult);
        BasicNameValuePair parameter = new BasicNameValuePair("SignIn", object.toString());

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(parameter);

        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);

        }
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println("---" + result);
        if (result.equals("SignInSuccess")) {
            return true;
        } else {
            return false;
        }


    }

    @Override
    public String getName(String loginId,int identity) throws Exception {
        HttpClient client = new DefaultHttpClient();
        String uri = URL + "GetName";
        HttpPost post = new HttpPost(uri);

        /**
         AskForName{"LoginId":"loginId","Identity":"identity"}
         *
         */


        JSONObject logInObj = new JSONObject();
        logInObj.put("LoginId", loginId);
        logInObj.put("Identity", identity);

        BasicNameValuePair parameter = new BasicNameValuePair("AskForName", logInObj.toString());

        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(parameter);

        post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);

        }
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        if(result.equals("CheckNameError"))
            return null;
        else return result.toString();
    }

    @Override
    public boolean techerQRCode(String classId, String stringQRCode) throws Exception {
        HttpClient client = new DefaultHttpClient();
        String uri = URL + "ReceiveQRCode";
        HttpPost post = new HttpPost(uri);
        /**
         *
         * JSON数据的封装
         * TeacherQRCode{"ClassId":"classId","StringQRCode":"stringQRCode"}
         */

        JSONObject object = new JSONObject();
        object.put("ClassId", classId);
        object.put("StringQRCode", stringQRCode);
        BasicNameValuePair parameter = new BasicNameValuePair("TeacherQRCode", object.toString());

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(parameter);

        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {

            throw new ServiceRulesException(ServiceRulesException.MSG_SERVER_ERROR);

        }
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println("---" + result);
        if (result.equals("isOk")) {
            return true;
        } else
            return false;

    }

}