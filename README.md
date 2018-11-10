# sleep
早睡签到打卡小程序

## 说明
* wechat文件夹：微信小程序端源码，简单应用[微信小程序api](https://developers.weixin.qq.com/miniprogram/dev/api/)，前端界面样式模仿的早起签到打卡小程序
* wxxcx文件夹：Java后端，主要是Servlet的简单运用，TimerTask+ServletContextListener监听定时任务
* py文件夹：py脚本，操作系统定时启动，用于机器人签到、删除过期formid
* 数据库为MySQL，users表、formid表、signin_record表、likes表、feedback表 
* PS：一些重要信息用*隐去了，比如appid

## 功能
* 1.自定义签到时间段
* 2.用户可以通过日历查看签到情况，也可以列表查看
* 3.用户每天签到的同时，可分享一些文字并发布到发现页，用户相互之间可以点赞
* 4.如果用户忘记签到，小程序会自动发送模板消息提醒用户
* 5.意见反馈（现在小程序推出这个功能了，当时还没有...）

## 界面（我的美工是真的差，勿喷...）
### 主页
![主页](https://github.com/zmdgg/sleep/blob/master/show/index.png)
### 发现页
![发现页](https://github.com/zmdgg/sleep/blob/master/show/find.png)
### 个人页
![个人页](https://github.com/zmdgg/sleep/blob/master/show/home.png)
