

var commonWindow = null;

var  EXT_COMMON = {

    /**
     * 调用window组件，打开URL LINK
     * @param title
     * @param url  {}
     * @param params
     */
    viewLink : function(title,url,params){
        if(commonWindow == null){
            commonWindow = new Ext.window.Window({
                title :'预览',width:EXT_COMMON.bodyWidth() - 200,height:EXT_COMMON.bodyHeight() - 120 ,
                closable:true, maximizable:true,closeAction:'hide',modal:true,
                layout:'fit',html:'<iframe id="common_system_window_for_ext_common_util" style="width: 100%;height: 100%;border: none;" />',
            });

        }
        commonWindow.show();
        commonWindow.setTitle(title);
        if(!params ){
            params =[] ;
        }else{
            params = (params+"").split(",");
        }
        for ( var i=0;i<params.length;i++){
            url = url.replace("{}",params[i]);
        }
        loadURL(url,'common_system_window_for_ext_common_util');
    },

    bodyWidth : function(){
        return Ext.getBody().getWidth();
    },
    bodyHeight : function(){
        return Ext.getBody().getHeight() ;
    },
    /**
     *
     * @param str
     * @param length
     * @returns {string}
     */
    subStrFormate : function (str ,length) {
        if(str+''.length > length){
            return "<div title='"+v+"'>"+str.substring(0,length -1 ) + "...</div>"
        }
        return   "<div title='"+v+"'>"+str +"</div>";
    },
    isContains : function(str, substr) {
        var b = str.indexOf(substr) >= 0;
        //alert(str + " <" + substr + "  " + b)
        return b;
    },
    
    /**
     * 状态值是否可用
     * @param status
     * @returns {boolean}
     */
    statusAble  : function(status){
        return status == 1 || status =='Y' || status==true || status =='OK' || status == 'SUCCESS' || status == 'YES';
    },

    /**
     * ExtAjax 返回对象提取字符串，并转换Json
     * @param res
     */
    responseToJson : function(res){
        if(undefined == res.responseText){
            try{
                return eval("("+res+")") ;
            }catch (e){
            }
        }
        var json =  eval("("+res.responseText+")");
        return json ;
    },
    /**
     * 延迟执行
     * @param fn
     * @param num  默认值 1500 毫秒！
     */
    dealy : function(fn,num){
        if(null == num) num = 1500
        var task = new Ext.util.DelayedTask(fn);
        task.delay( num )
    } ,
    /**
     * alert 信息
     * @param msg  信息内容
     * @param autoHide 是否自动隐藏。默认 true
     */
    info: function(msg,autoHide){
        if(autoHide ==null) {
            autoHide = true;
        }
        
        Ext.MessageBox.alert('提示',msg);
        if(autoHide==true){
            EXT_COMMON.dealy(function(){
                Ext.MessageBox.hide();
            } , 1500) ;
        }
    } ,
    confirm: function(msg ,fnYes){
        Ext.Msg.confirm("警告",msg,function(v){
            if( v =='yes'){
                fnYes;
            }else {
                //alert("no")
                return false;
            }
        });
    },

    wait : function(msg,timeout){
        if( !timeout){
            timeout = 15000 ;
        }
        if(msg){

        }else {
            msg = "努力加载中，拜托稍等一下..." ;
        }
        Ext.Msg.wait(msg,"稍等..." );
        EXT_COMMON.dealy(function(){
            Ext.MessageBox.hide();

        },timeout);

    },

    /**
     * 锁定屏幕
     * @param info
     */
    mask:function(info){
        Ext.getBody().mask(info);
    },
    /**
     * 解锁屏幕
     */
    unMask: function(){
      Ext.getBody().unmask();
    },

    /**
     * 操作 控制 超链接
     * @returns {string}
     */
    operationHref : function (text ,title,url ,params ) {
        var href = "<a color=\"blue\" style=\"cursor:pointer;color:darkblue\" title='"+title+"' onclick='EXT_COMMON.viewLink(\""+title+"\",\""+url+"\",\""+params+"\")'>" + text + "</a>";
        return href;
    },

    /**
     * 渲染操作 选项按钮...
     * @param operationHrefArrar operationHref的数组
     * @returns {string}
     */
    operationHrefs : function (operationHrefArrar) {
        var str = "";
        for(var i = 0 ; i < operationHrefArrar.length;i++){
            str += operationHrefArrar[i] + "&nbsp;";
        }
        return str ;
    },

    /**
     * 按钮操作事件,可以执行query,reset,等
     * @param operationName 操作名称
     * @param iconStyle 按钮样式
     * @param operationFun 操作事件
     * @returns {Ext.button.Button}
     */
    operationBtn : function(operationName, iconStyle , operationFun){
        return new Ext.button.Button({
            text:operationName,icon:iconStyle,
            handler:operationFun
        });
    },

    /**
     *  日期渲染！
     *   2018-2-9：
     *             从显示到日期，title显示到秒
     *             调整为 显示到秒，title 不变
     * @param v
     * @param grid
     * @param record
     * @returns {*}
     * @constructor
     */
    gridRenderDate: function(v , grid, record ){
        try{
            if(v == null || v <10000 ){
                return '---'
            }
            var dateFormateToDay ="";
            var dateFormateToSecond = "";
            var tmpDate = new Date( v   );
            var now = new Date();
            var str = "";
            var y = tmpDate.getFullYear()   ;

            str = y + "/" ;
            var m = tmpDate.getMonth() +1 ;
            if(m<10){
                str += "0";
            }
            str += "" + m +"/";
            var d = tmpDate.getDate() ;
            if(d<10){
                str+="0";
            }
            str+= d +" ";
            dateFormateToDay = str ;
            var h = tmpDate.getHours();
            if(h<10){
                str+="0";
            }
            str += h+":";
            var min = tmpDate.getMinutes() ;
            if(min<10){
                str+="0";
            }
            str += min +":";
            var s = tmpDate.getSeconds();
            if(s<10){
                str+="0";
            }
            str +=s;
            dateFormateToSecond = str ;

            return '<span title="'+dateFormateToSecond+'">' + dateFormateToSecond + '</span>';

            return str ;

        }catch (e){

            return v +"---";
        }
        return v ;

    },

    /**
     * 支付类型
     * @param v
     * @param grid
     * @param recode
     * @returns {*}
     */
    gridRenderByPayType: function (v, grid, recode) {
        try{
            if(v=='wxpay'){
                return '微信支付';
            }else if(v=='alipay'){
                return '支付宝支付';
            }else if(v=='otherpay'){
                return '其他支付';
            }else {
                return '';
            }
        }catch (e){

        }
    },

    /**
     *  if可用的渲染 ,通过record中的status值来动态渲染
     *  record.data.status = 1  渲染成不可用
     *  record.data.status = 0  渲染成不可用
     * @param v
     * @param grid
     * @param record
     */
    gridRenderByAble: function(v, grid, record){
        try{
            if( EXT_COMMON.statusAble( record.data.status )){
                return  v;
            }else{
                return '<del>' + v + '</del>' ;
            }
        }catch (e){
            //alert(e)
            return v ;
        }
    },
    /**
     *   if 可用的渲染 ,通过record中的status值来动态渲染
     *   true 渲染成 green
     *   false 渲染成 #ccc +删除线
     * @param v
     * @param grid
     * @param record
     * @returns {*}
     * @constructor
     */
    gridRenderByAbleWithColor: function(v, grid, record){
        try{
            if( EXT_COMMON.statusAble( v )){
                return  '<font color=green>正常</font>';
            }else{
                return '<font color="#888" >禁用</font>' ;
            }
        }catch (e){
            console.exception(e)
            return v ;
        }
    } ,
    /**
     * 图片是否存在！
     * @param v
     * @returns {string}
     * @constructor
     */
    gridRenderImgExists : function(v){
        v = v + ''
        if(v.length < 15) {
            return "<center><img src='"+ICON_NO+"' style='width:14px;' /></center>";
        }
        else{
            return  "<a target='_blank' href='"+v+"'><center><img src='"+ICON_OK+"' style='width:14px;' /></center></a>"
        }
    },
    /**
     * 针对Status 栏的渲染
     * @param statusValue
     * @returns {string}
     * @constructor
     */
    gridRenderStatus    :   function(statusValue){
        try{
            if( EXT_COMMON.statusAble( statusValue) ){
                return "<span style='color: green'>是</span>"
            }  else{
                return "<span style='color: red'>否</span>"
            }
        }catch (e){
            return "-"
        }
    },
    gridRenderWithTitle : function(v){
        return '<a title="'+v+'">' + v + '</a>'
    } ,

    gridRenderGreyWithTitle : function (v) {
        return '<a title="'+v+'" style="color: grey">' + v + '</a>';
    },
    /**
     * 是 否 的渲染
     * @param v
     * @returns {*}
     * @constructor
     */
    gridRenderWithYesOrNo_ZHCN  :   function(v){
        if( EXT_COMMON.statusAble( v)){
            return "是" ;
        }else{
            return "否";
        }
    },
    /**
     * 性别的渲染,数据库存的是m,w
     * @param sex
     * @returns {*}
     * @constructor
     */
    gridRenderWithSex : function(sex){
        if(sex=='M' || sex == 'm' || sex == '男'){
            return '男';
        }else if(sex == 'F' || sex =='f' || sex == '女'){
            return '女';
        }else{
            return "未知"
        }
    },
    /**
     * 用户中心视图展示的关联用户信息的渲染
     * @param userAccountId
     * @returns {*}
     * @constructor
     */
    gridRelevancyAccountUserId:function (userAccountId) {
        if(userAccountId==null  || userAccountId == undefined || userAccountId.trim() == ''){
            return '云平台已删除';
        }else{
            return userAccountId;
        }
    },
    /**
     * 表格 价格显示渲染
     * @param v
     * @returns {*}
     * @constructor
     */
    gridRenderPrice :   function(v ){
            return  '<div  style="width: 100%;text-align: right;">'+toDecimal2(v) +'</div>';

    } ,

    /**
     * 用户更新人渲染
     * @param v
     * @returns {string}
     */
    gridRenderUserVersionUpdatePerson : function(v){
        if(v == undefined || v == "null"){
            return "系统";
        }
        return  v;
    },
    /**
     *
     * @param v
     * @returns {*}
     * @constructor
     */
    gridRenderPriceWithColorGreen:   function(v ){

      return "<div style='width: 100%;text-align: right; color:green'>"  + toDecimal2(v)+ "</div>" ;

    } ,
    /**
     *
     * @param v
     * @returns {*}
     * @constructor
     */
    gridRenderPriceWithColorRed:   function(v ){
            return "<div  style='width: 100%;text-align: right;color:red'>"  +toDecimal2(v)+ "</div>" ;

    } ,
    /**
     *
     * @param v
     * @returns {*}
     * @constructor
     */
    gridRenderPriceWithColorGray:   function(v ){
            return "<div style='width: 100%;text-align: right; color:gray'>"  + toDecimal2(v)+ "</div>" ;

    } ,


    gridRenderBlue : function(v){
        return  "<div style='width: 100%; color:blue;font-weight: bold'>"  + v+ "</div>" ;
    },

    gridRenderRed : function(v){
        return  "<div style='width: 100%; color:red;font-weight: bold'>"  + v+ "</div>" ;
    },


    gridRenderGreen : function(v){
        return  "<div style='width: 100%; color:green;font-weight: bold'>"  + v+ "</div>" ;
    },

    gridRenderGrey : function(v){
        return  "<div style='width: 100%; color:grey;font-weight: bold'>"  + v+ "</div>" ;
    },
    gridRenderPink : function(v){
        return  "<div style='width: 100%; color:pink;font-weight: bold'>"  + v+ "</div>" ;
    },

    gridRenderOrange : function(v){
        return  "<div style='width: 100%; color:orange;font-weight: bold'>"  + v+ "</div>" ;
    },
    gridRenderMoney : function(v){
        if(v == null ){
            v = 0.0;
        }
        var value = (parseFloat(v) /100 )  ;
        var vs = value + '';
        if( !( vs.indexOf('.')>0)){
            vs = vs +'.00';
        }
        return EXT_COMMON.gridRenderPrice( vs);
    },

    gridRenderMoneyColorGreen : function(v){

        return  '<span style="color:green">'+EXT_COMMON.gridRenderMoney( v) +'</span>';
    },
    gridRenderMoneyColorRed : function(v){

        return  '<span style="color:red">'+EXT_COMMON.gridRenderMoney( v) +'</span>';
    },


    //>> --------textfield and combox ----- //
    /**
     * 搜索组件-text
     * @param emptyText
     * @param fieldLabel
     * @returns {Ext.form.field.Text}
     */
    search_text_field : function(emptyText,fieldLabel){
        var field = new Ext.form.field.Text({
            emptyText:emptyText,fieldLabel:fieldLabel,labelWidth:65,labelAlign:'right'
        });
        return field;
    },
    /**
     * 筛选搜索组件 -number
     * @param emptyText
     * @param fieldLabel
     * @returns {Ext.form.field.Text}
     */
    search_number_field :function(emptyText,fieldLabel){
        var field = new Ext.form.field.Number({
            emptyText:emptyText,fieldLabel:fieldLabel,labelWidth:65,labelAlign:'right'
        });
        return field;
    },
    /**
     * 筛选搜索组件 - combox
     * @param emptyText
     * @param fieldLabel
     */
    search_status_combox : function(emptyText,fieldLabel){
        var field = new Ext.form.ComboBox({
            emptyText:emptyText ,typeAhead:true,fieldLabel:fieldLabel,
            displayField:'name', valueField :'id',name:'workorderPayStatus',
            labelWidth:65,labelAlign:'right',width:170,
            store:new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data : [[' ','全部'],['N','否'],['Y','是']]
            }),editable:false
        }) ;
        return field;
    },

    /**
     * 筛选组件-可配置的combox
     * @param emptyText
     * @param fieldLabel
     * @param YText
     * @param NText
     */
    search_status_config_combox : function(emptyText,fieldLabel,YText,NText){
        var field = new Ext.form.ComboBox({
            emptyText:emptyText ,typeAhead:true,fieldLabel:fieldLabel,
            displayField:'name', valueField :'id',name:'workorderPayStatus',
            labelWidth:65,labelAlign:'right',width:170,
            store:new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data : [[' ','全部'],['N',NText],['Y',YText]]
            }),editable:false
        }) ;
        return field;
    },

    /**
     * 筛选组件 - 可配置的combox,支持多个不同的事件,事件需要封装成json对象
     * @param emptyText
     * @param fieldLabel
     * @param store
     * @param name
     * @param displayField
     * @param valueField
     * @param listenerFuncs 事件对象组
     */
    search_config_combox : function (emptyText, fieldLabel, store, name, displayField, valueField, listenerFuncs) {
        var field =  new Ext.form.ComboBox({
            emptyText:emptyText ,typeAhead:true,fieldLabel:fieldLabel,
            displayField:displayField, valueField :valueField,name:name,
            labelWidth:65,labelAlign:'right',width:170,
            store:store,editable:false,listeners : listenerFuncs
        }) ;
        return field;
    },

    /**
     * 筛选组件 - 可配置的combox,支持单一的change事件
     * @param emptyText
     * @param fieldLabel
     * @param store
     * @param displayField
     * @param valueField
     * @param changeFunc
     */
    search_config_combox_changeFunc : function (emptyText, fieldLabel, store, name, displayField, valueField, changeFunc) {
        return this.search_config_combox(emptyText,fieldLabel,store,name,displayField,valueField,{'change' : changeFunc});
    },

    /**
     * 日期时间控件
     * 如果没有选择任何时间,返回的是个object,  field.getValue() 返回null
     * 如果选择了时间,返回的是个UTO时间中文字符(例如:Mon Jun 04 2018 09:55:27 GMT+0800 (中国标准时间)),建议取值时,getTime()
     * @param emptyText
     * @param fieldLabel
     */
    search_config_dateTimeField : function (emptyText, fieldLabel, eventName, eventFunc) {
        var field = Ext.create({
            xclass:'Ext.ux.DateTimeField',
            fieldLabel:fieldLabel,
            labelWidth:65,
            emptyText:emptyText,
            xtype:'datetimefield',
            /*reference:'dateTimeText',*/
            format:"Y-m-d H:i:s"
        });
        if(eventName != undefined && eventName != '' && eventName.length > 0){
            field.addListener(eventName,eventFunc);
        }
        return field;
    },
    search_dateTimeField : function (emptyText, fieldLabel) {
        return this.search_config_dateTimeField(emptyText,fieldLabel,"",function () {
            
        });
    },

    search_dateField : function (emptyText,fieldLabel) {
      var field = new Ext.form.Date({
          fieldLabel : '开始日期',
          emptyText : '请选择',
          disabledDays : [1, 2, 5],//将星期一,二,五禁止.数值为0-6,0为星期日,6为星期六
          labelWidth : 100,
          readOnly : true,
          allowBlank : false,
          format : 'Y-m-d'

      });
      return field;
    },




    /**
     *
     * 授权检查 ，检查是否允许访问该请求
     * @param url
     * @constructor
     */
    AuthorizationCheck : function(url){
        var start = new Date().getTime();
        // 暂时放过权限校验, 带权限中心功能完善后增加！
        return ;
        Ext.Ajax.request({
            url:'/sysAuthorizationCheck',
            params:{
                url : url
            },success:function(res){
                var end = new Date().getTime();
                var json = EXT_COMMON.responseToJson( res);
                if(json.info  ){

                }else{
                    Ext.Msg.alert("访问受限","您未获得该操作的授权！详情：" + url );
                }
                if( end - start > 3500){
                    if(Ext.isReady ){
                        if(end -start > 15000){
                            Ext.Msg.alert("网络奔溃了", "网络应该已经奔溃了<br/>测试数据：单次请求响应时间:" + (end-start) + " MS");
                        }else if(end -start > 10000){
                            Ext.Msg.alert("网络几乎是奔溃的", "网络几乎慢的难以忍受了，你不会是在使用2G网络吧？<br/>测试数据：单次请求响应时间:" + (end-start) + " MS");
                        }else if(end -start > 6000){
                            Ext.Msg.alert("网络连接迟缓", "检测到网络连接速度迟缓，网络丢包率非常高，建议您检查所在网络是否稳定,谨慎操作，切勿疯狂点击！<br/>测试数据：单次请求响应时间:" + (end-start) + " MS");
                        }else{
                            Ext.Msg.alert("网络连接慢", "检测到网络连接速度迟缓，建议您检查所在网络是否稳定！<br/>测试数据：单次请求响应时间:" + (end-start) + "MS ");
                        }
                    }
                }
                //alert(json.data.info)
            }
        }) ;
    }






}




