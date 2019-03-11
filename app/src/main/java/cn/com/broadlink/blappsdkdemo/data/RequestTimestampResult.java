package cn.com.broadlink.blappsdkdemo.data;

public class RequestTimestampResult extends BaseResult{

    private String key;
    
    private String timestamp;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
}
