package cn.com.broadlink.blappsdkdemo.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.blappsdkdemo.intferfacer.SPControlModel;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

/**
 * Created by YeJin on 2016/5/10.
 */
public class SPControlModelImpl implements SPControlModel{

    private SPControlListener mSPControlListener;

    private SPControlModelImpl(){}

    private android.os.Handler mHandler = new android.os.Handler();

    public SPControlModelImpl(SPControlListener spControlListener){
        mSPControlListener = spControlListener;
    }

    private int dev_pwr = 0;

    @Override
    public void queryDevStatus(String did) {
        /**查询开关状态命令**/
        BLStdControlParam queryParam = new BLStdControlParam();
        queryParam.setAct(BLControlActConstans.ACT_GET);
        queryParam.getParams().add("pwr");

        final BLStdControlResult stdControlResult = BLLet.Controller.dnaControl(did, null, queryParam);
        if(mSPControlListener != null && stdControlResult != null && stdControlResult.getStatus() == BLControllerErrCode.SUCCESS){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSPControlListener.deviceStatusShow((Integer) stdControlResult.getData().getVals().get(0).get(0).getVal());
                }
            });
        }
    }

    @Override
    public void controlDevPwr(final String did, final int pwr) {
        /**设置开关状态**/
        if(mSPControlListener != null){
            mSPControlListener.controlStart();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                BLStdData.Value value = new BLStdData.Value();
                value.setVal(pwr);

                ArrayList<BLStdData.Value> pwrVals = new ArrayList<>();
                pwrVals.add(value);

                BLStdControlParam ctrlParam = new BLStdControlParam();
                ctrlParam.setAct(BLControlActConstans.ACT_SET);
                ctrlParam.getParams().add("pwr");
                ctrlParam.getVals().add(pwrVals);

                //设置单次控制重试次数
                BLConfigParam configParam = new BLConfigParam();
                configParam.put("CONTROLLER_SEND_COUNT", "3");

                final BLStdControlResult stdControlResult = BLLet.Controller.dnaControl(did, null, ctrlParam, configParam);
                if(mSPControlListener != null){
                    mSPControlListener.controlEnd();
                }

                if(stdControlResult != null && stdControlResult.getStatus() == BLControllerErrCode.SUCCESS){
                    if(mSPControlListener != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSPControlListener.controlSuccess((Integer) stdControlResult.getData().getVals().get(0).get(0).getVal());
                            }
                        });
                    }
                }else{
                    if(mSPControlListener != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSPControlListener.controlFail(stdControlResult);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public void taskDevSet(final String did,final String taskModel, final String taskData) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                //任务设置需要先GET，再SET，才能设置成功
                BLStdControlParam getParam = new BLStdControlParam();
                getParam.setAct(BLControlActConstans.ACT_GET);
                getParam.getParams().add(taskModel);

                final BLStdControlResult getControlResult = BLLet.Controller.dnaControl(did, null, getParam);
                if (getControlResult != null && getControlResult.getStatus() == BLControllerErrCode.SUCCESS) {

                    BLStdData.Value value = new BLStdData.Value();
                    value.setVal(taskData);
                    ArrayList<BLStdData.Value> vals = new ArrayList<>();
                    vals.add(value);

                    BLStdControlParam setParam = new BLStdControlParam();
                    setParam.setAct(BLControlActConstans.ACT_SET);
                    setParam.getParams().add(taskModel);
                    setParam.getVals().add(vals);

                    final BLStdControlResult setControlResult = BLLet.Controller.dnaControl(did, null, setParam);
                    if (setControlResult != null && setControlResult.getStatus() == BLControllerErrCode.SUCCESS) {
                        if(mSPControlListener != null){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSPControlListener.taskSuccess();
                                }
                            });
                        }
                    } else {
                        if(mSPControlListener != null){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSPControlListener.taskFaile(setControlResult.getMsg());
                                }
                            });
                        }
                    }
                } else {
                    if(mSPControlListener != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSPControlListener.taskFaile(getControlResult.getMsg());
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
