// pages/sign/sign.js
var util = require('../../utils/util.js');
const app = getApp()
Page({
  /**
   * 页面的初始数据
   */
  data: {
    items: [{
      name: 'CHN',
      value: '公开',
      checked: 'true'
    }, ],
    loading: false,
    buttonDis: false
  },
  bindSignin: function(e) {
    let that = this
    if (that.data.buttonDis == false) {
     // console.log(123)
      that.setData({
        loading: true,
        buttonDis: true
      })
      wx.request({
        url: app.globalData.domain+'/FeedbackServlet',
        method: "POST",
        data: {
          token: wx.getStorageSync("token"),
          word: e.detail.value["word"],
        },
        header: {
          //设置参数内容类型为x-www-form-urlencoded
          'content-type': 'application/x-www-form-urlencoded',
          'Accept': 'application/json'
        },
        success: function(result) {
          setTimeout(function() {
            that.setData({
              loading: false
            })
          }, 500)
          setTimeout(function() {
            wx.showToast({
              title: '感谢您的反馈！',
              icon: 'success',
              duration: 2000
            })
          }, 1000)
          setTimeout(function() {
            wx.switchTab({
              url: '../my/my'
            })
          }, 3000)
        },
        fail: function() {
          that.setData({
            loading: false
          })
        }
      })
    }
    let formid = e.detail.formId
    //console.log(formid)
    util.sendFormID(formid)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
    this.setData({
      buttonDis: false
    })
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

})