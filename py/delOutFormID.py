#coding:utf-8

import time
import logging
import os
import pymysql    

def delOutFormID():
    now = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    logging.info("时间："+now+"，开始清除即将过期的formID")
    #先查询需要删除个数
    sql_sel = """select * from formid
                     where datetime not between
                     current_date()-6 and sysdate()
                     ORDER BY datetime"""
    needDelCount =  cursor.execute(sql_sel)
    #获取第一条数据
    formIDList = cursor.fetchone()
    logging.info("清除即将过期formID的个数："+str(needDelCount))
    if needDelCount > 0:
        logging.info("距今最久的formID："+str(formIDList[0]))
        logging.info("该formID的日期："+str(formIDList[1]))
        #删除使用期限不在6天内的formID
        sql_del = """delete from formid
                         where datetime not between
                         current_date()-6 and sysdate()"""
        delCount =  cursor.execute(sql_del)
        #插入和修改需要加下面这句
        conn.commit()
        logging.info("删除的即将过期formID的个数："+delCount)
    else:
        logging.info("无需删除")
    #查询剩余formID个数
    sql_selCount = """select count(*) from formid"""
    cursor.execute(sql_selCount)
    count = cursor.fetchone()
    logging.info("剩余的formID的个数："+str(count[0]))
    logging.info("今天的清除工作完成")
    logging.info('===================================================================')

#日志配置
def setLogging():
    todayDate = time.strftime("%Y-%m-%d", time.localtime())    
    logging.basicConfig(
        #filename=os.path.join(os.getcwd(),'/delFormIDLog/'+todayDate+'.txt'),
        filename=os.path.join('C:/Users/Administrator/Desktop/','delFormIDLog/'+todayDate+'.txt'),
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

#连接数据库
conn = pymysql.connect(
    host = '127.0.0.1',
    port = 3306,user = 'root',
    passwd = '*',
    db = 'wxxcx',
    charset = 'utf8')
cursor = conn.cursor()   
setLogging()


delOutFormID()
os.system('pause')
#time.sleep(200)
#关闭数据库
cursor.close()
conn.close()

