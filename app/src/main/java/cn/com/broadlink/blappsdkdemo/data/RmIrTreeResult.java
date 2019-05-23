package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 红码树结果
 * Created by zhaohenghao on 2018/8/30.
 */
public class RmIrTreeResult extends BaseResult {
    private RespBody respbody;

    public RespBody getRespbody() {
        return respbody;
    }

    public void setRespbody(RespBody respbody) {
        this.respbody = respbody;
    }

    public static class RespBody implements android.os.Parcelable {
        private HotIrCode hotircode;
        private IrTree matchtree;
        private HotIrCode nobyteircode;

        public HotIrCode getHotircode() {
            return hotircode;
        }

        public void setHotircode(HotIrCode hotircode) {
            this.hotircode = hotircode;
        }

        public IrTree getMatchtree() {
            return matchtree;
        }

        public void setMatchtree(IrTree matchtree) {
            this.matchtree = matchtree;
        }

        public HotIrCode getNobyteircode() {
            return nobyteircode;
        }

        public void setNobyteircode(HotIrCode nobyteircode) {
            this.nobyteircode = nobyteircode;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.hotircode, flags);
            dest.writeParcelable(this.matchtree, flags);
            dest.writeParcelable(this.nobyteircode, flags);
        }

        public RespBody() {}

        protected RespBody(Parcel in) {
            this.hotircode = in.readParcelable(HotIrCode.class.getClassLoader());
            this.matchtree = in.readParcelable(IrTree.class.getClassLoader());
            this.nobyteircode = in.readParcelable(HotIrCode.class.getClassLoader());
        }

        public static final Creator<RespBody> CREATOR = new Creator<RespBody>() {
            @Override
            public RespBody createFromParcel(Parcel source) {return new RespBody(source);}

            @Override
            public RespBody[] newArray(int size) {return new RespBody[size];}
        };
    }

    public static class HotIrCode implements android.os.Parcelable {
        private List<String> ircodeid = new ArrayList<>();
        private Map<String, List<RmTvCodeInfo>> code = new HashMap<>();

        public List<String> getIrcodeid() {
            return ircodeid;
        }

        public void setIrcodeid(List<String> ircodeid) {
            this.ircodeid = ircodeid;
        }

        public Map<String, List<RmTvCodeInfo>> getCode() {
            return code;
        }

        public void setCode(Map<String, List<RmTvCodeInfo>> code) {
            this.code = code;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(this.ircodeid);
            dest.writeInt(this.code.size());
            for (Map.Entry<String, List<RmTvCodeInfo>> entry : this.code.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeTypedList(entry.getValue());
            }
        }

        public HotIrCode() {}

        protected HotIrCode(Parcel in) {
            this.ircodeid = in.createStringArrayList();
            int codeSize = in.readInt();
            this.code = new HashMap<String, List<RmTvCodeInfo>>(codeSize);
            for (int i = 0; i < codeSize; i++) {
                String key = in.readString();
                List<RmTvCodeInfo> value = in.createTypedArrayList(RmTvCodeInfo.CREATOR);
                this.code.put(key, value);
            }
        }

        public static final Creator<HotIrCode> CREATOR = new Creator<HotIrCode>() {
            @Override
            public HotIrCode createFromParcel(Parcel source) {return new HotIrCode(source);}

            @Override
            public HotIrCode[] newArray(int size) {return new HotIrCode[size];}
        };
    }

    public static class IrTree implements android.os.Parcelable {
        private String key;

        private List<IrCode> codeList;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<IrCode> getCodeList() {
            return codeList;
        }

        public void setCodeList(List<IrCode> codeList) {
            this.codeList = codeList;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.key);
            dest.writeTypedList(this.codeList);
        }

        public IrTree() {}

        protected IrTree(Parcel in) {
            this.key = in.readString();
            this.codeList = in.createTypedArrayList(IrCode.CREATOR);
        }

        public static final Creator<IrTree> CREATOR = new Creator<IrTree>() {
            @Override
            public IrTree createFromParcel(Parcel source) {return new IrTree(source);}

            @Override
            public IrTree[] newArray(int size) {return new IrTree[size];}
        };
    }

    public static class IrCode implements android.os.Parcelable {
        private byte[] code;
        private String ircodeid;//如果不为空则有唯一红码
        private IrTree chirdren;

        public byte[] getCode() {
            return code;
        }

        public void setCode(byte[] code) {
            this.code = code;
        }

        public String getIrcodeid() {
            return ircodeid;
        }

        public void setIrcodeid(String ircodeid) {
            this.ircodeid = ircodeid;
        }

        public IrTree getChirdren() {
            return chirdren;
        }

        public void setChirdren(IrTree chirdren) {
            this.chirdren = chirdren;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByteArray(this.code);
            dest.writeString(this.ircodeid);
            dest.writeParcelable(this.chirdren, flags);
        }

        public IrCode() {}

        protected IrCode(Parcel in) {
            this.code = in.createByteArray();
            this.ircodeid = in.readString();
            this.chirdren = in.readParcelable(IrTree.class.getClassLoader());
        }

        public static final Creator<IrCode> CREATOR = new Creator<IrCode>() {
            @Override
            public IrCode createFromParcel(Parcel source) {return new IrCode(source);}

            @Override
            public IrCode[] newArray(int size) {return new IrCode[size];}
        };
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.respbody, flags);
    }

    public RmIrTreeResult() {}

    protected RmIrTreeResult(Parcel in) {
        super(in);
        this.respbody = in.readParcelable(RespBody.class.getClassLoader());
    }

    public static final Creator<RmIrTreeResult> CREATOR = new Creator<RmIrTreeResult>() {
        @Override
        public RmIrTreeResult createFromParcel(Parcel source) {return new RmIrTreeResult(source);}

        @Override
        public RmIrTreeResult[] newArray(int size) {return new RmIrTreeResult[size];}
    };
}
