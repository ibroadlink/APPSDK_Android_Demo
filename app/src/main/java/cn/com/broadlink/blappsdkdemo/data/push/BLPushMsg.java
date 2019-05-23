package cn.com.broadlink.blappsdkdemo.data.push;

/**
 * 作者：EchoJ on 2017/8/1 18:54 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：推送消息的格式
 * ihc://[target]/[action]?[params] 	示例 ihc://common/web?url=http://www.baidu.com
 [target]——为字符串枚举类型，现先定义一个类型（common）
 [action]——为字符串枚举类型，现先定义一个网页类型（web）
 [params] ——先定义action为web类型的params的格式为url=XXXXXXX
 */

public class BLPushMsg {

    /**
     * data : {"action":"ihc://device/fastconConfig?familyId=xxxxx&masterDid=b4430d123456&clientInfo={"did":b4430d123457,"pid":"0000000000000000000000008c270000","name":" 魔法棒 ","configType ":"NONE"}"}
     * msg : xxx
     */

    private String data;
    private String msg;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
