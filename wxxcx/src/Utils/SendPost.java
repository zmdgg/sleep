package Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.RobotDao;
import net.sf.json.JSONObject;

public class SendPost {
	private final static Logger logger = LoggerFactory.getLogger(SendPost.class);   
	//发送模板消息
	public JSONObject SendTemplateMes(String access_token, String formid, String openid, String time) {
		String wxUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token;
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("touser", openid);
		jsonParam.put("template_id", "Uu7lPsl8ylcWgrTa9n9JG-qfKCFDqrWy-6d356iM5pc");
		jsonParam.put("page", "pages/index/index");
		jsonParam.put("form_id", formid);

		JSONObject data = new JSONObject();
		JSONObject keyword1 = new JSONObject();
		JSONObject keyword2 = new JSONObject();
		JSONObject keyword3 = new JSONObject();
		keyword1.put("value", "早睡提醒~");
		keyword2.put("value", time);
		keyword3.put("value", "叮！该签到啦~早睡早起身体好。");

		data.put("keyword1", keyword1);
		data.put("keyword2", keyword2);
		data.put("keyword3", keyword3);

		jsonParam.put("data", data);

		String urlString = "";
		// System.out.println("formid:"+formid);
		try {
			URL url = new URL(wxUrl);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("POST"); // 设置请求方式
				connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
				// connection.setRequestProperty("Content-Type",
				// "application/json;charset=utf-8"); // 设置发送数据的格式
				connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
				// 转换为字节数组
				byte[] databytes = (jsonParam.toString()).getBytes("utf-8");
				// 设置文件长度
				connection.setRequestProperty("Content-Length", String.valueOf(databytes.length));
				// 开始连接请求
				connection.connect();
				OutputStream out = new DataOutputStream(connection.getOutputStream());
				// 写入请求的字符串
				out.write(databytes);
				out.flush();
				out.close();
			} else {
				System.out.println("请输入 URL 地址");
			}
			int code = connection.getResponseCode();
			InputStream is = null;
			if (code == 200) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}
			//System.out.println("code:" + code);
			// 读取响应
			int length = (int) connection.getContentLength();// 获取长度
			if (length != -1) {
				byte[] data_temp = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, data_temp, destPos, readLen);
					destPos += readLen;
				}
				urlString = new String(data_temp, "UTF-8"); // utf-8编码
			}
			//System.out.println("urlString:" + urlString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = JSONObject.fromObject(urlString);
		String errcode = json.getString("errcode");
		logger.info("发送模板消息，openid：" + openid+"，结果code："+errcode);
		return json;
	}
	//获取access_token
	public String GetAccess_token() {
		String appId = "wxdf8a56b8ad720606";
		String secret = "e7d24d8b7c06a862e305ee4806683948";
		String wxUrl = "https://api.weixin.qq.com/cgi-bin/token?" + "grant_type=client_credential&appid=" + appId
				+ "&secret=" + secret;
		String urlString = "";
		try {
			URL url = new URL(wxUrl);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
			} else {
				System.out.println("请输入 URL 地址");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String current;
			while ((current = in.readLine()) != null) {
				urlString += current;
			}
			// System.out.println(urlString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = JSONObject.fromObject(urlString);
		String access_token = json.getString("access_token");
		logger.info("获取access_token：" + access_token);
		return access_token;
	}
}
