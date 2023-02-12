package com.example.socializer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.socializer.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback,GpsListener{

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQ_CODE=101;
    FrameLayout map;
    Button search,profile,chat;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        map = findViewById(R.id.map);
        search=(Button) findViewById(R.id.search);
        chat=(Button) findViewById(R.id.chat);
        profile=(Button) findViewById(R.id.profile);

        firebaseDatabase =FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        //to get user current location
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,SearchActivity.class);

                startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ChatActivity.class);

                startActivity(intent);

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);

                startActivity(intent);

            }
        });
    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_CODE);
            return;
        }

        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check if gps enabled
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //get location

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }else{
            //enable GPS
            Toast.makeText(this, "Enable GPS services", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        Task<Location> task=fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation=location;
                    SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment!=null;
                    supportMapFragment.getMapAsync(HomeActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng currLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions= new MarkerOptions().position(currLatLng).title("user location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currLatLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng,15));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQ_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //update location in db


    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnables(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}