//app.js
App(
  {
  
  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    
  },
  globalData: {
    userInfo: null  ,
    domain:"http://127.0.0.1:8080/wxxcx"
  }
})