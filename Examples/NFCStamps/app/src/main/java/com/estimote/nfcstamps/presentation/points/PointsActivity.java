package com.estimote.nfcstamps.presentation.points;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.estimote.nfcstamps.R;
import com.estimote.nfcstamps.SharedPrefHelper;
import com.estimote.nfcstamps.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PointsActivity extends AppCompatActivity {

    @Bind(R.id.activity_points_stamps_left)
    TextView stampsLeftInfo;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        sharedPrefHelper = new SharedPrefHelper(this);
        sharedPrefHelper.increaseStampAmount();
        ButterKnife.bind(this);
        setStampsLeftInfo();
    }

    private void setStampsLeftInfo() {
        int stampsLeft = Utils.STAMPS_AMOUNT - sharedPrefHelper.getStampAmountValue();
        stampsLeftInfo.setText(stampsLeft <= 0 ? getString(R.string.activity_points_stamps_collected) : getString(R.string.activity_points_stamps_left, stampsLeft));
    }
}
