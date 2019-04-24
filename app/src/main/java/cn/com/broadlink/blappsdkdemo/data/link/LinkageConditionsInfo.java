package cn.com.broadlink.blappsdkdemo.data.link;

import java.util.ArrayList;
import java.util.List;

/**
 * 触发条件
 * Created by YeJin on 2016/9/13.
 */
public class LinkageConditionsInfo {
    private List<LinkageDevPropertyInfo> property = new ArrayList<>();

    private ArrayList<LinkageConditionTimeInfo> datetime = new ArrayList<>();

    public List<LinkageDevPropertyInfo> getProperty() {
        return property;
    }

    public void setProperty(List<LinkageDevPropertyInfo> property) {
        this.property = property;
    }

    public ArrayList<LinkageConditionTimeInfo> getDatetime() {
        return datetime;
    }

    public void setDatetime(ArrayList<LinkageConditionTimeInfo> datetime) {
        this.datetime = datetime;
    }
}
