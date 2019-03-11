package cn.com.broadlink.blappsdkdemo.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import cn.com.broadlink.blappsdkdemo.R;


/**
 * 项目名称：BLEControlAppV4    
 * <br>类名称：BLAlert	
 * <br>类描述： BL对话框			
 * <br>创建人：YeJing 			
 * <br>创建时间：2015-3-26 下午1:05:05
 * <br>修改人：Administrator    		
 * <br>修改时间：2015-3-26 下午1:05:05
 * <br>修改备注：     		
 *
 */
public class BLAlert {
    
    private BLAlert(){};

    public static Dialog showDilog(Context context, int titleId, int messageId, int positiveButtonNameId,
                                   int negativeButtonNameId, final DialogOnClickListener clickListener){
        return showDilog(context,
                titleId != 0 ? context.getString(titleId) : null,
                messageId != 0 ? context.getString(messageId) : null,
                positiveButtonNameId != 0 ? context.getString(positiveButtonNameId) : null,
                negativeButtonNameId != 0 ? context.getString(negativeButtonNameId) : null,
                clickListener);
        
    }
    
    
    /**
     * @param context
     *          上下文
     * @param title
     *          标题
     * @param message
     *          信息
     * @param positiveButtonName
     *          确认按钮名称
     * @param negativeButtonName
     *          取消按钮名称
     * @param clickListener
     *          按钮时间
     * @return AlertDialog
     *          对话框
     */
    public static Dialog showDilog(Context context, String title, String message, String positiveButtonName,
                                   String negativeButtonName, final DialogOnClickListener clickListener) {
        return BLStyleDialog.Builder(context).setTitle(title).setMessage(message)
                .setCacelButton(negativeButtonName, new BLStyleDialog.OnBLDialogBtnListener() {
                    @Override
                    public void onClick(Button btn) {
                        if(clickListener != null){
                            clickListener.onNegativeClick();
                        }
                    }
                })
                .setConfimButton(positiveButtonName, new BLStyleDialog.OnBLDialogBtnListener() {
                    @Override
                    public void onClick(Button btn) {
                        if(clickListener != null){
                            clickListener.onPositiveClick();
                        }
                    }
                }).show();

    }

    public static Dialog showDialog(Context context, String title, String message, final DialogOnClickListener clickListener) {
        return BLStyleDialog.Builder(context).setTitle(title).setMessage(message)
                .setCacelButton("Cancel", new BLStyleDialog.OnBLDialogBtnListener() {
                    @Override
                    public void onClick(Button btn) {
                        if(clickListener != null){
                            clickListener.onNegativeClick();
                        }
                    }
                })
                .setConfimButton("Ok", new BLStyleDialog.OnBLDialogBtnListener() {
                    @Override
                    public void onClick(Button btn) {
                        if(clickListener != null){
                            clickListener.onPositiveClick();
                        }
                    }
                }).show();

    }

    public static Dialog showDialog(Context context, String message, final DialogOnClickListener clickListener) {
        return showDialog(context, null, message, clickListener);
    }

    public static Dialog showAlert(Context context, String title, String message, final OnSingleClickListener clickListener) {
        return BLStyleDialog.Builder(context).setTitle(title).setMessage(message)
                .setConfimButton("Ok", new BLStyleDialog.OnBLDialogBtnListener() {
                    @Override
                    public void onClick(Button btn) {
                        if(clickListener != null){
                            clickListener.doOnClick(null);
                        }
                    }
                }).show();

    }

