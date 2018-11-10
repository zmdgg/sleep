var util = require('../../utils/util.js');  
const app = getApp();
Page({
  data: {
    //测试授权结果
    nickName: "",
    avatarUrl: "../../images/photo.png",
    ID: "请授权登录",
    //判断小程序的API，回调，参数，组件等是否在当前版本可用。
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    beginTime: '21:00',
    endTime: '24:00',
  },
  onLoad: function () {
    let that = this
    // 查看是否授权过
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo']) {
          let userInfo = wx.getStorageSync('userInfo')
          let userid = wx.getStorageSync('userid')
          //签到时间
          let temp = wx.getStorageSync('beginTime')
          let beginTime = temp == '' ? '21:00' : temp
          temp = wx.getStorageSync('endTime')
          let endTime = temp == '' ? '24:00' : temp
          that.setData({
            userInfo: userInfo,
            nickName: userInfo.nickName,
            avatarUrl: userInfo.avatarUrl,
            ID:"ID:"+userid,
            beginTime: beginTime,
            endTime: endTime
          })
          //console.log("用户已经授权过")
        }else{
          //console.log("用户没有授权过/授权信息已不存在");
        }
      }
    })
  },
  bindGetUserInfo: function (e) {
    //console.log(e.detail.userInfo)
    //console.log(e.detail.userInfo.avatarUrl)
    //console.log("用户正在授权中，结果：")
    if (e.detail.userInfo) {
      //用户按了允许授权按钮
      //console.log("允许授权")
      this.login(e.detail.userInfo)
    } else {
      //用户按了拒绝按钮
      //console.log("拒绝授权")
      return;
    }
  },
  login:function(userInfo){
    //console.log("授权处理中")
    let that = this
    wx.login({
      success: function (res) {
        wx.request({
          url: app.globalData.domain+'/LoginServlet',
          method:"POST",
          data: {
            code: res.code,
            nickName: userInfo.nickName,
            avatarUrl: userInfo.avatarUrl,       
          },
          header: {
            //设置参数内容类型为x-www-form-urlencoded
            'content-type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
          },
          success: function (result) {
            //console.log(result.data);
            //console.log(result.data.token);
            //console.log(userInfo)
            wx.setStorageSync('token', result.data.token)
            wx.setStorageSync('userid', result.data.userid)
            wx.setStorageSync('userInfo', userInfo)
            that.setData({
              userInfo: userInfo,
              nickName: userInfo.nickName,
              avatarUrl: userInfo.avatarUrl,
              ID: "ID:" + wx.getStorageSync('userid')
            }),
            //console.log("成功")
            that.setTime(result.data.token)
          },
           fail: function (e) {
            //console.log(e)
          }
        })
      }
    })
    //console.log(wx.getStorageSync('token'))
  },
  setTime: function (token) {
    let that = this
   // console.log(token)
    //正在判断是否设置过时间
    wx.request({
      url: app.globalData.domain+'/GetTimeServlet',
      method: "POST",
      data: {
        token: token
      },
      header: {
        //设置参数内容类型为x-www-form-urlencoded
        'content-type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json'
      },
      success: function (result) {
       // console.log(result)
       // console.log("beginTime:" + result.data.beginTime)
        //如果没设置过
        if (result.data.last_update==''){
         // console.log("用户没设置过")
          wx.navigateTo({
            url: '../time/time'
          })
        }else{
         // console.log("用户设置过")
          wx.setStorageSync('beginTime', result.data.beginTime)
          wx.setStorageSync('endTime', result.data.endTime)
          wx.setStorageSync('last_update', result.data.last_update)
          that.setData({
            beginTime: result.data.beginTime,
            endTime: result.data.endTime
          })
        }
      }
    })
  },
  onShow:function(){
    let temp = wx.getStorageSync('beginTime')
    let beginTime = temp == '' ? '21:00' : temp
    temp = wx.getStorageSync('endTime')
    let endTime = temp == '' ? '24:00' : temp
    this.setData({
      beginTime: beginTime,
      endTime: endTime
    })

  },
  bindFunc: function (e) {
    //console.log(e)
    let formid = e.detail.formId
    //console.log(formid)
    util.sendFormID(formid)
  },
bindToUpdateTime:function(){
    wx.navigateTo({
      url: '../time/time'
    })
  },
bindSignRecord:function(){
  wx.navigateTo({
    url: '../signRecord/signRecord'
  })
},
bindToCalRecord: function () {
    wx.navigateTo({
      url: '../calendar/calendar'
    })
},
bindToAdmire:function(){
  wx.navigateTo({
    url: '../admire/admire'
  })
},
bindToAbout: function () {
  wx.navigateTo({
    url: '../about/about'
  })
},
  bindToFeedback:function(){
    wx.navigateTo({
      url: '../feedback/feedback'
    })
} ,
 /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (option) {
  console.log(option)
}
})