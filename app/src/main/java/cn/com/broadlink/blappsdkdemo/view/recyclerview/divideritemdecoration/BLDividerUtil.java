package cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;

 /**
 * 生成默认的RecyclerView分割线
 *  
 * @author JiangYaqiang
 * createrd at 2019/3/22 15:58
 */
public class BLDividerUtil {
    
    public static BLDividerItemDecoration getDefault(final Context context, final List< ? extends Object> data) {

        return new BLDividerItemDecoration(context) {

            @Nullable
            @Override
            public Divider getDivider(int itemPosition) {
                BLDividerBuilder builder = new BLDividerBuilder();
                if (itemPosition != data.size() - 1) {
                    builder.setBottomSideLine(true, context.getResources().getColor(R.color.gray), 1, 0, 0);
                }
                return builder.create();
            }
        };
    }
}
