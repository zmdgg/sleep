package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utils.SignUtils;
import dao.RobotDao;
import dao.UserDao;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SigninRobotServlet
 */

public class SigninRobotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(SigninRobotServlet.class);      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SigninRobotServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String token = request.getParameter("token");
		String date = request.getParameter("last_signDate");
		String isPublic = request.getParameter("isPublic");
		String word = request.getParameter("word");
		String likes_num = request.getParameter("likes_num");
		String ipInfo ="，来自"+ SignUtils.getIpAddress(request);
		logger.info("机器人请求签到 ，来自token："+token+ipInfo);
		RobotDao robotDao = new RobotDao();
		JSONObject data = robotDao.Signin(token, date, word, isPublic,likes_num);
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(data);
		out.flush();
	}

}
