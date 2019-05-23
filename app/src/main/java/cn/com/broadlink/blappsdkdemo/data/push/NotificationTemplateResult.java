package cn.com.broadlink.blappsdkdemo.data.push;

import java.io.Serializable;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.data.BaseResult;

/**
 * 通知模板请求结果
 */

public class NotificationTemplateResult extends BaseResult {

    private int totalpage;
    private List<TemplatesBean> templates;

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public List<TemplatesBean> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplatesBean> templates) {
        this.templates = templates;
    }

    public static class TemplatesBean implements Serializable {
        private String templateid;
        private String companyid;
        private String templatetype;
        private boolean enable;
        private ConditionsinfoBean conditionsinfo;
        private List<String> categorys;
        private List<TemplatenameBean> templatename;
        private List<EventsBean> events;
        private List<ActionBean> action;

        public String getTemplateid() {
            return templateid;
        }

        public void setTemplateid(String templateid) {
            this.templateid = templateid;
        }

        public String getCompanyid() {
            return companyid;
        }

        public void setCompanyid(String companyid) {
            this.companyid = companyid;
        }

        public String getTemplatetype() {
            return templatetype;
        }

        public void setTemplatetype(String templatetype) {
            this.templatetype = templatetype;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public ConditionsinfoBean getConditionsinfo() {
            return conditionsinfo;
        }

        public void setConditionsinfo(ConditionsinfoBean conditionsinfo) {
            this.conditionsinfo = conditionsinfo;
        }

        public List<String> getCategorys() {
            return categorys;
        }

        public void setCategorys(List<String> categorys) {
            this.categorys = categorys;
        }

        public List<TemplatenameBean> getTemplatename() {
            return templatename;
        }

        public void setTemplatename(List<TemplatenameBean> templatename) {
            this.templatename = templatename;
        }

        public List<EventsBean> getEvents() {
            return events;
        }

        public void setEvents(List<EventsBean> events) {
            this.events = events;
        }

        public List<ActionBean> getAction() {
            return action;
        }

        public void setAction(List<ActionBean> action) {
            this.action = action;
        }

        public static class ConditionsinfoBean implements Serializable {
            private List<DatetimeBean> datetime;
            private List<PropertyBean> property;

            public List<DatetimeBean> getDatetime() {
                return datetime;
            }

            public void setDatetime(List<DatetimeBean> datetime) {
                this.datetime = datetime;
            }

            public List<PropertyBean> getProperty() {
                return property;
            }

            public void setProperty(List<PropertyBean> property) {
                this.property = property;
            }

            public static class DatetimeBean implements Serializable {

                private String weekdays;
                private int timezone;
                private List<String> validperiod;

                public String getWeekdays() {
                    return weekdays;
                }

                public void setWeekdays(String weekdays) {
                    this.weekdays = weekdays;
                }

                public int getTimezone() {
                    return timezone;
                }

                public void setTimezone(int timezone) {
                    this.timezone = timezone;
                }

                public List<String> getValidperiod() {
                    return validperiod;
                }

                public void setValidperiod(List<String> validperiod) {
                    this.validperiod = validperiod;
                }
            }

            public static class PropertyBean implements Serializable {

                private String ikey;
                private int ref_value;
                private String ref_value_name;
                private int trend_type;
                private String category;

                public String getIkey() {
                    return ikey;
                }

                public void setIkey(String ikey) {
                    this.ikey = ikey;
                }

                public int getRef_value() {
                    return ref_value;
                }

                public void setRef_value(int ref_value) {
                    this.ref_value = ref_value;
                }

                public String getRef_value_name() {
                    return ref_value_name;
                }

                public void setRef_value_name(String ref_value_name) {
                    this.ref_value_name = ref_value_name;
                }

                public int getTrend_type() {
                    return trend_type;
                }

                public void setTrend_type(int trend_type) {
                    this.trend_type = trend_type;
                }

                public String getCategory() {
                    return category;
                }

                public void setCategory(String category) {
                    this.category = category;
                }
            }
        }

        public static class TemplatenameBean implements Serializable {


            private String name;
            private String language;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }
        }

        public static class EventsBean implements Serializable {

            private String ikey;
            private int ref_value;
            private String ref_value_name;
            private int trend_type;
            private int type;
            private String name;
            private int keeptime;
            private String category;

            public String getIkey() {
                return ikey;
            }

            public void setIkey(String ikey) {
                this.ikey = ikey;
            }

            public int getRef_value() {
                return ref_value;
            }

            public void setRef_value(int ref_value) {
                this.ref_value = ref_value;
            }

            public String getRef_value_name() {
                return ref_value_name;
            }

            public void setRef_value_name(String ref_value_name) {
                this.ref_value_name = ref_value_name;
            }

            public int getTrend_type() {
                return trend_type;
            }

            public void setTrend_type(int trend_type) {
                this.trend_type = trend_type;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getKeeptime() {
                return keeptime;
            }

            public void setKeeptime(int keeptime) {
                this.keeptime = keeptime;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }
        }

        public static class ActionBean implements Serializable {

            private String language;
            private String name;
            private String status;
            private String templatetype;
            private AlicloudBean alicloud;
            private GcmBean gcm;
            private IosBean ios;
            private EmailBean email;
            private WechatBean wechat;

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public AlicloudBean getAlicloud() {
                return alicloud;
            }

            public void setAlicloud(AlicloudBean alicloud) {
                this.alicloud = alicloud;
            }

            public GcmBean getGcm() {
                return gcm;
            }

            public void setGcm(GcmBean gcm) {
                this.gcm = gcm;
            }

            public IosBean getIos() {
                return ios;
            }

            public void setIos(IosBean ios) {
                this.ios = ios;
            }

            public EmailBean getEmail() {
                return email;
            }

            public void setEmail(EmailBean email) {
                this.email = email;
            }

            public WechatBean getWechat() {
                return wechat;
            }

            public void setWechat(WechatBean wechat) {
                this.wechat = wechat;
            }

            public String getTemplatetype() {
                return templatetype;
            }

            public void setTemplatetype(String templatetype) {
                this.templatetype = templatetype;
            }

            public static class AlicloudBean implements Serializable {

                private String templateid;
                private String tagcode;
                private String content;
                private String templatecontent;
                private boolean enable;
                private String did;
                private String action;
                private List<String> keylist;

                public String getTemplateid() {
                    return templateid;
                }

                public void setTemplateid(String templateid) {
                    this.templateid = templateid;
                }

                public String getTagcode() {
                    return tagcode;
                }

                public void setTagcode(String tagcode) {
                    this.tagcode = tagcode;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getTemplatecontent() {
                    return templatecontent;
                }

                public void setTemplatecontent(String templatecontent) {
                    this.templatecontent = templatecontent;
                }

                public boolean isEnable() {
                    return enable;
                }

                public void setEnable(boolean enable) {
                    this.enable = enable;
                }

                public String getDid() {
                    return did;
                }

                public void setDid(String did) {
                    this.did = did;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public List<String> getKeylist() {
                    return keylist;
                }

                public void setKeylist(List<String> keylist) {
                    this.keylist = keylist;
                }
            }

            public static class GcmBean implements Serializable {
                private String templateid;
                private String tagcode;
                private String content;
                private String templatecontent;
                private boolean enable;
                private String did;
                private String action;
                private List<String> keylist;

                public String getTemplateid() {
                    return templateid;
                }

                public void setTemplateid(String templateid) {
                    this.templateid = templateid;
                }

                public String getTagcode() {
                    return tagcode;
                }

                public void setTagcode(String tagcode) {
                    this.tagcode = tagcode;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getTemplatecontent() {
                    return templatecontent;
                }

                public void setTemplatecontent(String templatecontent) {
                    this.templatecontent = templatecontent;
                }

                public boolean isEnable() {
                    return enable;
                }

                public void setEnable(boolean enable) {
                    this.enable = enable;
                }

                public String getDid() {
                    return did;
                }

                public void setDid(String did) {
                    this.did = did;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public List<String> getKeylist() {
                    return keylist;
                }

                public void setKeylist(List<String> keylist) {
                    this.keylist = keylist;
                }
            }

            public static class IosBean implements Serializable {

                private String templateid;
                private String tagcode;
                private List<String> keylist;
                private String content;
                private boolean enable;
                private String did;
                private String action;

                public String getTemplateid() {
                    return templateid;
                }

                public void setTemplateid(String templateid) {
                    this.templateid = templateid;
                }

                public String getTagcode() {
                    return tagcode;
                }

                public void setTagcode(String tagcode) {
                    this.tagcode = tagcode;
                }

                public List<String> getKeylist() {
                    return keylist;
                }

                public void setKeylist(List<String> keylist) {
                    this.keylist = keylist;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public boolean isEnable() {
                    return enable;
                }

                public void setEnable(boolean enable) {
                    this.enable = enable;
                }

                public String getDid() {
                    return did;
                }

                public void setDid(String did) {
                    this.did = did;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }
            }

            public static class EmailBean implements Serializable {

                private String templateid;
                private String tagcode;
                private List<String> keylist;
                private String content;
                private boolean enable;
                private String did;

                public String getTemplateid() {
                    return templateid;
                }

                public void setTemplateid(String templateid) {
                    this.templateid = templateid;
                }

                public String getTagcode() {
                    return tagcode;
                }

                public void setTagcode(String tagcode) {
                    this.tagcode = tagcode;
                }

                public List<String> getKeylist() {
                    return keylist;
                }

                public void setKeylist(List<String> keylist) {
                    this.keylist = keylist;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public boolean isEnable() {
                    return enable;
                }

                public void setEnable(boolean enable) {
                    this.enable = enable;
                }

                public String getDid() {
                    return did;
                }

                public void setDid(String did) {
                    this.did = did;
                }
            }

            public static class WechatBean implements Serializable {
                private String templateid;
                private String tagcode;
                private List<String> keylist;
                private String content;
                private boolean enable;
                private String did;

                public String getTemplateid() {
                    return templateid;
                }

                public void setTemplateid(String templateid) {
                    this.templateid = templateid;
                }

                public String getTagcode() {
                    return tagcode;
                }

                public void setTagcode(String tagcode) {
                    this.tagcode = tagcode;
                }

                public List<String> getKeylist() {
                    return keylist;
                }

                public void setKeylist(List<String> keylist) {
                    this.keylist = keylist;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public boolean isEnable() {
                    return enable;
                }

                public void setEnable(boolean enable) {
                    this.enable = enable;
                }

                public String getDid() {
                    return did;
                }

                public void setDid(String did) {
                    this.did = did;
                }
            }
        }
    }
}
