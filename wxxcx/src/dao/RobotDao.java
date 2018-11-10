package dao;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.AddRobotServlet;
import net.sf.json.JSONObject;

public class RobotDao extends DBConn{
	private final static Logger logger = LoggerFactory.getLogger(RobotDao.class);   
	// 添加新机器人
	public JSONObject addRobot(String nickName)
				throws UnsupportedEncodingException {
			JSONObject data = new JSONObject();
			boolean result = false;
			int openid = this.countUser()+1;
			// 生成userid
			int userid = 40000 + openid;
			//头像地址
			String avatarUrl = "*/imagesWX/"+openid+".jpg";
			// 生成首次注册时间
			Date now = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			String first_register = ft.format(now);
			// 生成token
			String token = "token"+openid;
			// 对nickName编码,防止emoji
			final Base64.Encoder encoder = Base64.getEncoder();
			final byte[] textByte = nickName.getBytes("UTF-8");
			final String encodedText = encoder.encodeToString(textByte);
			// 添加新纪录
			String sql = "insert into users(userid,openid,nickName,avatarUrl,session_key,token,"
					+ "first_register,beginTime,endTime,sign_days,running_days,"
					+ "long_running_days,is_robot) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
			Object[] params = { userid, openid, encodedText, avatarUrl, 1, token, first_register, "19:00",
					"24:00", 0, 0, 0, 0 };
			try {
				int rows = super.executeSQL(sql, params);
				if (rows > 0) {
					result = true;
					data.put("token", token);
					data.put("userid", userid);
					logger.info("添加成功，userid："+userid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!result) {
				data.put("token", "failure");
				logger.info("添加失败");
			}
			return data;
		}

	//签到（机器人）
	public JSONObject Signin(String token, String today, String word, String isPublic,String likes_num) {
		JSONObject data = new JSONObject();
		String sql = "select sign_days,last_signDate,running_days,long_running_days,userid from users where token=?";
		Object[] params = { token };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				int sign_days = rs.getInt("sign_days");
				int running_days = rs.getInt("running_days");
				int long_running_days = rs.getInt("long_running_days");
				String userid = rs.getString("userid");
				String last_signDate = rs.getString("last_signDate") == null ? "" : rs.getString("last_signDate");
				// 如果是第一次签到，则last_signDate为空
				if (last_signDate.isEmpty()) {
					// 先修改用户表
					sign_days = 1;
					running_days = 1;
					long_running_days = 1;
					this.Signin_users(token, today, sign_days, running_days, long_running_days);
					// 再添加签到表,先判断word是否为空
					if (word.isEmpty() || word.matches("\\s{0,}")) {
						word = "早睡第" + String.valueOf(sign_days) + "天";
					}
					// 获取当前时刻
					this.Signin_signinRecord(userid, today, word, isPublic, sign_days,likes_num);

				}
				// 不是第一次签到
				else {
					sign_days = sign_days + 1;
					// 判断是否为连续签到
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date date1 = format.parse(today);
					Date date2 = format.parse(last_signDate);
					int a = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
					// 和上次相隔天数大于1，断签了
					if (a > 1) {
						long_running_days = long_running_days > running_days ? long_running_days : running_days;
						running_days = 1;
					} else {
						running_days = running_days + 1;
						long_running_days = long_running_days > running_days ? long_running_days : running_days;
					}
					// 再添加签到表,先判断word是否为空
					if (word.isEmpty() || word.matches("\\s{0,}")) {
						word = "早睡第" + String.valueOf(sign_days) + "天";
					}
					this.Signin_users(token, today, sign_days, running_days, long_running_days);
					this.Signin_signinRecord(userid, today, word, isPublic, sign_days,likes_num);
				}
				data.put("sign_days", sign_days);
				data.put("running_days", running_days);
				data.put("long_running_days", long_running_days);
			}else{
				logger.info("机器人请求签到 ，来自token："+token+"，未查到userid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	// 签到，修改用户表
		public boolean Signin_users(String token, String today, int sign_days, int running_days, int long_running_days) {
			boolean result = false;
			String sql = "update users set last_signDate=?,sign_days=?,running_days=?,long_running_days=? where token=?";
			Object[] params = { today, sign_days, running_days, long_running_days, token };
			try {
				int rows = super.executeSQL(sql, params);
				if (rows > 0) {
					result = true;
					logger.info("机器人请求签到 ，来自token："+token+"，修改users表成功");
				}else{
					logger.info("机器人请求签到 ，来自token："+token+"，修改users表失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		// 签到，添加签到表
		public boolean Signin_signinRecord(String userid, String today, String word, String isPublic, int sign_days,String likes_num)
				throws UnsupportedEncodingException {
			boolean result = false;
			// 生成signinID
			String signinID = userid + "-" + String.valueOf(sign_days);
			// 获取当前时间
			Date day = new Date();
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			String nowTime = df.format(day);
			// 编码word，防止emoji
			final Base64.Encoder encoder = Base64.getEncoder();
			final byte[] textByte = word.getBytes("UTF-8");
			// 编码
			final String encodedText = encoder.encodeToString(textByte);
			Object[] params = { signinID, userid, today, nowTime, encodedText, likes_num, isPublic };
			String sql = "insert into signin_record(signinID,userid,date,time,"
					+ "word,likes_num,isPublic) values(?,?,?,?,?,?,?)";
			// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
			try {
				int rows = super.executeSQL(sql, params);
				if (rows > 0) {
					result = true;
					logger.info("机器人请求签到 ，来自token："+userid+"，修改signin_record表成功");
				}else{
					logger.info("机器人请求签到 ，来自token："+userid+"，修改signin_record表失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		// 查询机器人的个数，用于生成userid
		public int countUser() {
			int count = 0;
			String sql = "select COUNT(*) as rowcount from users where is_robot=0";
			ResultSet rs = super.executeQuery(sql);
			try {
				if (rs.next()) {
					// 有记录，说明不是第一次
					count = rs.getInt("rowcount");
					//System.out.println("数据库里用户数：" + count);
				} else {
					count = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return count;
		}
}