    /**
     * 设置自定义对话框
     * @param context
     * @param view
     * @param positiveButtonName
     * @param negativeButtonName
     * @param clickListener
     * @return
     */
    public static AlertDialog showCustomViewDilog(Context context, View view, String positiveButtonName, String negativeButtonName, final DialogOnClickListener clickListener) {
        final Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                                .setView(view)
                                .setCancelable(false);
                                
        if (negativeButtonName != null) {
            builder.setNegativeButton(negativeButtonName, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickListener != null) {
                        clickListener.onNegativeClick();
                    }
                }
            });
        }

        if (positiveButtonName != null) {
            builder.setPositiveButton(positiveButtonName, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickListener != null) {
                        clickListener.onPositiveClick();
                    }
                }
            });
        }
        return builder.show();
    }

    /**
     * 关闭对话框
     */
    public static void autoClose(DialogInterface dialog, boolean showing){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, showing);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static AlertDialog showEditDilog(final Context context, String msg, String value, final BLEditDialogOnClickListener clickListener, boolean isNumberType) {
        return showEditDilog(context, null, msg,value,null,"Ok", "Cancel",clickListener, isNumberType);
    }
    

    /**
     * @param context
     *          上下文
     * @param title
     *          标题
     * @param contentHint
     *          提示内容
     * @param value
     *          EidtText需要显示的内容信息
     * @param editHint
     *          EidtText hint需要显示的内容信息
     * @param positiveButtonName
     *          确认按钮名称
     * @param negativeButtonName
     *          取消按钮名称
     * @param clickListener
     *          按钮时间
     * @return AlertDialog
     *          对话框
     */
    public static AlertDialog showEditDilog(final Context context, String title, String contentHint,
                                            String value, String editHint, String positiveButtonName,
                                            String negativeButtonName, final BLEditDialogOnClickListener clickListener, boolean isNumberType) {
    	Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);

        View view = LayoutInflater.from(context).inflate(R.layout.view_bl_input_alert_content, null);
        TextView hintTextView = (TextView) view.findViewById(R.id.content_hint_view);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_view);
        titleTextView.setText(title);
        final EditText editText = (EditText) view.findViewById(R.id.input_view);
        if(isNumberType){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setHint(editHint);
        
        if(!TextUtils.isEmpty(contentHint)){
            hintTextView.setVisibility(View.VISIBLE);
            hintTextView.setText(contentHint);
        }
        
        if(!TextUtils.isEmpty(value)){
            editText.setText(value);
            editText.setSelection(value.length());
        }
        
        if (negativeButtonName != null) {
            builder.setNegativeButton(negativeButtonName, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickListener != null) {
                    	clickListener.onClinkCacel(editText.getText().toString());
                    }
                }
            });
        }

        if (positiveButtonName != null) {
            builder.setPositiveButton(positiveButtonName, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickListener != null) {
                    	String value = editText.getText().toString();
                    	clickListener.onClink(value);
                    }
                }
            });
        }
        AlertDialog alertDialog = builder.setView(view).show();
        alertDialog.setCanceledOnTouchOutside(false);
        
        return alertDialog;
    }

    /**
     * 密码输入的alert
     */
    public static AlertDialog showPwdEditDilog(final Context context, String title, String contentHint,
                                               String value, String editHint, String positiveButtonName,
                                               String negativeButtonName, final BLEditDialogOnClickListener clickListener) {
        Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);

        View view = LayoutInflater.from(context).inflate(R.layout.view_bl_input_pwd_alert, null);
        TextView hintTextView = (TextView) view.findViewById(R.id.content_hint_view);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_view);
        titleTextView.setText(title);
        
        final InputTextView editText = (InputTextView) view.findViewById(R.id.input_view);
        editText.getEditText().setHint(editHint);

        if(!TextUtils.isEmpty(contentHint)){
            hintTextView.setVisibility(View.VISIBLE);
            hintTextView.setText(contentHint);
        }

        if(!TextUtils.isEmpty(value)){
            editText.getEditText().setText(value);
            editText.getEditText().setSelection(value.length());
        }

        if (negativeButtonName != null) {
            builder.setNegativeButton(negativeButtonName, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickListener != null) {
                        clickListener.onClinkCacel(editText.getEditText().getText().toString());
                    }
                }
            });
        }

        if (positiveButtonName != null) {
            builder.setPositiveButton(positiveButtonName, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (clickListener != null) {
                        String value = editText.getEditText().getText().toString();
                        clickListener.onClink(value);
                    }
                }
            });
        }
        AlertDialog alertDialog = builder.setView(view).show();
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }



    public static Dialog showAlert(Context context, String title, String messageId,
                                   String confimButtonText, BLStyleDialog.OnBLDialogBtnListener confimBtnListener,
                                   String cancleButtonText, BLStyleDialog.OnBLDialogBtnListener cacenlBtnListener) {
       return BLStyleDialog.Builder(context).setTitle(title).setMessage(messageId)
               .setCacelButton(cancleButtonText, cacenlBtnListener)
               .setConfimButton(confimButtonText, confimBtnListener).show();
    }

//    public static Dialog showAlert(Context context, View view,
//                                             String confimButtonText, BLStyleDialog.OnBLDialogBtnListener confimBtnListener,
//                                             String cancleButtonText, BLStyleDialog.OnBLDialogBtnListener cacenlBtnListener,
//                                             BLStyleDialog.BLDialogDismissListener listener) {
//        return BLStyleDialog.Builder(context).setView(view)
//                .setCacelButton(confimButtonText, confimBtnListener)
//                .setConfimButton(cancleButtonText, cacenlBtnListener)
//                .setBLDialogDismissListener(listener)
//                .show();
//    }

    /**
     * Dialog事件的接口.
     */
    public interface BLEditDialogOnClickListener {
        void onClink(String value);
        void onClinkCacel(String value);
    }
    
    /**
     * Dialog事件的接口.
     */
    public interface DialogOnClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }
}
