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

import Utils.SendPost;
import Utils.SignUtils;
import dao.UserDao;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SetTimeServlet
 */

public class SetTimeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(SetTimeServlet.class);      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetTimeServlet() {
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
		String last_update = request.getParameter("last_update");
		String beginTime = request.getParameter("beginTime");
		String endTime = request.getParameter("endTime");
		String ipInfo ="，来自"+ SignUtils.getIpAddress(request);
		logger.info("请求设置时间 ，来自token："+token+"，时间段："+beginTime+"~"+endTime+"，上次修改日期："+last_update+ipInfo);
		UserDao userDao = new UserDao();
		JSONObject data = userDao.setTime(token, last_update, beginTime, endTime);
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(data);
		out.flush();
	}

}
