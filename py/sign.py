#coding:utf-8

import requests
import json
import time
import random
import datetime

import logging
import os
    
headers = {
    'content-type': 'application/x-www-form-urlencoded',
    'Accept': 'application/json'
}
word=['','安','安啦~','晚安','愿醒来一切安好。','早睡早起身体好！']
#大部分默认签到
wordNew=['','','','','安','晚安']

#添加机器人
def addRobot(nickName):
    url="*/wxxcx/AddRobotServlet"   
    jsonParams={
        'nickName':nickName
    }
    r = requests.post(url,headers=headers,data=jsonParams)
    print('自动签到返回状态码为：' + str(r.status_code))
    print(r.text)
    
#全部按顺序签到    
def signin():
    url="*/wxxcx/SigninRobotServlet"
    todayDate = time.strftime("%Y-%m-%d", time.localtime())
    #要改成0随机签到
    for num in range(1,21):
        jsonParams = {'token':'token'+str(num),
                 'word':wordNew[random.randint(0,2)],
                  'isPublic':'y',
                 'last_signDate':todayDate,
                    'likes_num':random.randint(0,1)
        }
        r = requests.post(url,headers=headers,data=jsonParams)
        print('token'+str(i)+'：自动签到返回状态码为：' + str(r.status_code))
        print(r.text)
        time.sleep(60*random.randint(5,8))
   

#随机签到,count为数量 
def signinRandom(count):
    url="*/wxxcx/SigninRobotServlet"
    todayDate = time.strftime("%Y-%m-%d", time.localtime())
    logging.info('开始签到，日期：'+todayDate)
    numPool =getSignOrderRandom(count)
    for num in numPool:
        jsonParams = {
            'token':'token'+str(num),
            'word':wordNew[random.randint(0,5)],
            'isPublic':'y',
            'last_signDate':todayDate,
            'likes_num':random.randint(0,1)
        }
        r = requests.post(url,headers=headers,data=jsonParams)
        nowTime = time.strftime("%H:%M:%S", time.localtime())
        logging.info('时间：'+nowTime+'，token'+str(num)+'：自动签到返回状态码为：' + str(r.status_code))
        logging.info('返回值：'+r.text)
        intervalMin = random.randint(5,8)
        intervalSec = random.randint(0,60)
        logging.info('暂停'+str(intervalMin)+"分钟"+str(intervalSec)+'秒')
        time.sleep(60*intervalMin+intervalSec)
    logging.info('今日自动签到结束！')
    logging.info('===================================================================')

#随机生成签到顺序,count为数量
def getSignOrderRandom(count):
    numPool =[]
    for num in range(0,count):
        #保证不重复
        while 1:
            num = random.randint(1,21)
            if num not in numPool:
                numPool.append(num)
                break
    return numPool

#日志配置
def setLogging():
    todayDate = time.strftime("%Y-%m-%d", time.localtime())    
    logging.basicConfig( 
        #filename=os.path.join(os.getcwd(),'/robotSignLog/'+todayDate+'.txt'),
        filename=os.path.join('C:/Users/Administrator/Desktop/','robotSignLog/'+todayDate+'.txt'),
        # 定义输出log的格式
        format='%(message)s',
        level=logging.INFO
    )
    #同时输出到控制台
    console = logging.StreamHandler();
    console.setLevel(logging.INFO);
    formatter = logging.Formatter('%(message)s');
    console.setFormatter(formatter);
    logging.getLogger('').addHandler(console);
    
setLogging()
signinRandom(15)



