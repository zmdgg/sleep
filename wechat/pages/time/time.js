// pages/time/time.js
const app = getApp()
var util = require('../../utils/util.js');  
//设置显示时间
let temp = wx.getStorageSync('beginTime')
let beginTime = temp == '' ? '21:00' : temp
temp = wx.getStorageSync('endTime')
let endTime = temp == '' ? '24:00' : temp
//初始化picker,并计算picker选择默认选中的时间
const sel_beginTime = []
const sel_endTime = []
let begin_index = 0
let end_index = 0
for (let i = 19; i <= 23; i++) {
  sel_beginTime.push(i+':00')
  if (beginTime == (i + ':00')){
    begin_index = i-19
  }
}
for (let i = 20; i <= 24; i++) {
  sel_endTime.push(i + ':00')
  if (endTime == (i + ':00'))
    end_index = i-20
}
Page({
  /**
   * 页面的初始数据
   */
  data: {
    sel_beginTime: sel_beginTime,
    sel_endTime: sel_endTime,
    begin_index: begin_index,
    end_index: end_index,
    beginTime: beginTime,
    endTime: endTime,
    buttonDis:false
    /*
    beginTime: sel_beginTime[begin_index],
    endTime: sel_endTime[end_index]
    */
  },
  bindBeginTimeChange:function(e){
   // console.log(e.detail.value + "," + this.data.end_index)
    let right_index = e.detail.value
   //如果开始时间大于等于结束时间
    if (right_index>= this.data.end_index){
      right_index = this.data.end_index
    }
    this.setData({
      beginTime: sel_beginTime[right_index],
      begin_index: right_index
    })
   // console.log(this.data.begin_index + "," + this.data.end_index)
   // console.log(this.data.beginTime + "," + this.data.endTime)
  },
  bindEndTimeChange: function (e) {
    let right_index = e.detail.value
    //如果结束时间小于或等于开始时间
    if (this.data.begin_index>=right_index){
      right_index = this.data.begin_index
    }
    this.setData({
      endTime: sel_endTime[right_index],
      end_index: right_index
    })
  },
  bindUpdateTime:function(e){
    let that = this
    if(that.data.buttonDis==false){
     // console.log(123)
      that.setData({
        buttonDis: true
      })
    //先判断是否有授权
    wx.getSetting({
      success: function (res) {
        //console.log(res)
        if (res.authSetting['scope.userInfo']) {
          //有授权，再看今天是否修改过时间
          let last_update = wx.getStorageSync('last_update') 
          let nowDate = util.formatDate(new Date());     
          let token = wx.getStorageSync('token') 
          //上次修改日期与当期日期不一样，说明今天没有设置过
          if (last_update != nowDate){
            wx.request({
              url: app.globalData.domain+'/SetTimeServlet',
              method: "POST",
              data: {
                token: token,
                last_update:nowDate,
                beginTime:that.data.beginTime,
                endTime: that.data.endTime,
              },
              header: {
                //设置参数内容类型为x-www-form-urlencoded
                'content-type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
              },
              success: function (result) {
                //设置成功
                if(result.data.code==1){
                  wx.showToast({
                    title: '设置成功',
                    icon: 'success',
                    duration: 2000
                  })
                  wx.setStorageSync("last_update", nowDate)
                  wx.setStorageSync("beginTime", that.data.beginTime)
                  wx.setStorageSync("endTime", that.data.endTime)
                  that.setData({
                    last_update: nowDate,
                    beginTime: that.data.beginTime,
                    endTime: that.data.endTime,
                  })
                  setTimeout(function () {
                    wx.navigateBack({
                      delta: 1
                    })
                  }, 1000)           
                }else{
                  //设置失败
                  wx.showModal({
                    title: '提示',
                    content: '设置失败',
                    showCancel: false
                  })
                }
              }
            })
            
          }else{
            wx.showModal({
              title: '提示',
              content: '今天已经设置过了',
              showCancel: false
            })
          }
        
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
    }
    let formid = e.detail.formId
    //console.log(formid)
    util.sendFormID(formid)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let beginTime = wx.getStorageSync("beginTime")
    let endTime = wx.getStorageSync("endTime")
    if(beginTime!=''){
      this.setData({
        beginTime: beginTime,
        endTime: endTime,
      })
    }
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
      this.setData({
        buttonDis:false
      })
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
  test:function(e){
    console.log(e)
  }
})