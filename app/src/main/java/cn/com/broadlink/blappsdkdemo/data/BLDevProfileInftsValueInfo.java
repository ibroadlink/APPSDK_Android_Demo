package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口的值
 * @author YeJin
 *
 */
public class BLDevProfileInftsValueInfo {

	private int idx;
	
	private int act;
	
	private List<Integer> in = new ArrayList<Integer>();

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getAct() {
		return act;
	}

	public void setAct(int act) {
		this.act = act;
	}

	public List<Integer> getIn() {
		return in;
	}

	public void setIn(List<Integer> in) {
		this.in = in;
	}
	
}
