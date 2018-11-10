package dao;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utils.SignUtils;
import controller.FeedbackServlet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UserDao extends DBConn {
	private final static Logger logger = LoggerFactory.getLogger(UserDao.class); 
	// 查询是否为新用户
	public boolean isFirstRegister(String openid) {
		boolean result = true;// 默认是第一次注册
		String sql = "select * from users where openid=?";
		Object[] params = { openid };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				// 有记录，说明不是第一次
				result = false;
				//System.out.println("数据库");
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
		return result;
	}

	// 添加新用户
	public JSONObject addUser(String openid, String session_key, String nickName, String avatarUrl)
			throws UnsupportedEncodingException {
		JSONObject data = new JSONObject();
		boolean result = false;
		// 生成userid
		int userid = 40000 + this.countUser()+1;
		// 生成首次注册时间
		Date now = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		String first_register = ft.format(now);
		// 生成token
		String token = Encrypt.getMD5("wxdk" + String.valueOf(userid) + first_register);
		// 对nickName编码,防止emoji
		final Base64.Encoder encoder = Base64.getEncoder();
		final byte[] textByte = nickName.getBytes("UTF-8");
		final String encodedText = encoder.encodeToString(textByte);
		// 添加新纪录
		String sql = "insert into users(userid,openid,nickName,avatarUrl,session_key,token,"
				+ "first_register,beginTime,endTime,sign_days,running_days,"
				+ "long_running_days,is_robot) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
		Object[] params = { userid, openid, encodedText, avatarUrl, session_key, token, first_register, "21:00",
				"23:00", 0, 0, 0, 1 };
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				data.put("token", token);
				data.put("userid", userid);
				logger.info("openid:"+openid+"第一次授权登录，注册成功，userid："+userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!result) {
			data.put("token", "failure");
			logger.info("openid:"+openid+"第一次授权登录，注册失败");
		}
		return data;
	}

	// 修改token和session_key
	public JSONObject updateUser(String openid, String session_key, String nickName, String avatarUrl)
			throws UnsupportedEncodingException {
		JSONObject userInfo = this.getUseridAndFirstReg(openid);
		JSONObject data = new JSONObject();
		boolean result = false;
		// 生成token
		String token = Encrypt.getMD5("wxdk" + userInfo.getString("userid") + userInfo.getString("first_register"));
		// 对nickName编码,防止emoji
		final Base64.Encoder encoder = Base64.getEncoder();
		final byte[] textByte = nickName.getBytes("UTF-8");
		final String encodedText = encoder.encodeToString(textByte);
		// 添加新纪录
		String sql = "update users set nickName=?,avatarUrl=?,session_key=?,token=? where openid=?";
		Object[] params = { encodedText, avatarUrl, session_key, token, openid };
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				data.put("token", token);
				data.put("userid", userInfo.getString("userid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!result) {
			data.put("token", "failure");
		}
		return data;
	}

	// 获取userid和first_register，用于生成token
	public JSONObject getUseridAndFirstReg(String openid) {
		JSONObject data = new JSONObject();
		String sql = "select * from users where openid=?";
		Object[] params = { openid };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				// 有记录，说明不是第一次
				data.put("userid", rs.getString("userid"));
				data.put("first_register", rs.getString("first_register"));
				// System.out.println("数据库");
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
		return data;
	}

	// 查询设置的时间
	public JSONObject getTime(String token) {
		JSONObject data = new JSONObject();
		String sql = "select * from users where token=?";
		Object[] params = { token };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				// 防止异常
				String tempBegin = rs.getString("beginTime") == null ? "" : rs.getString("beginTime").substring(0, 5);
				String tempEnd = rs.getString("endTime") == null ? "" : rs.getString("endTime").substring(0, 5);
				if (tempEnd.equals("00:00"))
					tempEnd = "24:00";
				data.put("beginTime", tempBegin);
				data.put("endTime", tempEnd);
				data.put("last_update", rs.getString("last_update") == null ? "" : rs.getString("last_update"));
				logger.info("查询个人时间设置，来自token："+token+"，查到信息");
			}else{
				logger.info("查询个人时间设置，来自token："+token+"，未查到信息");
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
		return data;
	}

	// 设置时间
	public JSONObject setTime(String token, String last_update, String beginTime, String endTime) {
		JSONObject data = new JSONObject();
		String sql = "update users set last_update=?,beginTime=?,endTime=? where token=?";
		Object[] params = { last_update, beginTime, endTime, token };
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				logger.info("请求设置时间 ，成功，来自token："+token+"，时间段："+beginTime+"~"+endTime+"，上次修改日期："+last_update);
				data.put("code", "1");
			} else {
				logger.info("请求设置时间 ，失败，来自token："+token+"，时间段："+beginTime+"~"+endTime+"，上次修改日期："+last_update);
				data.put("code", "0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	// 签到判断
	public JSONObject SigninJudge(String token) {
		JSONObject data = new JSONObject();
		String sql = "select beginTime,endTime,last_signDate from users where token=?";
		Object[] params = { token };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				String tempBegin = rs.getString("beginTime") == null ? "" : rs.getString("beginTime");
				String tempEnd = rs.getString("endTime") == null ? "" : rs.getString("endTime");
				String last_signDate = rs.getString("last_signDate") == null ? "" : rs.getString("last_signDate");
				Date now = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
				String today = ft.format(now);
				// 先判断今天还没签到
				if (!today.equals(last_signDate) || last_signDate.isEmpty()) {
					// 如果在时间段内
					if (SignUtils.isInTime(tempBegin, tempEnd)) {
						data.put("code", "1");
						logger.info("请求签到 ，可以签到，时间段："+tempBegin+"~"+tempEnd+"，来自token："+token);
					} else {
						// 如果不在时间段内
						data.put("code", "3");
						logger.info("请求签到 ，不在签到时间段："+tempBegin+"~"+tempEnd+"，来自token："+token);
					}
				} else {
					// 如果已经签过了
					data.put("code", "2");
					logger.info("请求签到 ，今天已经签到了，来自token："+token+"，上次签到日期："+last_signDate);
				}
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
		return data;
	}

	// 签到
	public JSONObject Signin(String token, String word, String isPublic) {
		JSONObject data = new JSONObject();
		String sql = "select sign_days,last_signDate,running_days,long_running_days,userid from users where token=?";
		Object[] params = { token };
		//获取今天的日期
		Date day = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(day);
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
					this.Signin_signinRecord(userid, today, word, isPublic, sign_days);
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
					this.Signin_signinRecord(userid, today, word, isPublic, sign_days);
				}
				data.put("sign_days", sign_days);
				data.put("running_days", running_days);
				data.put("long_running_days", long_running_days);
			}else{
				logger.info("请求签到 ，来自token："+token+"，未查到userid");
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
				logger.info("请求签到 ，来自token："+token+"，修改users表成功");
			}else{
				logger.info("请求签到 ，来自token："+token+"，修改users表失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 签到，添加签到表
	public boolean Signin_signinRecord(String userid, String today, String word, String isPublic, int sign_days)
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
		Object[] params = { signinID, userid, today, nowTime, encodedText, 0, isPublic };
		String sql = "insert into signin_record(signinID,userid,date,time,"
				+ "word,likes_num,isPublic) values(?,?,?,?,?,?,?)";
		// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				logger.info("请求签到 ，来自userid："+userid+"，修改signin_record表成功");
			}else{
				logger.info("请求签到 ，来自userid："+userid+"，修改signin_record表失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 查询个人签到记录
	public JSONObject getMySignRecord(String token) throws UnsupportedEncodingException {
		JSONObject data = new JSONObject();
		String sql = "select * from signin_record where userid in(" + "select userid from users where token=?)";
		Object[] params = { token };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			int i = 0;
			JSONArray row = new JSONArray();
			while (rs.next()) {
				final Base64.Decoder decoder = Base64.getDecoder();
				JSONObject temp = new JSONObject();
				temp.put("signinID", rs.getString("signinID"));
				temp.put("date", rs.getString("date"));
				temp.put("time", rs.getString("time"));
				temp.put("likes_num", rs.getString("likes_num"));
				// 解码
				temp.put("word", new String(decoder.decode(rs.getString("word")), "UTF-8"));
				row.add(i, temp);
				++i;
			}
			data.put("count", i);
			data.put("sign_record", row);
			logger.info("请求个人签到记录，数量："+i+"，来自token："+token);
		} catch (SQLException e) {
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

	// 查询全部签到记录（不能查是否点过赞）
	public JSONObject getAllSignRecord(String pageIndex, String pageCount) throws UnsupportedEncodingException {
		JSONObject data = new JSONObject();
		int index = Integer.parseInt(pageIndex);
		int count = Integer.parseInt(pageCount);
		if (index != 0)
			index = index * count - 1;
		String sql = "select signin_record.*,nickName,avatarUrl from signin_record,users "
				+ "where isPublic='y' and signin_record.userid=users.userid " + "ORDER BY date DESC,time DESC "
				+ "limit " + index + "," + count;
		// System.out.println(sql);
		ResultSet rs = super.executeQuery(sql);
		try {
			int i = 0;
			JSONArray row = new JSONArray();
			while (rs.next()) {
				// System.out.println("查到了");
				final Base64.Decoder decoder = Base64.getDecoder();
				JSONObject temp = new JSONObject();
				temp.put("signinID", rs.getString("signinID"));
				temp.put("nickName", new String(decoder.decode(rs.getString("nickName")), "UTF-8"));
				// System.out.println("查到了昵称");
				temp.put("avatarUrl", rs.getString("avatarUrl"));
				temp.put("date", rs.getString("date"));
				String tempTime = rs.getString("time");
				if (tempTime.equals("00:00:00"))
					tempTime = "24:00:00";
				temp.put("time", tempTime);
				temp.put("likes_num", rs.getString("likes_num"));
				// 解码
				temp.put("word", new String(decoder.decode(rs.getString("word")), "UTF-8"));
				row.add(i, temp);
				++i;
			}
			data.put("count", i);
			data.put("sign_record", row);
		} catch (SQLException e) {
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

	// 查询全部签到记录（能查是否点过赞）
	public JSONObject getAllSignRecordAndIsLike(String pageIndex, String pageCount, String token)
			throws UnsupportedEncodingException {
		JSONObject data = new JSONObject();
		int index = Integer.parseInt(pageIndex);
		int count = Integer.parseInt(pageCount);
		if (index != 0)
			index = index * count ;
		String sql = "select signin_record.*,nickName,avatarUrl from signin_record,users "
				+ "where isPublic='y' and signin_record.userid=users.userid " + "ORDER BY date DESC,time DESC "
				+ "limit " + index + "," + count;
		// 先查记录
		ResultSet rs = super.executeQuery(sql);
		try {
			int i = 0;
			JSONArray row = new JSONArray();
			while (rs.next()) {
				// System.out.println("查到了");
				final Base64.Decoder decoder = Base64.getDecoder();
				JSONObject temp = new JSONObject();
				temp.put("signinID", rs.getString("signinID"));
				temp.put("nickName", new String(decoder.decode(rs.getString("nickName")), "UTF-8"));
				// System.out.println("查到了昵称");
				temp.put("avatarUrl", rs.getString("avatarUrl"));
				temp.put("date", rs.getString("date"));
				String tempTime = rs.getString("time");
				if (tempTime.equals("00:00:00"))
					tempTime = "24:00:00";
				temp.put("time", tempTime);
				temp.put("likes_num", rs.getString("likes_num"));
				// 解码
				temp.put("word", new String(decoder.decode(rs.getString("word")), "UTF-8"));
				temp.put("isLike", "");
				row.add(i, temp);
				++i;
			}
			data.put("count", i);
			data.put("sign_record", row);
			logger.info("请求find页，查到数据量："+count+"，来自token："+token);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// 再查userid
		String userid = "";
		if (!(userid = this.getUserIDByToken(token)).matches("\\s{0,}")) {
			//有此用户
			logger.info("请求find页，token："+token+"，查出userid："+userid);
			// 在判断是否点过赞
			JSONArray row = data.getJSONArray("sign_record");
			String sql_isLike = "select signinID from likes where userid=? and signinID=?";
			JSONArray rowNew = new JSONArray();
			for (int i = 0; i < row.size(); i++) {
				JSONObject temp = row.getJSONObject(i);
				String signinID = temp.getString("signinID");
				Object[] params = { userid, signinID };
				ResultSet rs_isLike = super.executeQuery(sql_isLike, params);
				try {
					if (rs_isLike.next()) {
						// 有记录，说明点过赞
						temp.put("isLike", "d");
						rowNew.add(i, temp);
					} else {
						rowNew.add(i, row.getJSONObject(i));
					}
					rs_isLike.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						rs_isLike.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			data.put("sign_record", rowNew);
		} else {
			// 如果没有此用户
			logger.info("请求find页，token："+token+"，未查出userid");
			data.put("code", "2");
		}
		return data;
	}

	// 点赞
	public JSONObject Like(String token, String signinID) {
		JSONObject data = new JSONObject();
		// 先判断是否有这这个用户
		String userid = "";
		if (!(userid = this.getUserIDByToken(token)).matches("\\s{0,}")) {
			//有此用户，再判断是否点过赞
			if (!this.isAllreadyLike(userid, signinID)) {
				// 修改点赞表和签到记录表
				this.Like_signinRec(userid,signinID);
				this.Like_like(userid, signinID);
				// 点赞成功
				data.put("code", "1");
				logger.info("请求点赞，来自token："+token+"，待点赞记录id："+signinID+"，查到userid："+userid+"，点赞成功");
			} else {
				// 已经点过了
				data.put("code", "3");
				logger.info("请求点赞，来自token："+token+"，待点赞记录id："+signinID+"，查到userid："+userid+"，已经点过赞了");
			}
		} else {
			// 如果没有此用户
			logger.info("请求点赞，来自token："+token+"，待点赞记录id："+signinID+"，未查到userid");
			data.put("code", "2");
		}
		return data;
	}

	// 根据是否已经点过赞了
	public boolean isAllreadyLike(String userid, String signinID) {
		boolean result = false;
		String sql = "select * from likes where userid=? and signinID=?";
		Object[] params = { userid, signinID };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				// 有记录说明，点过赞了
				result = true;
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
		return result;
	}

	// 根据token查userid
	public String getUserIDByToken(String token) {
		String userid = "";
		String sql = "select userid from users where token=?";
		Object[] params = { token };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			if (rs.next()) {
				userid = rs.getString("userid");
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
		return userid;
	}

	// 点赞，修改签到记录表
	public boolean Like_signinRec(String userid,String signinID) {
		boolean result = false;
		String sql = "update signin_record set likes_num=likes_num+1  where signinID=?";
		Object[] params = { signinID };
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				logger.info("请求点赞，修改signin_record表成功，待点赞记录id："+signinID+"，userid："+userid);
			}else{
				logger.info("请求点赞，修改signin_record表失败，待点赞记录id："+signinID+"，userid："+userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 点赞，修改点赞表
	public boolean Like_like(String userid, String signinID) {
		boolean result = false;
		Object[] params = { signinID, userid };
		String sql = "insert into likes(signinID,userid) values(?,?)";
		// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				logger.info("请求点赞，修改likes表成功，待点赞记录id："+signinID+"，userid："+userid);
			}else{
				logger.info("请求点赞，修改likes表失败，待点赞记录id："+signinID+"，userid："+userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 获取userid和first_register，用于生成token
	public JSONObject getSignCalendar(String token) {
		JSONObject data = new JSONObject();
		// 先判断是否有这这个用户
		String userid = "";
		if (!(userid = this.getUserIDByToken(token)).matches("\\s{0,}")) {
			//有此用户
			logger.info("请求个人签到日历，来自token："+token+"，查到userid："+userid);
			String sql = "select date from signin_record where userid=?";
			Object[] params = { userid };
			ResultSet rs = super.executeQuery(sql, params);
			try {
				int count = 0;
				JSONArray cal_record = new JSONArray();
				while (rs.next()) {
					// 有记录，说明签过到
					cal_record.add(count, rs.getString("date"));
					++count;
				}
				// 没有记录
				if (count == 0)
					data.put("code", "3");
				else {
					data.put("code", "1");
					data.put("cal_record", cal_record);
				}
				logger.info("请求个人签到日历，查到数量："+count+"，来自userid："+userid);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			// 如果没有此用户
			logger.info("请求个人签到日历，来自token："+token+"，未查到userid");
			data.put("code", "2");
		}
		return data;
	}

	// 存储新获取的formid
	public boolean SetFormID(String formid) {
		boolean result = false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM:dd HH:mm:ss");
		Date now = new Date();
		String datetime = df.format(now);
		Object[] params = { formid, datetime };
		String sql = "insert into formid(formid,datetime) values(?,?)";
		// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				logger.info("存储成功 ，formid："+formid);
			}else{
				logger.info("存储失败 ，formid："+formid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 获取此时需要推送的用户表（还没签到或第一次使用的，并且不是机器人）
	public JSONArray getNowSendUsers(String beginTime) {
		System.out.println(beginTime);
		JSONArray data = new JSONArray();
		// 获取今天日期
		SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd");
		String todayDate = df.format(new Date());
		String sql = "select openid,endTime from users where beginTime=? and is_robot=1 " + 
							"and (last_signDate!=? or last_signDate is NULL)";//is null 表示一次没签过的
		Object[] params = { beginTime, todayDate };
		ResultSet rs = super.executeQuery(sql, params);
		try {
			int i = 0;
			while (rs.next()) {
				// 有记录，说明不是第一次
				JSONObject user = new JSONObject();
				user.put("openid", rs.getString("openid"));
				String endTime = rs.getString("endTime");
				if (endTime.equals("00:00:00"))
					endTime = "24:00:00";
				user.put("endTime", endTime);
				data.add(i, user);
				++i;
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
		return data;
	}

	// 获取同等数量的fomrid
	public JSONArray getFormID(int count) {
		JSONArray data = new JSONArray();
		String sql = "select formid from formid order by datetime DESC limit 0," + count;
		ResultSet rs = super.executeQuery(sql);
		try {
			int i = 0;
			while (rs.next()) {
				// 有记录，说明不是第一次
				data.add(i, rs.getString("formid"));
				++i;
			}
			logger.info("从数据库获取的formid数量：" + i);
		} catch (SQLException e) {
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

	// 删除已经使用过的fomrid
	public JSONArray deleteUsedFormID(JSONArray formidList) {
		JSONArray data = new JSONArray();
		int j = 0;
		for (int i = 0; i < formidList.size(); i++) {
			String sql = "delete from formid where formid=?";
			Object[] params = { formidList.getString(i) };
			try {
				int result = super.executeSQL(sql, params);
				if(result>0)
					++j;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("删除已使用的formid数量：" + j);
		return data;
	}

	// 存储反馈信息
	public boolean FeedBack(String token, String word) throws UnsupportedEncodingException {
		boolean result = false;
		// 对word编码,防止emoji
		final Base64.Encoder encoder = Base64.getEncoder();
		final byte[] textByte = word.getBytes("UTF-8");
		final String encodedText = encoder.encodeToString(textByte);
		// 添加新纪录
		String sql = "insert into feedback(token,word) values(?,?)";
		// 注意这里的values里面全是问号，下面用executeSQL(sql, params)函数替换
		Object[] params = { token, encodedText };
		try {
			int rows = super.executeSQL(sql, params);
			if (rows > 0) {
				result = true;
				logger.info("意见反馈成功，来自token:"+token);
			}else{
				logger.info("意见反馈失败，来自token:"+token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 查询总用户数，用于生成userid
	public int countUser() {
		int count = 0;
		String sql = "select COUNT(*) as rowcount from users";
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
