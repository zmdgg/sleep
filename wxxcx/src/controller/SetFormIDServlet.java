package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utils.SignUtils;
import dao.UserDao;

/**
 * Servlet implementation class GetFormIDServlet
 */

public class SetFormIDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(SetFormIDServlet.class);      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetFormIDServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String formid = request.getParameter("formid");
		String ipInfo ="，来自"+ SignUtils.getIpAddress(request);
		logger.info("获得formid："+formid+ipInfo);		
		if(!formid.equals("the formId is a mock one")){
			UserDao userDao = new UserDao();
			userDao.SetFormID(formid);
		}

	}

}
