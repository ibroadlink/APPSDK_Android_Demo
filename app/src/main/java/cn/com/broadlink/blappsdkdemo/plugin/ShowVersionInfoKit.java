package cn.com.broadlink.blappsdkdemo.plugin;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.plugin.VersionInfoActivity;

/**
 * 作者：EchoJ on 2019/3/7 11:38 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */
public class ShowVersionInfoKit implements IKit {
    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getName() {
        return R.string.version_info;
    }

    @Override
    public int getIcon() {
        return R.mipmap.icon;
    }

    @Override
    public void onClick(Context context) {
        final Intent intent = new Intent(context, VersionInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {

    }
}
