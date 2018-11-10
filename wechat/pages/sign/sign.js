// pages/sign/sign.js
var util = require('../../utils/util.js');
const app = getApp();
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
      that.setData({
        loading: true,
        buttonDis:true
      })
      //console.log(e.detail.value)
      let isPublic = e.detail.value["isPublic"][0] == "public" ? 'y' : 'n'
      let nowDate = util.formatDate(new Date());
      //console.log(e.detail.value["isPublic"])
      //console.log(isPublic)
      wx.request({
        url: app.globalData.domain+'/SigninServlet',
        method: "POST",
        data: {
          token: wx.getStorageSync("token"),
          word: e.detail.value["word"],
          isPublic: isPublic,
          last_signDate: nowDate,
        },
        header: {
          //设置参数内容类型为x-www-form-urlencoded
          'content-type': 'application/x-www-form-urlencoded',
          'Accept': 'application/json'
        },
        success: function(result) {
          wx.setStorageSync("sign_days", result.data.sign_days)
          wx.setStorageSync("running_days", result.data.running_days)
          wx.setStorageSync("long_running_days", result.data.long_running_days)
          wx.setStorageSync("last_signDate", nowDate)
          that.setData({
            signinPic: '../../images/sleep-3.png'
          })
          setTimeout(function() {
            that.setData({
              loading: false
            })
          }, 500)
          setTimeout(function() {
            wx.showToast({
              title: '打卡成功',
              icon: 'success',
              duration: 2000
            })
          }, 1000)
          setTimeout(function() {
            wx.switchTab({
              url: '../find/find'
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