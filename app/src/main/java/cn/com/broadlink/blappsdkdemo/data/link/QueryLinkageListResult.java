package cn.com.broadlink.blappsdkdemo.data.link;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.data.BaseResult;

/**
 * Created by YeJin on 2016/9/14.
 */
public class QueryLinkageListResult extends BaseResult {
    private List<LinkageInfo> linkages = new ArrayList<>();

    public List<LinkageInfo> getLinkages() {
        return linkages;
    }

    public void setLinkages(List<LinkageInfo> linkages) {
        this.linkages = linkages;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.linkages);
    }

    public QueryLinkageListResult() {}

    protected QueryLinkageListResult(Parcel in) {
        super(in);
        this.linkages = in.createTypedArrayList(LinkageInfo.CREATOR);
    }

    public static final Creator<QueryLinkageListResult> CREATOR = new Creator<QueryLinkageListResult>() {
        @Override
        public QueryLinkageListResult createFromParcel(Parcel source) {return new QueryLinkageListResult(source);}

        @Override
        public QueryLinkageListResult[] newArray(int size) {return new QueryLinkageListResult[size];}
    };
}
