//index.js  
//获取应用实例  
const app = getApp()
var util = require('../../utils/util.js');  
Page({
  data: {
    year: 0,
    month: 0,
    date: ['日', '一', '二', '三', '四', '五', '六'],
    dateArr: [],
    cal_record: []
  },
  onLoad: function () {
    //获取当前年月
    let now = new Date();
    let year = now.getFullYear();
    //let month = util.getMonth(new Date());
    let month = now.getMonth()+1;
    //console.log(now)
    //console.log(year)
    //console.log(month)
    let that = this
    // 查看是否授权过
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo']) {
          wx.request({
            url: app.globalData.domain+'/GetSignCalendarServlet',
            method: "POST",
            data: {
              token: wx.getStorageSync('token')
            },
            header: {
              //设置参数内容类型为x-www-form-urlencoded
              'content-type': 'application/x-www-form-urlencoded',
              'Accept': 'application/json'
            },
            success: function (result) {
              let code = result.data.code
              //有数据
              if (code == 1) {
                let cal_record = result.data.cal_record
                //console.log(cal_record)
                that.setData({
                  cal_record: cal_record
                })

              } else if (code == 2) {
                //无此用户  

              }
              that.dateInit();
            },
            fail: function (e) {
              //console.log(e)
              that.dateInit();
            }
          })
          //console.log("用户已经授权过")
        } else {
          //console.log("用户没有授权过/授权信息已不存在");
         that.dateInit();
        }
        that.setData({
          year: year,
          month: month,
        })
      }
    })
  },
  dateInit: function (setYear, setMonth) {
    //全部时间的月份都是按0~11基准，显示月份才+1  
    let dateArr = [];                       //需要遍历的日历数组数据  
    let arrLen = 0;                         //dateArr的数组长度  
    let now = setYear ? new Date(setYear, setMonth) : new Date();
    let year = setYear || now.getFullYear();
    let nextYear = 0;
    let month = setMonth ||  now.getMonth(); //没有+1方便后面计算当月总天数  
    let nextMonth = (month + 1) > 11 ? 1 : (month + 1);
    let tempMonth = month;
    if (tempMonth + 1 < 10)
      tempMonth = '0' + (tempMonth + 1)
    //startWeek是目标月1号对应的星期，已/为界（兼容苹果）
    let startWeek = new Date(year + '/' + (tempMonth) + '/' + '01').getDay(); 
    //console.log(year + ',' + (month + 1) + ',' + 1)                        
    let dayNums = new Date(year, nextMonth, 0).getDate();    //获取目标月有多少天  
    let obj = {};
    let num = 0;
    if (month + 1 > 11) {
      nextYear = year + 1;
      dayNums = new Date(nextYear, nextMonth, 0).getDate();
    }
    if (month + 1 < 10)
      month = '0' + (month + 1)
    arrLen = startWeek + dayNums;
    //console.log('arrLen:'+arrLen)
    //console.log('startWeek:' + startWeek)
    //onsole.log('dayNums:' + dayNums)
    for (let i = 0; i < arrLen; i++) {
      if (i >= startWeek) {
        num = i - startWeek + 1;
        let t = num
        if (t<10)
          t = '0' + t
        obj = {
          date: '' + year + '-' + month + '-' + t,
          dateNum: num,
          isSignin:0//标记是否签过到
        }
      } else {
        obj = {};
      }
      dateArr[i] = obj;
    }
    //标记签到的日期
    let that = this
    let cal_record = that.data.cal_record
   // console.log(cal_record)
    //console.log(dateArr)
    for (let i in dateArr) {
      if (dateArr[i]['date']){
        for (var j in cal_record){
          if (dateArr[i]['date'] == cal_record[j])
            dateArr[i]['isSignin']=1
        }
      }
    }
    this.setData({
      dateArr: dateArr
    })
  },
  lastMonth: function () {
    //全部时间的月份都是按0~11基准，显示月份才+1  
    let year = this.data.month - 2 < 0 ? this.data.year - 1 : this.data.year;
    let month = this.data.month - 2 < 0 ? 11 : this.data.month - 2;
    this.setData({
      year: year,
      month: (month + 1)
    })
    this.dateInit(year, month);
  },
  nextMonth: function () {
    //全部时间的月份都是按0~11基准，显示月份才+1  
    let year = this.data.month > 11 ? this.data.year + 1 : this.data.year;
    let month = this.data.month > 11 ? 0 : this.data.month;
    this.setData({
      year: year,
      month: (month + 1)
    })
    this.dateInit(year, month);
  }
})  