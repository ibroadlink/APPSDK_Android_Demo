package cn.com.broadlink.blappsdkdemo.intferfacer;


/**
 * 作者：EchoJ on 2017/9/20 16:00 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */

public abstract class BasePushListener {
    public String getProgreeDialogTip(){return null;}
    public boolean isShowProgressDialog(){return true;}
    public abstract void onCallBack(String result);
}
