package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

public class CloudAcBrandResponse extends BaseResult{

	/**
	 * respbody : {"brand":[{"brandid":2,"brand":"sds","enbrand":"","famousstatus":0},{"brandid":5,"brand":"长虹","enbrand":"","famousstatus":0},{"brandid":10,"brand":"海信","enbrand":"","famousstatus":0},{"brandid":11,"brand":"海尔","enbrand":"","famousstatus":0},{"brandid":14,"brand":"东芝","enbrand":"","famousstatus":0},{"brandid":16,"brand":"三星","enbrand":"","famousstatus":0},{"brandid":17,"brand":"三洋","enbrand":"","famousstatus":0},{"brandid":20,"brand":"松下","enbrand":"","famousstatus":0},{"brandid":21,"brand":"夏普","enbrand":"","famousstatus":0},{"brandid":47,"brand":"飞鹿","enbrand":"","famousstatus":0},{"brandid":53,"brand":"春兰","enbrand":"","famousstatus":0},{"brandid":76,"brand":"东宝","enbrand":"","famousstatus":0},{"brandid":90,"brand":"富士通","enbrand":"","famousstatus":0},{"brandid":196,"brand":"TCL","enbrand":"","famousstatus":0},{"brandid":212,"brand":"日立","enbrand":"","famousstatus":0},{"brandid":213,"brand":"天鹅","enbrand":"","famousstatus":0},{"brandid":273,"brand":"LG","enbrand":"","famousstatus":0},{"brandid":349,"brand":"三菱","enbrand":"","famousstatus":0},{"brandid":370,"brand":"三菱电机","enbrand":"","famousstatus":0},{"brandid":371,"brand":"三菱重工","enbrand":"","famousstatus":0},{"brandid":372,"brand":"伊莱克斯","enbrand":"","famousstatus":0},{"brandid":373,"brand":"华凌","enbrand":"","famousstatus":0},{"brandid":374,"brand":"大金","enbrand":"","famousstatus":0},{"brandid":375,"brand":"奥克斯","enbrand":"","famousstatus":0},{"brandid":376,"brand":"开利","enbrand":"","famousstatus":0},{"brandid":377,"brand":"志高","enbrand":"","famousstatus":0},{"brandid":378,"brand":"惠而浦","enbrand":"","famousstatus":0},{"brandid":379,"brand":"格兰仕","enbrand":"","famousstatus":0},{"brandid":380,"brand":"格力","enbrand":"","famousstatus":0},{"brandid":381,"brand":"特灵","enbrand":"","famousstatus":0},{"brandid":382,"brand":"玉兔","enbrand":"","famousstatus":0},{"brandid":383,"brand":"科龙","enbrand":"","famousstatus":0},{"brandid":384,"brand":"约克","enbrand":"","famousstatus":0},{"brandid":385,"brand":"澳柯玛","enbrand":"","famousstatus":0},{"brandid":386,"brand":"华宝","enbrand":"","famousstatus":0},{"brandid":394,"brand":"YORK","enbrand":"","famousstatus":0},{"brandid":395,"brand":"柯耐弗","enbrand":"","famousstatus":0},{"brandid":396,"brand":"新飞","enbrand":"","famousstatus":0},{"brandid":397,"brand":"SOGO","enbrand":"","famousstatus":0},{"brandid":399,"brand":"康丽","enbrand":"","famousstatus":0},{"brandid":400,"brand":"蓝波","enbrand":"","famousstatus":0},{"brandid":401,"brand":"山星","enbrand":"","famousstatus":0},{"brandid":402,"brand":"上菱","enbrand":"","famousstatus":0},{"brandid":403,"brand":"双鹿","enbrand":"","famousstatus":0},{"brandid":404,"brand":"索华","enbrand":"","famousstatus":0},{"brandid":405,"brand":"同力","enbrand":"","famousstatus":0},{"brandid":406,"brand":"小鸭","enbrand":"","famousstatus":0},{"brandid":407,"brand":"新乐","enbrand":"","famousstatus":0},{"brandid":408,"brand":"扬子","enbrand":"","famousstatus":0},{"brandid":409,"brand":"中意","enbrand":"","famousstatus":0},{"brandid":410,"brand":"NISO","enbrand":"","famousstatus":0},{"brandid":411,"brand":"内田","enbrand":"","famousstatus":0},{"brandid":412,"brand":"现代","enbrand":"","famousstatus":0},{"brandid":413,"brand":"格林","enbrand":"","famousstatus":0},{"brandid":414,"brand":"SAPORO","enbrand":"","famousstatus":0},{"brandid":415,"brand":"未知品牌","enbrand":"","famousstatus":0},{"brandid":416,"brand":"YUETU","enbrand":"","famousstatus":0},{"brandid":417,"brand":"STARIGHT_AIRCON","enbrand":"","famousstatus":0}]}
	 */

	private RespbodyBean respbody = new RespbodyBean();

	public RespbodyBean getRespbody() {
		return respbody;
	}

	public void setRespbody(RespbodyBean respbody) {
		this.respbody = respbody;
	}

	public List<BLRmBrandInfo> getBrand() {
		return respbody.getBrand();
	}

	public static class RespbodyBean {
		private List<BLRmBrandInfo> brand;

		public List<BLRmBrandInfo> getBrand() {
			return brand;
		}

		public void setBrand(List<BLRmBrandInfo> brand) {
			this.brand = brand;
		}
	}
}
