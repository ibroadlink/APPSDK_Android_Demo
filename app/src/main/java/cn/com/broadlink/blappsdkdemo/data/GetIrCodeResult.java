package cn.com.broadlink.blappsdkdemo.data;

/**
 * 获取红码
 * Created by zhaohenghao on 2018/9/6.
 */
public class GetIrCodeResult extends BaseResult {
    private String randkey;
    private byte[] data;

    public String getRandkey() {
        return randkey;
    }

    public void setRandkey(String randkey) {
        this.randkey = randkey;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
