package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseResult implements Parcelable {
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

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.error);
        dest.writeInt(this.status);
        dest.writeString(this.msg);
    }

    public BaseResult() {}

    protected BaseResult(Parcel in) {
        this.error = in.readInt();
        this.status = in.readInt();
        this.msg = in.readString();
    }

}
