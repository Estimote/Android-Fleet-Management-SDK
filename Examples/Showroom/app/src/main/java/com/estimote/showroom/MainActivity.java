package com.estimote.showroom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.showroom.estimote.NearableID;
import com.estimote.showroom.estimote.Product;
import com.estimote.showroom.estimote.ShowroomManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ShowroomManager showroomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<NearableID, Product> products = new HashMap<>();
        // TODO: replace with identifiers of your own nearables
        products.put(new NearableID("abcdef0000000001"), new Product("Sweeft Headphones",
                "These super cool headphones will make sure you'll re-discover your favorite Taylor Swift song anew. You just can't put a price tag on that!"));
        products.put(new NearableID("abcdef0000000002"), new Product("Nyan Bicycle 3x14",
                "Rush down the local streets with this amazing bike, leaving a trail of rainbow behind you, to the awe of everyone around."));
        showroomManager = new ShowroomManager(this, products);
        showroomManager.setListener(new ShowroomManager.Listener() {
            @Override
            public void onProductPickup(Product product) {
                ((TextView) findViewById(R.id.titleLabel)).setText(product.getName());
                ((TextView) findViewById(R.id.descriptionLabel)).setText(product.getSummary());
                findViewById(R.id.descriptionLabel).setVisibility(View.VISIBLE);
            }
            @Override
            public void onProductPutdown(Product product) {
                ((TextView) findViewById(R.id.titleLabel)).setText("Pick up an object to learn more about it");
                findViewById(R.id.descriptionLabel).setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ShowroomManager updates");
            showroomManager.startUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ShowroomManager updates");
        showroomManager.stopUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showroomManager.destroy();
    }
}
