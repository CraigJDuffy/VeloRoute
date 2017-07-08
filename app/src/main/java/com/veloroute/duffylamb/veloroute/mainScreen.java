package com.veloroute.duffylamb.veloroute;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mapzen.android.graphics.MapFragment;
import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.MapzenMapPeliasLocationProvider;
import com.mapzen.android.graphics.OnMapReadyCallback;
import com.mapzen.android.graphics.model.CinnabarStyle;
import com.mapzen.android.search.MapzenSearch;
import com.mapzen.model.ValhallaLocation;
import com.mapzen.pelias.gson.Feature;
import com.mapzen.pelias.gson.Result;
import com.mapzen.pelias.widget.AutoCompleteAdapter;
import com.mapzen.pelias.widget.AutoCompleteListView;
import com.mapzen.pelias.widget.PeliasSearchView;
import com.mapzen.tangram.LngLat;
import com.mapzen.valhalla.Route;
import com.mapzen.valhalla.RouteCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class mainScreen extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private MapzenMap map;
    private boolean enableLocationOnResume = false;

    private AutoCompleteListView listView;
    private MapzenMapPeliasLocationProvider peliasLocationProvider;

    private RoutePlanner routePlanner;
    private Button BtnSet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        listView = (AutoCompleteListView) findViewById(R.id.list_view);
        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(autoCompleteAdapter);
        peliasLocationProvider = new MapzenMapPeliasLocationProvider(this);

        final MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapzenMap map) {

                mainScreen.this.map = map;
                map.setRotation(0f);
                map.setZoom(0);
                map.setTilt(0f);
                map.setPosition(new LngLat(51.476852, -0.000500));
                map.setZoomButtonsEnabled(true);
                map.setCompassButtonEnabled(true);


                checkLocationPermissions();
                map.setStyle(new CinnabarStyle());

                peliasLocationProvider.setMapzenMap(map);
                routePlanner.setMap(map);
            }
        });


        setupSearchView();
        setupRoutePlanner();
        BtnSet = (Button) findViewById(R.id.btn_set);

        BtnSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mainScreen.this, settingsScreen.class);
                startActivity(myIntent);
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

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



    private void setupSearchView() {

        MapzenSearch mapzenSearch = new MapzenSearch(this);
        mapzenSearch.setLocationProvider(peliasLocationProvider);
        PeliasSearchView searchView = (PeliasSearchView) findViewById(R.id.pelias_search_view);

        searchView.setAutoCompleteListView(listView);
        searchView.setPelias(mapzenSearch.getPelias());
        searchView.setCallback(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                map.clearSearchResults();
                for (Feature feature : response.body().getFeatures()) {
                    showSearchResult(feature);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(this.getString(R.string.search_hint));

        searchView.setOnPeliasFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    BtnSet.setVisibility(View.INVISIBLE);
                    routePlanner.smartHide();
                } else {
                    BtnSet.setVisibility(View.VISIBLE);
                    routePlanner.smartShow();
                }
            }
        });
    }

    //CommentLineSoIcanCommit
    private void setupRoutePlanner() {

        this.routePlanner = new RoutePlanner((LinearLayout) findViewById(R.id.route_planner));
        routePlanner.setRouteCallback(new RouteCallback() {
            @Override
            public void success(@NotNull Route route) {

                List<LngLat> coordinates = new ArrayList<>();
                for (ValhallaLocation location : route.getGeometry()) {
                    coordinates.add(new LngLat(location.getLongitude(), location.getLatitude()));
                }
                map.drawRouteLine(coordinates);

            }

            @Override
            public void failure(int i) {
                // TODO: 02/07/2017
                Log.e("Router Callback", "failure: " + Integer.toString(i));
            }
        });

    }


    private void showSearchResult(Feature feature) {

        LngLat point = new LngLat(feature.geometry.coordinates.get(0), feature.geometry.coordinates.get(1));
        map.setPosition(point);
        map.clearSearchResults();
        map.drawSearchResult(point);
        map.setZoom(16);
        routePlanner.searchResult(feature);
    }

    public void checkLocationPermissions() {

        if (hasLocationPermission()) {
            map.setMyLocationEnabled(true);
        } else {
            requestPermission();
        }

    }

    private boolean hasLocationPermission() {

        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        map.setMyLocationEnabled(true);
    }


}






