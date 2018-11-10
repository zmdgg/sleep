package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utils.SignUtils;
import dao.UserDao;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class LoginServlet
 */
//@.asfWebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    /**
     * Default constructor. 
     */
    public LoginServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//不接受get请求
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String code = request.getParameter("code");
		String nickName = request.getParameter("nickName");
		String avatarUrl = request.getParameter("avatarUrl");
		String appId = "*";
	    String secret = "*";
	    String wxUrl = "https://api.weixin.qq.com/sns/jscode2session?appid="
	    				+ appId + "&secret=" + secret + "&js_code=" + code + 
	    				"&grant_type=authorization_code";
        String urlString = "";
		try
	      {
	         URL url = new URL(wxUrl);
	         URLConnection urlConnection = url.openConnection();
	         HttpURLConnection connection = null;
	         if(urlConnection instanceof HttpURLConnection)
	         {
	            connection = (HttpURLConnection) urlConnection;
	         }
	         else
	         {
	            System.out.println("请输入 URL 地址");
	            return;
	         }
	         BufferedReader in = new BufferedReader(
	         new InputStreamReader(connection.getInputStream()));
	         String current;
	         while((current = in.readLine()) != null)
	         {
	            urlString += current;
	         }
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
		JSONObject json = JSONObject.fromObject(urlString);  
		String openid = json.getString("openid");
		String session_key = json.getString("session_key");
		String ipInfo ="，来自"+ SignUtils.getIpAddress(request);
		logger.info("openid:"+openid+"请求登录"+ipInfo);
		UserDao userDao = new UserDao();
		//如果是第一次授权
		if(userDao.isFirstRegister(openid)){
			JSONObject result = userDao.addUser(openid, session_key,nickName,avatarUrl);
			//如果授权成功
			if(!result.get("token").equals("failure")){
				//结果都在userdao里面
				response.setContentType("application/json;charset=utf-8");
				PrintWriter out = response.getWriter();
				out.print(result);
				out.flush();
			}
		}else{
		//已经授权过了
			JSONObject result = userDao.updateUser(openid, session_key,nickName,avatarUrl);
			//如果更新授权成功
			if(!result.get("token").equals("failure")){
				response.setContentType("application/json;charset=utf-8");
				PrintWriter out = response.getWriter();
				out.print(result);
				out.flush();
			}
		}
	}

}
