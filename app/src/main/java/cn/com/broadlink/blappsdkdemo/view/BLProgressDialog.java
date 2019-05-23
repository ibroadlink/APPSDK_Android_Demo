package cn.com.broadlink.blappsdkdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.R;


/**
 * 项目名称：BLEControlAppV4    
 * <br>类名称：BLProgressDialog	
 * <br>类描述： 进度框			
 * <br>创建人：YeJing 			
 * <br>创建时间：2015-4-9 下午6:44:13
 * <br>修改人：Administrator    		
 * <br>修改时间：2015-4-9 下午6:44:13
 * <br>修改备注：     		
 *
 */
public class BLProgressDialog extends Dialog {
	private BLProgressDialog(Context context) {
		super(context);
	}

	private BLProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static BLProgressDialog createDialog(Context context) {
		return createDialog(context, null);
	}

	public static BLProgressDialog createDialog(Context context, String message) {
	    BLProgressDialog customProgressDialog = new BLProgressDialog(context, R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.view_bl_progress);
		LayoutParams layoutParams = customProgressDialog.getWindow().getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		customProgressDialog.setCanceledOnTouchOutside(false);
		
		if(!TextUtils.isEmpty(message)){
		    TextView messageView = (TextView) customProgressDialog.findViewById(R.id.message);
		    messageView.setVisibility(View.VISIBLE);
		    messageView.setText(message); 
		}
		return customProgressDialog;
	}
	
	public static BLProgressDialog createDialog(Context context, int messageId){
	    return createDialog(context, context.getString(messageId));
	}

	public void setMessage(int message){
		setMessage(getContext().getString(message));
	}

	public void setMessage(String message){
		if(!TextUtils.isEmpty(message)){
			TextView messageView = (TextView) findViewById(R.id.message);
			messageView.setVisibility(View.VISIBLE);
			messageView.setText(message);
		}
	}
	
	public void toastShow(String message, int resid){
	    setCanceledOnTouchOutside(true);
	    TextView messageView = (TextView) findViewById(R.id.message);
	    ImageView stateIcon = (ImageView) findViewById(R.id.state_icon);
	    findViewById(R.id.progress_bar).setVisibility(View.GONE);
	    stateIcon.setVisibility(View.VISIBLE);
	    messageView.setVisibility(View.VISIBLE);
	    messageView.setText(message);
	    stateIcon.setImageResource(resid);
	    
	    new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
	}
}
