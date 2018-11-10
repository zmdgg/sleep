package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.UserDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * Application Lifecycle Listener implementation class TestSend
 *
 */

public class TestSend implements ServletContextListener {
	Timer timer = new Timer();
	private final static Logger logger = LoggerFactory.getLogger(TestSend.class);
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	 timer.cancel();  
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 	
    	//System.out.println();
    	logger.info("监听开始");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		TimerTask taskDay = new TimerTask() {
			public void run() {
				String dayNow = df.format(new Date());
				if (dayNow.matches("(19|20|21|22|23):30:00")) {
					//System.out.println("到:" + dayNow);
					logger.info("到:" + dayNow);
					// 刷新access_token
					SendPost sp = new SendPost();
					String access_token = sp.GetAccess_token();
					//获取需要推送的
					UserDao userDao = new UserDao();
					JSONArray openidList = userDao.getNowSendUsers(dayNow.substring(0,2)+":00:00");
					int count = openidList.size();
					logger.info("需要发送模板消息的数量：" + count);
					// 如果有需要推送的
					if (count > 0) {
						//System.out.println("需要推送的用户数："+count);
						//获取同等数量的formid
						JSONArray formidList = userDao.getFormID(count);
						for (int i = 0; i < count; i++) {
							JSONObject user = openidList.getJSONObject(i);
							sp.SendTemplateMes(access_token, formidList.getString(i), 
									user.getString("openid"),  user.getString("endTime"));
						}
						//删除已经使用过的formid
						userDao.deleteUsedFormID(formidList);
					}
				}
			}
		};
		/*
		 * schedule 和 scheduleAtFixedRate 区别： 可将schedule理解为scheduleAtFixedDelay，
		 * 两者主要区别在于delay和rate 1、schedule，如果第一次执行被延时（delay），
		 * 随后的任务执行时间将以上一次任务实际执行完成的时间为准 2、scheduleAtFixedRate，如果第一次执行被延时（delay），
		 * 随后的任务执行时间将以上一次任务开始执行的时间为准（需考虑同步）
		 * 
		 * 参数：1、任务体 2、延时时间（可以指定执行日期）3、任务执行间隔时间
		 */
		// timer.schedule(task, 0, 1000 * 3);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/DD HH:mm:ss");
		Date startDate;
		try {
			startDate = dateFormatter.parse("2018/06/08 19:30:00");
			timer.scheduleAtFixedRate(taskDay, startDate, 60 * 60 * 1000);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
	
}
