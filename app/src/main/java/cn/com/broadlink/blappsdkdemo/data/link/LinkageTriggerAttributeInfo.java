package cn.com.broadlink.blappsdkdemo.data.link;

import java.util.ArrayList;
import java.util.List;

/**
 * 联动属性触发
 * Created by YeJin on 2016/9/13.
 */
public class LinkageTriggerAttributeInfo {
    //触发数组
    private List<LinkageDevPropertyInfo> events = new ArrayList<>();

    private LinkageConditionsInfo conditionsinfo;

    public List<LinkageDevPropertyInfo> getEvents() {
        return events;
    }

    public void setEvents(List<LinkageDevPropertyInfo> events) {
        this.events = events;
    }

    public LinkageConditionsInfo getConditionsinfo() {
        return conditionsinfo;
    }

    public void setConditionsinfo(LinkageConditionsInfo conditionsinfo) {
        this.conditionsinfo = conditionsinfo;
    }
}
