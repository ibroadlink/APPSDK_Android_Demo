package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 红码树结果
 * Created by zhaohenghao on 2018/8/30.
 */
public class RmIrTreeResult extends BaseResult implements Serializable {
    private RespBody respbody;

    public RespBody getRespbody() {
        return respbody;
    }

    public void setRespbody(RespBody respbody) {
        this.respbody = respbody;
    }

    public static class RespBody implements Serializable {
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
    }

    public static class HotIrCode implements Serializable {
        private List<String> ircodeid;
        private Map<String, List<RmTvCodeInfo>> code;

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
    }

    public static class IrTree implements Serializable {
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
    }

    public static class IrCode implements Serializable {
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
    }
}
