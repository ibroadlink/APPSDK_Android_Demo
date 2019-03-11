package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 电视频道
 * Project: BLEControlAppV4</p>
 * Title: RmTvChannelResult</p>
 * Company: BroadLink</p> 
 * @author YeJing
 * @date 2015-10-23
 */
public class RmTvChannelResponse extends BaseResult{
	private RespbodyBean respbody = new RespbodyBean();

	public RespbodyBean getRespbody() {
		return respbody;
	}

	public void setRespbody(RespbodyBean respbody) {
		this.respbody = respbody;
	}

	public static class RespbodyBean {
		private List<BLRmBrandInfo> tvchannel = new ArrayList<>();

		public List<BLRmBrandInfo> getTvchannel() {
			return tvchannel;
		}

		public void setTvchannel(List<BLRmBrandInfo> tvchannel) {
			this.tvchannel = tvchannel;
		}
	}
}
