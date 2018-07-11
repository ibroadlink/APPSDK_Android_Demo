package cn.com.broadlink.blappsdkdemo.plugin;

/**
 * 返回JS错误code和信息
 * @author YeJin
 *
 */
public class BLJsBaseResult {

	private int code;
	
	private String error;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
