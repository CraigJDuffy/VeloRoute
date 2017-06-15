package com.veloroute.duffylamb.veloroute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mapzen.android.graphics.MapFragment;
import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.OnMapReadyCallback;
import com.mapzen.tangram.LngLat;


public class mainScreen extends AppCompatActivity {

    MapzenMap map;
    /**
     * To conserve resources, {@link MapzenMap#setMyLocationEnabled} is set to false when
     * the activity is paused and re-enabled when the activity resumes.
     */
    private boolean enableLocationOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        final MapFragment mapFragment =
                (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapzenMap map) {
                map.setPosition(new LngLat(-3.183814, 55.958723));
                map.setRotation(0f);
                map.setZoom(17f);
                map.setTilt(0f);

            }
        });
    }

    @Override protected void onPause() {
        super.onPause();
        if (map != null && map.isMyLocationEnabled()) {
            map.setMyLocationEnabled(false);
        }
    }

    private void configureMap(boolean enabled) {
        if (enabled) {
            setMyLocationEnabled(true);
        }
        map.setFindMeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mainScreen.this, R.string.change_location, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        map.setCompassButtonEnabled(true);
        map.setZoomButtonsEnabled(true);
        map.setPersistMapState(true);
    }

    private void setMyLocationEnabled(boolean enabled) {
        map.setMyLocationEnabled(enabled);
        enableLocationOnResume = enabled;
    }
}






