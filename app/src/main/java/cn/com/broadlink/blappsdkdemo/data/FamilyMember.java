package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FamilyMember implements Parcelable {
    private String userid;
    //用户类型，0-管理员，1-普通用户
    private int type;

    public FamilyMember() {}

    public FamilyMember(Parcel in) {
        userid = in.readString();
        type = in.readInt();
    }

    public static final Creator<FamilyMember> CREATOR = new Creator<FamilyMember>() {
        @Override
        public FamilyMember createFromParcel(Parcel in) {
            return new FamilyMember(in);
        }

        @Override
        public FamilyMember[] newArray(int size) {
            return new FamilyMember[size];
        }
    };

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeInt(type);
    }
}
