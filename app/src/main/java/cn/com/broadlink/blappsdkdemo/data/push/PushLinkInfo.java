package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.ArrayList;
import java.util.List;

public class PushLinkInfo {
    
    public String familyid;
    public String ruleid;
    public String rulename = "EchoTestRule";
    
    //联动信息，json格式，app透传
    public String characteristicinfo;
    
    //地址信息,暂时填空
    public String locationinfo;

    //json格式场景内容 
    public String modulecontent;
    
    //场景id，如无场景联动，则填空列表
    public List<String> moduleid = new ArrayList<>();
    
    //"H5":表示此联动由H5页面设置; 
    // "APP": 表示此联动由APP 联动设置（默认）; 
    // "notify_xxx": 表示此联动为设备消息通知 （xxx 表示模板实例化templateid值）格式为"notify_<templateid>",可通过该字段确定模板是否已实例化
    public String source;
    
    // 固定为1
    public int ruletype = 1;
    
    //1使能
    public int enable = 1;
    
    //联动内场景，若无场景,则不填
    public List<LinkDevInfoBean> linkagedevices = new ArrayList<>();

    public PushLinkInfo() {
    }

    public static final class LinkDevInfoBean{
        
        //场景id
        public String moduleid;
        
        //场景名称
        public String name;
        
        //模板信息经过base64编码
        public String extern;
        
        //联动类型,默认通知
        public String linkagetype = "notify";

        public LinkDevInfoBean() {
        }
    }
    
    
}
