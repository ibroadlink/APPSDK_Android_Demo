package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/8 17:20
 */
public class BLAcOneKeyPairResultInfo {

    public List<DownloadinfoBean> downloadinfo = new ArrayList<>();

    public static class DownloadinfoBean {
        /**
         * downloadurl : /publicircode/v2/app/getfuncfilebyfixedid?fixedid=32398871
         * fixkey : 7dcbbc3c
         * name : 美的_5692
         * fixedId : 32398871
         */

        public String downloadurl;
        public String fixkey;
        public String name;
        public String fixedId;
    }
}
