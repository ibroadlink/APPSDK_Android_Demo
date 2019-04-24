package cn.com.broadlink.blappsdkdemo.data.push;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PushQueryTempListParam {
    
    //大类.小类 （产品profile文件的srvs字段第一位，第三位）,如srvs:"1.50.3"，category为"1.3", 若无则填空即可
    public List<String> category = new ArrayList<>();
    
   //需要传lid
    public String companyid;
    
    //开始页
    public int pagestart = 1;
    
    //如何非空,附加条件为该类型, app无用处，填空
    public String templatetype;
    
    //页数
    public int pagesize = 2147483647;

    public PushQueryTempListParam(String cat) {
        //companyid = BLLet.getLicenseId(); // companyid?
        
        if(!TextUtils.isEmpty(cat)){
            companyid = "b0491bc574dfa144908c3cd671f56370";
            category.add(cat);
        }
    }
}
