package cn.com.broadlink.blappsdkdemo.data;


public class BaseResult {
    private int error = -1;

    private int status = -1;

    private String msg;

    public int getError() {
        return error == -1 ? status : error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess(){
        return getError() == 0;
    }

    public int getStatus() {
        return status == -1 ? error : status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
