// pages/find/find.js
var localData = []
var util = require('../../utils/util.js');
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    count: 0,
    dataList: localData,
    pageIndex: 0,
    pageCount: 10,
    hasMoreData: true,
    hidden: true
  },
  //点赞
  bindLike: function(e) {
    let signinID = e.currentTarget.dataset.id
    let token = wx.getStorageSync("token")
    let that = this
    console.log(signinID)
    wx.request({
      url: app.globalData.domain+'/LikeServlet',
      method: "POST",
      data: {
        token: token,
        signinID: signinID
      },
      header: {
        //设置参数内容类型为x-www-form-urlencoded
        'content-type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json'
      },
      success: function(result) {
        let code = result.data.code
        //console.log(result.data.count)
        console.log(code);
        if (code == 1) {
          let list = that.data.dataList
          for (var i in list) {
            //console.log(list[i]['signinID'])
            if (list[i]['signinID'] == signinID) {
              //点赞数
              list[i]['likes_num'] = parseInt(list[i]['likes_num']) + 1
              list[i]['isLike'] = 'd'
            }
          }
          that.setData({
            dataList: list,
          })
        } else if (code == 3) {
          wx.showToast({
            title: '已经点过赞了',
            icon: 'none'
          })
        }

      },
      fail: function(e) {
        //console.log(e)
      }
    })
    let formid = e.detail.formId
    util.sendFormID(formid)
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    let that = this
    // 查看是否授权过
    wx.getSetting({
      success: function(res) {
        if (res.authSetting['scope.userInfo']) {
          that.setData({
            hidden: false
          })
          wx.request({
            url: app.globalData.domain+'/GetAllSignRecordServlet',
            method: "POST",
            data: {
              pageIndex: that.data.pageIndex,
              pageCount: that.data.pageCount,
              token: wx.getStorageSync("token"),
            },
            header: {
              //设置参数内容类型为x-www-form-urlencoded
              'content-type': 'application/x-www-form-urlencoded',
              'Accept': 'application/json'
            },
            success: function(result) {
              //console.log(result)
              let count = result.data.count
              //console.log(result.data.count);
              if (count != 0) {
                that.setData({
                  count: count,
                  dataList: result.data.sign_record,
                })
              }
            },
            fail: function(e) {
              //console.log(e)
            }
          })
          that.setData({
            hidden: true,
            hasMoreData: true
          })
          //console.log("用户已经授权过")
        } else {
          //console.log("用户没有授权过/授权信息已不存在");
          wx.showModal({
            title: '提示',
            content: '请先前往"我的主页"授权登录',
            showCancel:false,
            success: function (res) {
              if (res.confirm) {
                wx.switchTab({
                  url: '../my/my',
                })
              }
            }
          })
        }
      }
    })

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

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {
    this.setData({
      hasMoreData: true
    })
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
    let that = this
    that.setData({
      hidden: false,
    })
    // 查看是否授权过
    wx.getSetting({
      success: function(res) {
        if (res.authSetting['scope.userInfo']) {
          wx.request({
            url: app.globalData.domain+'/GetAllSignRecordServlet',
            method: "POST",
            data: {
              pageIndex: 0,
              pageCount: that.data.pageCount,
              token: wx.getStorageSync("token"),
            },
            header: {
              //设置参数内容类型为x-www-form-urlencoded
              'content-type': 'application/x-www-form-urlencoded',
              'Accept': 'application/json'
            },
            success: function(result) {
              //console.log(result)
              let count = result.data.count
              //console.log("进来" + that.data.hasMoreData)
              //console.log(result.data.count);
              if (count != 0) {
                that.setData({
                  count: count,
                  pageIndex:0,
                  dataList: result.data.sign_record,
                  hasMoreData: true,
                })
                //console.log("修改以后" + that.data.hasMoreData)
              }
            },
            fail: function(e) {
             // console.log(e)
            }
          })
          //console.log("用户已经授权过")
        } else {
          //console.log("用户没有授权过/授权信息已不存在");
        }
      }
    })
    //console.log("之前" + that.data.hasMoreData)
    that.setData({
      hasMoreData: true,
      hidden: true,
    })
    //console.log("之后" + that.data.hasMoreData)
    //最后需要加一个停止刷新，防止卡在刷新状态下
    wx.stopPullDownRefresh();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {
    //console.log(1)
    let that = this

    // 查看是否授权过
    wx.getSetting({
      success: function(res) {
        if (res.authSetting['scope.userInfo']) {
          //console.log("上啦" + that.data.hasMoreData)
          //判断是否还有更多数据
          if (that.data.hasMoreData != true) {
            wx.showToast({
              title: '已加载全部数据',
              icon: 'none'
            })
          } else {
            that.setData({
              hidden: false,
            })
            wx.request({
              url: app.globalData.domain+'/GetAllSignRecordServlet',
              method: "POST",
              data: {
                pageIndex: that.data.pageIndex + 1,
                pageCount: that.data.pageCount,
                token: wx.getStorageSync("token")
              },
              header: {
                //设置参数内容类型为x-www-form-urlencoded
                'content-type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
              },
              success: function(result) {
                //console.log(result)
                let count = result.data.count
                if (count != 0) {
                  let newdataList = that.data.dataList
                  let t = result.data.sign_record
                  //下拉追加
                  for (var i in t) {
                    newdataList.push(t[i]);
                  }
                  newdataList.push()
                  that.setData({
                    count: count,
                    dataList: newdataList,
                    pageIndex: that.data.pageIndex + 1,
                  })
                }
                //如果没有更多数据
                if (count < that.data.pageCount || count == 0) {
                  //console.log('判断')
                  that.setData({
                    hasMoreData: false
                  })
                }
              },
              fail: function(e) {
                //onsole.log(e)
              }
            })
            that.setData({
              hidden: true,
            })
          }
          //console.log("用户已经授权过")
        } else {
          //console.log("用户没有授权过/授权信息已不存在");
        }
      }
    })

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (option) {
    console.log(option)
  }
})