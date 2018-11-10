package Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.GetAllSignRecordServlet;

public class SignUtils {
	private final static Logger logger = LoggerFactory.getLogger(SignUtils.class);   
	public static boolean isInTime(String b, String e) {
		boolean result = false;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		if(e.equals("00:00:00"))
			e = "24:00:00";
		Date nowTime,startTime,endTime;
		try {
			nowTime = df.parse(df.format(System.currentTimeMillis()));
			startTime = df.parse(b);
			endTime = df.parse(e);
			//System.out.println("现在:"+nowTime);
			if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
				 result =  true;
			}
			Calendar date = Calendar.getInstance();
			date.setTime(nowTime);
			Calendar begin = Calendar.getInstance();
			begin.setTime(startTime);
			Calendar end = Calendar.getInstance();
			end.setTime(endTime);
			if (date.after(begin) && date.before(end)) {
				 result =  true;
			} else {
				 result =  false;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}		
		//System.out.println("signUtils:"+result);
		return result;
	}
	public static boolean isInTimeWithNow(String b, String e,String n) {
		boolean result = false;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		if(e.equals("00:00:00"))
			e = "24:00:00";
		Date nowTime,startTime,endTime;
		try {
			nowTime = df.parse(n);
			startTime = df.parse(b);
			endTime = df.parse(e);
			//System.out.println("现在:"+nowTime);
			if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
				 result =  true;
			}
			Calendar date = Calendar.getInstance();
			date.setTime(nowTime);
			Calendar begin = Calendar.getInstance();
			begin.setTime(startTime);
			Calendar end = Calendar.getInstance();
			end.setTime(endTime);
			if (date.after(begin) && date.before(end)) {
				 result =  true;
			} else {
				 result =  false;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}		
		//System.out.println("signUtils:"+result);
		return result;
	}
	  
	    /** 
	     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址; 
	     *  
	     * @param request 
	     * @return 
	     * @throws IOException 
	     */  
	    public final static String getIpAddress(HttpServletRequest request) throws IOException {  
	        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址  
	        String ip = request.getHeader("X-Forwarded-For");  
	        String info = "";
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
                info=" - X-Forwarded-For - ip：" + ip;  
            }    
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("Proxy-Client-IP"); 
	    	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
	                    info=" - Proxy-Client-IP - ip：" + ip;  
	                }  
	            }  
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("WL-Proxy-Client-IP");  
	                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
	                    info=" - WL-Proxy-Client-IP - ip：" + ip;  
	                }   
	            }  
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("HTTP_CLIENT_IP");  
	                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
	                    info=" - HTTP_CLIENT_IP - ip：" + ip;  
	                }     
	            }  
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
	                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
	                    info=" - HTTP_X_FORWARDED_FOR - ip：" + ip;  
	                }       
	            }  
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getRemoteAddr();  
	                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
	                    info=" - getRemoteAddr - ip：" + ip;  
	                }         
	            }  
	        } else if (ip.length() > 15) {  
	            String[] ips = ip.split(",");  
	            for (int index = 0; index < ips.length; index++) {  
	                String strIp = (String) ips[index];  
	                if (!("unknown".equalsIgnoreCase(strIp))) {  
	                    ip = strIp;  
	                    info=" - 多层代理 - ip:"+ip;
	                    break;  
	                }  
	            }  
	        }  
	        return info;  
	    }  
	/*
	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		System.out.println(hour + ":" + minute + ":" + second);
		System.out.println(isInTime("21:00:00","00:00:00"));
		System.out.println(isInTimeWithNow("21:00:00","00:00:00","01:00:00"));
		String s= "";
		if(s.matches("\\s{0,}"))
			System.out.println(1);

	}
			*/
}