/**
 * 管理员权限授权预检查
 */
Ext.Ajax.addListener("beforerequest",
    function(conn, options, eOpts ){

        if(options){
            var url = options.url;
            //放过 检查的 请求...
            if( url.indexOf("sysAuthorizationCheck") >-1){
                return ;
            }else{
                EXT_COMMON.AuthorizationCheck( url);
            }

        }
    },
    this
);

function loadURL( url ,target){
    document.getElementById( target).src ="/resources/html/sys/loading.html";
    setTimeout( function () {
        document.getElementById( target).src = url;
    }, 300)
}

function checkRight( id ){

    if(id == 'rolemanager'){
        Ext.Msg.alert("访问受限","您的访问受限！");
        return false;
    }else{

    }
    return true;
}

/**
 * 强制保留2位小数
 * @param x
 * @returns {*}
 */
function toDecimal2(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return false;
    }
    var f = Math.round(x*100)/100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + 2) {
        s += '0';
    }
    return s;
}


/*
Ext.define('Ext.form.field.Money', {
    extend:'Ext.form.field.Number',
    //alias: 'money',
    alternateClassName: ['Ext.form.NumberField', 'Ext.form.Number'],
    allowExponential: true,
    allowDecimals : true,

    decimalSeparator : null,
    submitLocaleSeparator: true,
    decimalPrecision : 2,
    minValue: Number.NEGATIVE_INFINITY,
    maxValue: Number.MAX_VALUE,
    step: 1,
    minText : 'The minimum value for this field is {0}',
    maxText : 'The maximum value for this field is {0}',
    nanText : '{0} is not a valid number',
    negativeText : 'The value cannot be negative',
    baseChars : '0123456789',
    autoStripChars: false,

    initComponent: function() {
        var me = this;
        if (me.decimalSeparator === null) {
            me.decimalSeparator = Ext.util.Format.decimalSeparator;
        }
        me.callParent();

        me.setMinValue(me.minValue);
        me.setMaxValue(me.maxValue);
    },



    setValue: function(value) {
        try{
            value = value/100 ;
        }catch (e){
            value = 0.00;
        }
        var me = this,
            inputEl = me.inputEl;

        if (inputEl && me.emptyText && !Ext.isEmpty(value)) {
            inputEl.removeCls(me.emptyUICls);
            me.valueContainsPlaceholder = false;
        }

        me.callParent(arguments);

        me.applyEmptyText();
        return me;
    }
});
*/

function alert(e){
    console.info( "alert print >> " + e)
}
//alert("123")