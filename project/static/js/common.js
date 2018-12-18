/**常用工具类*/
var commonUtils = {};
var dateUtils = {};
/**
 * 校验是否为空(先删除二边空格再验证)
 * result: true：为空;false相反
 */
commonUtils.isNull = function(str){
	if(str==undefined){
		return true;
	}
	if(typeof(str) == "string"){
		str = $.trim(str);
		if (null == str || "" == str || 'undefined'==str) {
			return true;
		} else {
			return false;
		}
	}else{
		if(str!==null){
			return false;
		}else{
			return true;
		}
	}
};

/**
* add时间方法
*/
dateUtils.changeDays = function changeDays(dateStr, days) {
	var isdate = new Date(dateStr.replace(/-/g,"/"));  //把日期字符串转换成日期格式
	isdate = new Date((isdate/1000+(86400*days))*1000);  //日期加days天
	var dataStr = isdate.getFullYear()+"-"+(isdate.getMonth()+1)+"-"+(isdate.getDate());
	return dataStr;
}
/**
* 获取日期方法
*/
dateUtils.getDateStr = function getDateStr() {
	var nowDate = new Date(); 
	var date = nowDate.getFullYear()+"-"+(nowDate.getMonth()+1)+"-"+(nowDate.getDate()) 
	return date;
}
dateUtils.getDateTimeStr = function(value) {
	value = value || "";
    if (value == "") {
        return value;
    }
    var date = new Date(value);
    return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
}
/**
 * (function() {}())表示立即执行函数，比jquery的初始化函数还前。
 * 使用方法:
 * 开启:MaskUtil.mask();
 * 关闭:MaskUtil.unmask();
 * 
 * MaskUtil.mask('其它提示文字...');
 * */
var MaskUtil = (function(){
	var $mask,$maskMsg;
	var defMsg = '正在处理，请稍待。。。';
	function init(){
		if(!$mask){
			$mask = $("<div class=\"datagrid-mask mymask\"></div>").appendTo("body");
		}
		if(!$maskMsg){
			$maskMsg = $("<div class=\"datagrid-mask-msg mymask\">"+defMsg+"</div>")
				.appendTo("body").css({'font-size':'12px'});
		}
		$mask.css({width:"100%",height:$(document).height()});
		var scrollTop = $(document.body).scrollTop();
		$maskMsg.css({
			left:( $(document.body).outerWidth(true) - 190 ) / 2
			,top:( ($(window).height() - 45) / 2 ) + scrollTop
		}); 
	}
	return {
		mask:function(msg){
			init();
			$mask.show();
			$maskMsg.html(msg||defMsg).show();
		}
		,unmask:function(){
			$mask.hide();
			$maskMsg.hide();
		}
	}
}());
/**串行睡眠commonUtils.sleep(1000) 睡眠一秒*/
commonUtils.sleep = function (s) {
	var t = Date.now();
	function sleep(d){
		while(Date.now - t <= s);
	} 
}