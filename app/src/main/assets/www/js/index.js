var BRIDGE = "BLIftttBridge";

var mProfileIntfs = null;
var mStringIntfs = null;
var TrendType = 4;
var keyG;
var objG;
var stringG;

//var mProfileIntfs = ({"power": [{"act": 3, "idx": 1, "in": [1, 0, 1]}], "temp": [{"act": 3, "idx": 1, "in": [2, 16, 32, 1, 1]}]});
//var mStringIntfs = ({"power": {"name": "空净开关", "values": {"0": "关", "1": "开"}}, "temp": {"name": "温度", "values": {"0": "1度", "1": "2度"}}});

var funcContent = document.querySelector(".funcContent");
var dialogBg = document.querySelector(".dialogBg");
var dialogContent = dialogBg.querySelector(".dialogContent");
var childBox  = document.querySelector('.child-box');
var selectContainer = document.querySelector('.selectContainer');
var selectContainer2 = document.querySelector('.selectContainer2');
var dialogCancel = document.querySelector('.dialogCancel');
var child1 = document.querySelector('#child1');
var child2 = document.querySelector('#child2');
var child3 = document.querySelector('#child3');
var child4 = document.querySelector('#child4');
var child5 = document.querySelector('#child5');
var child6 = document.querySelector('#child6');
dialogBg.querySelector(".dialogCancel").onclick = function(){
    dialogBg.style.display = "none";
};
var flagShow;
var flagType;

var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        cordova.exec(getProfileSucc, null, BRIDGE, 'getProfile', []);
        cordova.exec(getStringSucc, null, BRIDGE, 'getString', []);
        //判断是否显示“大于”“小于”“变为”
        //cordova.exec(getVisibleSucc,null,BRIDGE,'getvisible',[]);
    }
};

/**
 * 查询到profile之后保存到内存
 * @param profile
 */
//function getTrendType(num){
//    TrendType = num;
//    childBox.setAttribute('class','child-box');
//    this.setAttribute('class','child-box selected');
//}
function getVisibleSucc(flag){
    flag = JSON.parse(flag);
    flagShow = flag.visiable;
    flagType = flag.type;
    if(flagShow==1){
        showDialogHideShow(flagType);
    }else{
        showDialogN(flagType);
    }

}
child1.onclick = function () {
    //alert('444');
    TrendType = 4;
    child2.setAttribute('class','child-box');
    child3.setAttribute('class','child-box');
    child1.setAttribute('class','child-box selected');
};

child2.onclick = function () {
    //alert('222');
    TrendType = 2;
    child1.setAttribute('class','child-box');
    child3.setAttribute('class','child-box');
    child2.setAttribute('class','child-box selected');
};
child3.onclick = function () {
    //alert('333');
    TrendType = 3;
    child1.setAttribute('class','child-box');
    child2.setAttribute('class','child-box');
    child3.setAttribute('class','child-box selected');
};
child4.onclick = function () {
    //alert('333');
    TrendType = 4;
    child5.setAttribute('class','child-box');
    child6.setAttribute('class','child-box');
    child4.setAttribute('class','child-box selected');
};
child5.onclick = function () {
    //alert('333');
    TrendType = 0;
    child6.setAttribute('class','child-box');
    child4.setAttribute('class','child-box');
    child5.setAttribute('class','child-box selected');
};
child6.onclick = function () {
    //alert('333');
    TrendType = 1;
    child4.setAttribute('class','child-box');
    child5.setAttribute('class','child-box');
    child6.setAttribute('class','child-box selected');
};
function getProfileSucc(profile){
    mProfileIntfs = eval("(" + profile + ")").suids[0].intfs;
    initView();
}

/**
 * 查询到profile描述之后保存到内存
 * @param string
 */
function getStringSucc(string){
    mStringIntfs = eval("(" + string + ")").intfs;
    initView();
}
function getVS(params,flagType){
    if(params){
        if(flagType==1){
            selectContainer.style.display ="block";
            selectContainer2.style.display ="none";
            selectContainer.style.height = "15%";
            dialogCancel.style.height = '15%';
            dialogContent.style.height = '70%';
        }else{
            selectContainer2.style.display ="block";
            selectContainer.style.display ="none";
            selectContainer2.style.height = "15%";
            dialogCancel.style.height = '15%';
            dialogContent.style.height = '70%';
        }


    }else{
        selectContainer.style.display ="none";
        selectContainer2.style.display ="none";
        dialogContent.style.height = '80%';
        dialogCancel.style.height = '20%';
    }
}
/**
 * 根据取得的profile初始化界面
 */
