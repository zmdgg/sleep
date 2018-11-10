//获取日期和时间
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}
//不足补零
const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}
//获取日期
const formatDate = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return [year, month, day].map(formatNumber).join('-')
}
//发送formid
const sendFormID = formid => {
  wx.request({
    url: 'https://zhangmm.top/wxxcx/SetFormIDServlet',
    method: "POST",
    data: {
      formid: formid
    },
    header: {
      //设置参数内容类型为x-www-form-urlencoded
      'content-type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json'
    },
  })
}
module.exports = {
  formatTime: formatTime,
  formatDate: formatDate,
  sendFormID: sendFormID
}
