var index_tabs;
var layout_west_tree;
var indexTabsMenu;
$(function() {
    $('#index_layout').layout({fit : true});
    index_tabs = $('#index_tabs').tabs({
        fit : true,
        border : false,
        onContextMenu : function(e, title) {
            e.preventDefault();
            indexTabsMenu.menu('show', {
                left : e.pageX,
                top : e.pageY
            }).data('tabTitle', title);
        },
        tools : [{
            iconCls : 'fi-home',
            handler : function() {
                index_tabs.tabs('select', 0);
            }
        }, {
            iconCls : 'fi-loop',
            handler : function() {
                refreshTab();
            }
        }, {
            iconCls : 'fi-x',
            handler : function() {
                var index = index_tabs.tabs('getTabIndex', index_tabs.tabs('getSelected'));
                var tab = index_tabs.tabs('getTab', index);
                if (tab.panel('options').closable) {
                    index_tabs.tabs('close', index);
                }
            }
        }]
    });
    // 选项卡菜单
    indexTabsMenu = $('#tabsMenu').menu({
        onClick : function(item) {
            var curTabTitle = $(this).data('tabTitle');
            var type = $(item.target).attr('type');
            if (type === 'refresh') {
                refreshTab();
                return;
            }
            if (type === 'close') {
                var t = index_tabs.tabs('getTab', curTabTitle);
                if (t.panel('options').closable) {
                    index_tabs.tabs('close', curTabTitle);
                }
                return;
            }
            var allTabs = index_tabs.tabs('tabs');
            var closeTabsTitle = [];
            $.each(allTabs, function() {
                var opt = $(this).panel('options');
                if (opt.closable && opt.title != curTabTitle
                        && type === 'closeOther') {
                    closeTabsTitle.push(opt.title);
                } else if (opt.closable && type === 'closeAll') {
                    closeTabsTitle.push(opt.title);
                }
            });
            for ( var i = 0; i < closeTabsTitle.length; i++) {
                index_tabs.tabs('close', closeTabsTitle[i]);
            }
        }
    });
    
    layout_west_tree = $('#layout_west_tree').tree({
        url : basePath+'/sysResource/tree',
        parentField : 'pid',
        lines : true,
        onClick : function(node) {
            var opts = {
                title : node.text,
                border : false,
                closable : true,
                fit : true,
                iconCls : node.iconCls
            };
            var url = node.attributes;
            if (url && url.indexOf("http") == -1) {
                url = basePath + url;
            }
            node.openMode='iframe';
            if(url != ""){
                if (node.openMode == 'iframe') {
                    opts.content = '<iframe src="' + url + '" frameborder="0" style="border:0;width:100%;height:99.5%;"></iframe>';
                    addTab(opts);
                } else if (url) {
                    opts.href = url;
                    addTab(opts);
                }
            }
        }
    });
    //document.getElementById('main').innerHTML="欢迎使用工作流管理平台"
	// 基于准备好的dom，初始化echarts实例
	// var myChart = echarts.init(document.getElementById('main'));
	// var dataAll = [389, 259, 262, 324, 232, 176, 196, 214, 133, 370];
	// var yAxisData = ['原因1','原因2','原因3','原因4','原因5','原因6','原因7','原因8','原因9','原因10'];
	// var option = {
	//     //backgroundColor: '#0f375f',
	//     title:[
	//         {text:"各渠道投诉占比",x: '2%', y: '1%',textStyle:{color:"#696969",fontSize:"14"}},
	//         {text:"投诉原因TOP10",x: '40%', y: '1%',textStyle:{color:"#696969",fontSize:"14"}},
	//         {text:"各级别投诉占比",x: '2%', y: '50%',textStyle:{color:"#696969",fontSize:"14"}},
	//     ],
	//     grid: [
	//         {x: '45%', y: '7%', width: '40%', height: '77%'},
	//     ],
	//     tooltip: {
	//         formatter: '{b} ({c})'
	//     },
	//     xAxis: [
	//         {gridIndex: 0, axisTick: {show:false},axisLabel: {show:false},splitLine: {show:false},axisLine: {show:false }},
	//     ],
	//     yAxis: [
	//          {  gridIndex: 0, interval:0,data:yAxisData.reverse(),
	//             axisTick: {show:false}, axisLabel: {show:true},splitLine: {show:false},
	//             axisLine: {show:true,lineStyle:{color:"#86c9f4"}},
	//         }
	//     ],
	//     series: [
	//         {
	//             name: '各渠道投诉占比',
	//             type: 'pie',
	//             radius : '27%',
	//             center: ['20%', '25%'],
	//             color:['#86c9f4','#4da8ec','#3a91d2','#005fa6','#315f97'],
	//             data:[
	//                 {value:335, name:'客服电话'},
	//                 {value:310, name:'奥迪官网'},
	//                 {value:234, name:'媒体曝光'},
	//                 {value:135, name:'质检总局'},
	//                 {value:105, name:'其他'},
	//             ],
	//             labelLine:{normal:{show:false}},
	//             itemStyle: {normal: {label:{ show: true,  formatter: '{b} \n ({d}%)', textStyle:{color:'#86c9f4'}} },},
	//         },
	//         {
	//             name: '各级别投诉占比',
	//             type: 'pie',
	//             radius : '27%',
	//             center: ['20%', '65%'],
	//             color:['#86c9f4','#4da8ec','#3a91d2','#005fa6','#315f97'],
	//             labelLine:{normal:{show:false}},
	//             data:[
	//                 {value:335, name:'A级'},
	//                 {value:310, name:'B级'},
	//                 {value:234, name:'C级'},
	//                 {value:135, name:'D级'},
	//             ],
	//             itemStyle: {normal: {label:{ show: true,  formatter: '{b} \n ({d}%)', textStyle:{color:'#86c9f4'}} },},
	//         },
	//         {
	//             name: '投诉原因TOP10',
	//             center: ['40%', '50%'],
	//             type: 'bar',xAxisIndex: 0,yAxisIndex: 0,barWidth:'45%',
	//             itemStyle:{normal:{color:'#86c9f4'}},
	//             label:{normal:{show:true, position:"right",textStyle:{color:"#696969"}}},
	//             data: dataAll.sort(),
	//         },
	//     ]
	// };
	// // 使用刚指定的配置项和数据显示图表。
	// myChart.setOption(option);
    //
});

function addTab(opts) {
    var t = $('#index_tabs');
    if (t.tabs('exists', opts.title)) {
        t.tabs('select', opts.title);
    } else {
        t.tabs('add', opts);
    }
}

function refreshTab() {
    var index = index_tabs.tabs('getTabIndex', index_tabs.tabs('getSelected'));
    var tab = index_tabs.tabs('getTab', index);
    var options = tab.panel('options');
    if (options.content) {
        index_tabs.tabs('update', {
            tab: tab,
            options: {
                content: options.content
            }
        });
    } else {
        tab.panel('refresh', options.href);
    }
}

function logout(){
    $.messager.confirm('提示','确定要退出?',function(r){
        if (r){
            progressLoad();
            $.post(basePath+'/logout', function(result) {
                if(result.success){
                    progressClose();
                    window.location.href=basePath+'/';
                }
            }, 'json');
        }
    });
}

function editUserPwd() {
    parent.$.modalDialog({
        title : '修改密码',
        width : 300,
        height : 250,
        href : basePath+'/sysUser/editPwdPage',
        buttons : [ {
            text : '确定',
            handler : function() {
                var f = parent.$.modalDialog.handler.find('#editUserPwdForm');
                f.submit();
            }
        } ]
    });
}


