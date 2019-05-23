package cn.com.broadlink.blappsdkdemo.activity.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.com.broadlink.blappsdkdemo.R;

public class BaseFragment extends Fragment {

	private FrameLayout mBody;

    @Override
	public void onDestroy() {
        super.onDestroy();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_base_frame, container, false);

		findView(view);

		mBody.addView(setContentView(inflater, container, savedInstanceState));

		return view;
	}

	public View setContentView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
		return setContentView(inflater, container, savedInstanceState);
	}
	
	private void findView(View view) {
		mBody = (FrameLayout) view.findViewById(R.id.base_frame);
	}

	/**
	 * 返回键
	 * @return
	 */
	public boolean onBackPressed() {
        return false;
    };

    public void doing(){}

}