function initView(){
    if(mProfileIntfs != null && mStringIntfs != null){
        funcContent.innerHTML = "";
        for(var key in mProfileIntfs){
            //console.log(mProfileIntfs[intf]);
            var obj = mProfileIntfs[key][0];

            // 属性可写的话
            if(obj.act == 2 || obj.act == 3|| obj.act == 1){
                var value = obj.in[1];
                var valueName;

                console.log();
                if(obj.in[0] == 1){
                    // 枚举需要读值
                    valueName = mStringIntfs[key].values[value];
                }else{
                    valueName = value;
                }

                cordova.exec(null, null, BRIDGE, 'setinitValue', eval("[{" + key + ": " + value + "}]"));

                var html = '<div class="funcUnit" name="' + key + '"> <div class="funcLabel">' + mStringIntfs[key].name + '</div> <div class="funcValue" value="' + value + '">' + valueName + '</div> </div>';
                funcContent.innerHTML += html;
            }
        }

        // 注册点击事件
        var views = funcContent.querySelectorAll(".funcUnit");
        for(i in views) {
            views[i].onclick = showDialog;
        }
    }
}

/**
 * 根据不同的属性显示不同的选择框
 */
function showDialogHideShow(flagType){
    if(objG.in[0] == 1){
        getVS(0,flagType);
        for(i in stringG.values){
            dialogContent.innerHTML += '<div class="dialogUnit" value="' + i + '">' + stringG.values[i] + '</div>';
        }
    }else{
        //(并显示大于、小于、变为)
        getVS(1,flagType);

        var min = objG.in[1];
        var max = objG.in[2];
        var step = objG.in[3];
        var multiple = objG.in[4];
//        if(max>=900){
//            step = step*100;
//        }
        for(var i = min; i <= max; i += step){
            dialogContent.innerHTML += '<div class="dialogUnit" value="' + i + '">' + i / multiple + '</div>';
        }
    }

    // 注册点击事件
    var views = dialogContent.querySelectorAll(".dialogUnit");
    for(i in views) {
        views[i].onclick = function(){
//        if(objG.in[0] == 1){
            selected(keyG, this.innerHTML, this.getAttribute("value"),TrendType)
//        }else{
//            selected(keyG, this.innerHTML, this.getAttribute("value")/ multiple,TrendType)
//        }

        };
    }

    // 显示
    dialogBg.style.display = "block";
}
function showDialogN(flagType){
    if(objG.in[0] == 1){
        getVS(0,flagType);
        for(i in stringG.values){
            dialogContent.innerHTML += '<div class="dialogUnit" value="' + i + '">' + stringG.values[i] + '</div>';
        }
    }else{
        //(并显示大于、小于、变为)
        //getVS(1);
        var min = objG.in[1];
        var max = objG.in[2];
        var step = objG.in[3];
        var multiple = objG.in[4];
//        if(max>=900){
//           step = step*100;
//        }
        for(var i = min; i <= max; i += step){
            dialogContent.innerHTML += '<div class="dialogUnit" value="' + i + '">' + i / multiple + '</div>';
        }
    }

    // 注册点击事件
    var views = dialogContent.querySelectorAll(".dialogUnit");
    for(i in views) {
        views[i].onclick = function(){
            //selected(keyG, this.innerHTML, this.getAttribute("value")/multiple,TrendType)
//            if(objG.in[0] == 1){
                    selected(keyG, this.innerHTML, this.getAttribute("value"),TrendType)
//               }else{
//                    selected(keyG, this.innerHTML, this.getAttribute("value")/ multiple,TrendType)
//                }
        };
    }

    // 显示
    dialogBg.style.display = "block";
}
function showDialog(){
    // 清空页面元素
    dialogContent.innerHTML = "";

     keyG = this.getAttribute("name");
     objG = mProfileIntfs[keyG][0];
     stringG = mStringIntfs[keyG];
    //判断是否显示“大于”“小于”“变为”
    cordova.exec(getVisibleSucc,null,BRIDGE,'getvisible',[]);
}

/**
 * 选择值
 * @param key
 * @param name
 * @param value
 */
function selected(key, name, value,trendtype){
    var funcValue = funcContent.querySelector('.funcUnit[name=' + key + ']').querySelector(".funcValue");
    funcValue.setAttribute("value", value);
    funcValue.innerHTML = name;

    cordova.exec(null, null, BRIDGE, 'setValue', eval("["+"{" + key + ": " + value + "}"+","+"{"+"keyname"+": "+"\""+name+"\""+"}"+","+"{" + "trendtype" + ": " + trendtype + "}"+"]"));

    dialogBg.style.display = "none";
    getVS(0);

}

app.initialize();

//initView();