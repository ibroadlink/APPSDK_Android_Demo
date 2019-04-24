package cn.com.broadlink.blappsdkdemo.data.link;

import java.util.List;

import cn.com.broadlink.blappsdkdemo.data.push.NotificationTemplateResult;

/**
 * Created by Administrator on 2018/6/11 0011.
 */

public class LinkageDeviceExtend {

    private List<NotificationTemplateResult.TemplatesBean.ActionBean> action;

    public List<NotificationTemplateResult.TemplatesBean.ActionBean> getAction() {
        return action;
    }

    public void setAction(List<NotificationTemplateResult.TemplatesBean.ActionBean> action) {
        this.action = action;
    }
}

