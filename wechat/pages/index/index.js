//index.js
//获取应用实例
const app = getApp()
var util = require('../../utils/util.js');  
Page({
  data: {
    sign_days:0,
    running_days:0,
    long_running_days:0,
    motoo: "总有人黑着眼眶熬着夜。",
    userInfo: {},
    beginTime:'21:00',
    endTime: '24:00',
    signinPic:'../../images/sleep-1.png',
  },
  //修改签到时间
  updateTime: function () {
    wx.navigateTo({
      url: '../time/time'
    })
  },
  //签到
  signin: function (e) {
    let that = this
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo']) {
          //有授权，再看今天是否已经签到
         // let last_signDate = wx.getStorageSync('last_signDate')
          let nowDate = util.formatDate(new Date());
          let token = wx.getStorageSync('token')
          //console.log(last_signDate)
          wx.request({
            url: app.globalData.domain+'/SigninJudgeServlet',
              method: "POST",
              data: {
                token: token,
              },
              header: {
                //设置参数内容类型为x-www-form-urlencoded
                'content-type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
              },
              success: function (result) {
                //可以签到
                if (result.data.code == 1) {
                  wx.navigateTo({
                    url: '../sign/sign',
                  })
                } else if (result.data.code == 2){
                  //签到失败,已经签过到了
                  wx.showModal({
                    title: '提示',
                    content: '已经签过到了',
                    showCancel: false
                  })
                } else if (result.data.code == 3) {
                  //签到失败,不在签到时间段
                  that.toastShow('不在签到时间段 ', "../../images/warn.png");
                }
              }
            })
        } else {
          //没有授权。导向授权页
          //console.log("用户没有授权过/授权信息已不存在");
          wx.showModal({
            title: '提示',
            content: '请先前往“我的主页”授权登录',
            showCancel: false,
            //自动导向我的主页
            success: function (res) {
              if (res.confirm) {
                setTimeout(function () {
                  wx.switchTab({
                    url: '../my/my'
                  })
                }, 1000) 
              }
            }
          })
        }
      }
    })
    let formid = e.detail.formId
    //console.log(formid)
    util.sendFormID(formid)
  },
  //自定义toast
   toastShow: function (str, icon) {
    var _this = this;
    _this.setData({
      isShow: true,
      txt: str,
      icon: icon
    });
    setTimeout(function () {    //toast消失
      _this.setData({
        isShow: false
      });
    }, 1500);
  },
  onLoad: function () {
    let last_signDate = wx.getStorageSync('last_signDate')
    if (last_signDate!=''){
      //获取今天日期
      let nowDate = util.formatDate(new Date());
      //如果今天已经签过到了
      if (nowDate == last_signDate){
        this.setData({
          signinPic: '../../images/sleep-3.png'
        })
      }
    }
    //设置签到时间
    let temp = wx.getStorageSync('beginTime')
    let beginTime = temp == '' ? '21:00' : temp
    temp = wx.getStorageSync('endTime')
    let endTime = temp == '' ? '24:00' : temp
    this.setData({
      beginTime: beginTime,
      endTime: endTime,
    })
    //设置签到记录
    let temp_record = wx.getStorageSync('sign_days')
    let sign_days = temp_record == '' ? 0 : temp_record
    temp_record = wx.getStorageSync('running_days')
    let running_days = temp_record == '' ? 0 : temp_record
    temp_record = wx.getStorageSync('long_running_days')
    let long_running_days = temp_record == '' ? 0 : temp_record
    this.setData({
      sign_days: sign_days,
      running_days: running_days,
      long_running_days: long_running_days,
    })
    
  },
  onShow: function () {
    let last_signDate = wx.getStorageSync('last_signDate')
    if (last_signDate != '') {
      //获取今天日期
      let nowDate = util.formatDate(new Date());
      //如果今天已经签过到了
      if (nowDate == last_signDate) {
        this.setData({
          signinPic: '../../images/sleep-3.png'
        })
      }
    }
    //设置签到时间
    let temp = wx.getStorageSync('beginTime')
    let beginTime = temp == '' ? '21:00' : temp
    temp = wx.getStorageSync('endTime')
    let endTime = temp == '' ? '24:00' : temp
    this.setData({
      beginTime: beginTime,
      endTime: endTime,
    })
    //设置签到记录
    let temp_record = wx.getStorageSync('sign_days')
    let sign_days = temp_record == '' ? 0 : temp_record
    temp_record = wx.getStorageSync('running_days')
    let running_days = temp_record == '' ? 0 : temp_record
    temp_record = wx.getStorageSync('long_running_days')
    let long_running_days = temp_record == '' ? 0 : temp_record
    this.setData({
      sign_days: sign_days,
      running_days: running_days,
      long_running_days: long_running_days,
    })
  },
  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (option) {
    console.log(option)
    /*
    return {
      title: '自定义转发标题',
      path: '/page/index/index'
    }
    */

  }
})
