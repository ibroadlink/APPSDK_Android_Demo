package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author JiangYaqiang
 * 2019/6/27 16:03
 */
public final class IhgBulbInfo implements Parcelable {
    
    public ArrayList<String> maclist = new ArrayList<>();
    public ArrayList<Integer> rgblist = new ArrayList<>();
    public ShowParam showParam = new ShowParam();

    public IhgBulbInfo(int bulbCnt) {
        for (int i = 0; i < bulbCnt; i++) {
            maclist.add("000000000000");
            rgblist.add(0);
        }
    }
    public IhgBulbInfo() {
    }

    protected IhgBulbInfo(Parcel in) {
        this.maclist = in.createStringArrayList();
        this.rgblist = new ArrayList<>();
        in.readList(this.rgblist, Integer.class.getClassLoader());
        this.showParam = in.readParcelable(ShowParam.class.getClassLoader());
    }

    public static final class ShowParam implements Parcelable {
        
        public String loop = IhgBulbWallConstants.LOOP_TYPE.SINGLE;
        public int times = 10;
        public int interval = 3;
        public int brightness = 30;
        
        
        protected ShowParam(Parcel in) {
            this.loop = in.readString();
            this.times = in.readInt();
            this.interval = in.readInt();
            this.brightness = in.readInt();
        }

        public static final Creator<ShowParam> CREATOR = new Creator<ShowParam>() {
            @Override
            public ShowParam createFromParcel(Parcel source) {return new ShowParam(source);}

            @Override
            public ShowParam[] newArray(int size) {return new ShowParam[size];}
        };
        
        public ShowParam() {
        }
     
        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.loop);
            dest.writeInt(this.times);
            dest.writeInt(this.interval);
            dest.writeInt(this.brightness);
        }
    }

    public static final Creator<IhgBulbInfo> CREATOR = new Creator<IhgBulbInfo>() {
        @Override
        public IhgBulbInfo createFromParcel(Parcel source) {return new IhgBulbInfo(source);}

        @Override
        public IhgBulbInfo[] newArray(int size) {return new IhgBulbInfo[size];}
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.maclist);
        dest.writeList(this.rgblist);
        dest.writeParcelable(this.showParam, flags);
    }

}
