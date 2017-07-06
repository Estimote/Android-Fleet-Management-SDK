package com.estimote.nfcstamps.presentation.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.nfcstamps.R;
import com.estimote.nfcstamps.SharedPrefHelper;
import com.estimote.nfcstamps.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.activity_profile_recycler_stamp)
    RecyclerView stampCollectionView;
    @Bind(R.id.activity_profile_stamps_left_info)
    TextView stampsLeftInfo;

    private StampCollectionAdapter stampCollectionAdapter;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        ButterKnife.bind(this);
        sharedPrefHelper = new SharedPrefHelper(this);
        setupStampCollectionView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStampsInfo();
    }

    private void setupStampCollectionView() {
        stampCollectionView.setLayoutManager(new GridLayoutManager(this, Utils.GRID_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        stampCollectionAdapter = new StampCollectionAdapter(this, 0);
        stampCollectionView.setAdapter(stampCollectionAdapter);
    }

    private void updateStampsInfo() {
        int stampsLeft = sharedPrefHelper.getStampAmountValue();
        stampCollectionAdapter.update(stampsLeft);
        setupStampLeftInfo(Utils.STAMPS_AMOUNT - stampsLeft);

    }

    private void setupStampLeftInfo(int stampsLeft) {
        if (stampsLeft <= 0) {
            stampsLeftInfo.setText(getString(R.string.activity_points_stamps_collected));
        } else {
            stampsLeftInfo.setText(getString(R.string.activity_points_stamps_left, stampsLeft));
        }
    }

    @OnClick(R.id.activity_profile_button)
    void onButtonClick() {
        if (sharedPrefHelper.getStampAmountValue() < Utils.STAMPS_AMOUNT) {
            Toast.makeText(this, getString(R.string.activity_profile_not_collected), Toast.LENGTH_LONG).show();
        } else {
            sharedPrefHelper.saveStampAmountValue(0);
            stampCollectionAdapter.update(sharedPrefHelper.getStampAmountValue());
        }
    }
}
