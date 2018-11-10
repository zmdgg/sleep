// pages/signRecord/signRecord.js
var localData = []
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    count: 0,
    dataList: localData,
    hidden:true
  },
  bindLike: function (e) {
    //console.log(e)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this
    // 查看是否授权过
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo']) {
          that.setData({
            hidden:false
          })
          wx.request({
            url: app.globalData.domain+'/GetMySignRecordServlet',
            method: "POST",
            data: {
              token: wx.getStorageSync("token")
            },
            header: {
              //设置参数内容类型为x-www-form-urlencoded
              'content-type': 'application/x-www-form-urlencoded',
              'Accept': 'application/json'
            },
            success: function (result) {
              let count = result.data.count
              //console.log(result.data.count);
              if (count != 0) {
                that.setData({
                  count: count,
                  dataList: result.data.sign_record.reverse()
                })
              }
            },
            fail: function (e) {
              console.log(e)
            }
          })
          that.setData({
            hidden: true
          })
          console.log("用户已经授权过")
        } else {
          console.log("用户没有授权过/授权信息已不存在");
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

})