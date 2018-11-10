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
import net.sf.json.JSONObject;

/**
 * Servlet implementation class AddRobotServlet
 */

public class AddRobotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(AddRobotServlet.class);   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddRobotServlet() {
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
		String nickName = request.getParameter("nickName");
		String ipInfo ="，来自"+ SignUtils.getIpAddress(request);
		logger.info("添加新机器人，昵称："+nickName+ipInfo);
		RobotDao robotDao = new RobotDao();
		JSONObject data = robotDao.addRobot(nickName);
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(data);
		out.flush();
	}

}
