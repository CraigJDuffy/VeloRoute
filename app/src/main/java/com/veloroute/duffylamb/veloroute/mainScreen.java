package com.veloroute.duffylamb.veloroute;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapzen.android.graphics.MapFragment;
import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.OnMapReadyCallback;
import com.mapzen.android.graphics.model.CinnabarStyle;
import com.mapzen.tangram.LngLat;



public class mainScreen extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private MapzenMap map;
    private boolean enableLocationOnResume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        final MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapzenMap map) {
                mainScreen.this.map = map;
                map.setRotation(0f);
                map.setZoom(0);
                map.setTilt(0f);
                map.setPosition(new LngLat(0,0));
                map.setZoomButtonsEnabled(true);
                map.setCompassButtonEnabled(true);
                checkRuntimePermissions();
                map.setStyle(new CinnabarStyle());

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map.isMyLocationEnabled()) {
            map.setMyLocationEnabled(false);
            enableLocationOnResume = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (enableLocationOnResume) {
            map.setMyLocationEnabled(true);
        }
    }

    private boolean permissionNotGranted() { // Returns true if permission is not granted #TheArtOftheBodge
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSIONS_REQUEST_CODE);
    }

    public void checkRuntimePermissions() {
        if (permissionNotGranted()) {
            requestPermission();
        }
        else {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        for(int i = 0; i<grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED)
                return;
        }
        map.setMyLocationEnabled(true);
    }


}






