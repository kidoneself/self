后台主要基于ExtJs 5 实现，这边随便记录一些东西，或者注意事项，可能记录的不全，不定期记录

1.
    在提交form的时候，SpringMVC的Date 格式不会接受Date值为NULL的对象，如果提交了Date为null的参数，会不报错也不处理，直接抛给游览器一个400 bad request 错误。
    所以，如果有Date的参数，一定要检查初始化。 一些比如createTime,updateTime的date类型，前台除了展示，全部交给后台服务去赋值吧！

2.  store的参数绑定跟ExtJs 3.X时代有了很大的变化，但是其实是更加规范的。参考如下：
    Exp:
        var store = new Ext.data.JsonStore({
                proxy: {
                    type: 'ajax', url: basePath + 'list', reader: { type: 'json', rootProperty: 'content' ,  totalProperty:'totalElements' }, autoLoad: true ,autoDestroy:true
                },
                fields: [ 'id'  ],
                listeners:{
                    'load'  :   function(store, op, options){
                        //这个是载入后事件， 这边主要是刷新 grid 视图 。 Ext有grid数据和标题错位的问题。 刷新视图可以解决问题
                        grid.getView().refreshView();
                    },
                    'beforeload'    :   function (store,op,options) {
                        // 绑定 常用参数示例 ，这个参数在每次 load之前都会加载 。 参数用公共变量，或者直接getValue
                        var params = {
                            status : showStatus
                        };
                        Ext.apply(store.proxy.extraParams ,params) ;
                    }
                }
            });


