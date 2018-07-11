package cn.com.broadlink.blappsdkdemo.activity.Family;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.family.params.BLFamilyAllInfo;

public class FamilyAddActivity extends TitleActivity implements FamilyInterface {

    private InputTextView mFamilyNameView;
    private Button mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_add);
        setTitle(R.string.str_create_family);
        setBackWhiteVisible();

        findView();
        setListener();

        BLLocalFamilyManager.getInstance().setFamilyInterface(this);
    }

    private void findView() {
        mFamilyNameView = (InputTextView) findViewById(R.id.family_name_view);
        mAddBtn = (Button) findViewById(R.id.btn_add_family);
    }

    private void setListener() {
        mFamilyNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddBtn.setEnabled(s.length() > 0);
            }
        });

        mAddBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                showProgressDialog(getResources().getString(R.string.loading));
                String name = mFamilyNameView.getTextString();
                BLLocalFamilyManager.getInstance().createDefaultFamily(name);
            }
        });
    }

    @Override
    public void familyInfoChanged(Boolean isChanged, String familyId, String familyVersion) {
        dismissProgressDialog();
        if (isChanged) {
            FamilyAddActivity.this.finish();
        }
    }

    @Override
    public void familyAllInfo(BLFamilyAllInfo allInfo) {

    }
}
