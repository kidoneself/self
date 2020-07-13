




//方法注册中心加载
document.write('<script type="text/javascript"src="/resources/javascript/sys/config/reg.js"></script>') ;
var defaultPageSize = 25 ;
var tabs = new Ext.TabPanel({
    region: 'center',closable:false,tbar:[],autoScroll: true,
    activeTab: 0,      // First tab active by default
    items: {
        closable:true,
        title: '系统概况',
        html: '<iframe frameborder="no" style="width: 100%;height:98%;" src="http://www.0574123.com/" />'
    }
}) ;

Ext.onReady(function(){
    /**
     * 用户管理！
     * @type {Ext.tree.Panel}
     */
    var userTree = new Ext.tree.Panel({
        rootVisible : false , title:'用户管理',text:'系统管理',
        root:{
             children:  []
        },
        listeners:{
            itemclick : function(a ,b){  callFucnction(b.id, b.data.text)  }
        }
    });

    /**
     * 系统管理
     * @type {Ext.tree.Panel}
     */
    var sysTree = new Ext.tree.Panel({
        rootVisible : false ,  title:'系统管理',text:'系统管理',
        root:{
            children:  []
        },
        listeners:{
            itemclick : function(a ,b){ callFucnction(b.id, b.data.text) }
        }
    }) ;


    /**
     *  布局 菜单导航区域
     * @type {Ext.Panel}
     */
    var accordionPanel =new Ext.Panel({
        frame: true,region: 'west',width:200,  layout: "accordion", collapsible: true,
        title:'导航菜单',   layoutConfig: {  animate: true },
        items:[
            //此处添加相应的模块 树（导航分类）
            userTree,
            sysTree
        ]

    });



    var view = new Ext.container.Viewport({
        layout: 'border',
        items: [
            {
                region:'north',xtype:'container' ,frame:true,height:38 ,
                html:'' +
                '<span style="float: right; margin-top: 10px;font-size: 14px;color:#fff; margin-right: 10px;">' +
                '   <a style="color: #fff;" href="javascript:helpSearch()">查询帮助</a>&nbsp;&nbsp;' +
                '   <a style="color: #fff; " href="javascript:window.location.reload()">刷新页面</a>&nbsp;&nbsp;' +
                '   <a style="color: #fff;font-weight: bold;" href="javascript:logout()">退出登录</a>&nbsp;&nbsp;' +
                '</span>' +
                '<img src="/resources/images/sys/shop/dxstyle_logo_w250_h150.png" style="height: 36px;float: left overflow: hidden; float:left;margin-left: 10px;">' +
                '<h1 style="color: #fff;font-size: 18px; "> &nbsp;&nbsp;大象生活——综合管理智能数据中心</h1>' +
                '<span></span> ' +
                '' +
                '' +
                ''
            },
            accordionPanel, tabs
        ]
    });


});



var w = new Ext.Window({
    id:'ilovethisgame_0574123',
    closable: true,
    closeAction: 'hide',
    maximizable: true,
    animateTarget: this,
    minWidth: 350,
    title:'系统查询帮助',
    html:'亲爱的管理员大大:<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您辛苦了！' +
    '非常感谢您使用大象商城管理端，您查看一遍该帮助将帮助您解决将来工作中遇到的很多查询问题。<br/>' +
    '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;管理端为了提高查询的灵活性，特地开放了更高的权限给大家，但是也需要一些小技巧去掌握哦！<br/>' +
    '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;比如： 您需要查询内容为：<font color="red">你吃饭了吗？</font> 这么一句话! 您可以从中取得任意字 和 %的组合 来查询。<br/>' +
    '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; % :表示任意内容 ，也包含空。<br/> ' +
    '如果不出现%, 表示需要完全匹配的查询， 即您需要输入 :你吃饭了吗？  才能查到结果！<br/>' +
    '开始字符如果为 “你” ，那么将查询 “你”字开始符合查询条件的结果 <br/>' +
    '如果开始字符为 % ,那么表示 开始可以是空，或者任意字符 ，+ 后面符合条件的结果 <br/>' +
    '结束字符也同样道理哦！  % 表示匹配任意！  其他字符，表示 严格匹配 ！' +
    '',
    width:500,
    height:450,closeAction:'hide',animCollapse:true
});
/**
 * 查询帮助
 */
function helpSearch(){
    w.show(this)
}
function logout(){
    Ext.Msg.confirm('确定吗','您确定退出系统吗？')
}


function warnMsg(title , msg){
    Ext.Msg.show({
        title:title,
        message:msg ,
        icon:Ext.Msg.WARNING,
        animateTarget:Ext.getBody()
    });

    function hideWarn(){
        Ext.Msg.hide();
    }
    setTimeout( hideWarn , 2000)
}

