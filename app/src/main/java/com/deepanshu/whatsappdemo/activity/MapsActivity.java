package com.deepanshu.whatsappdemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.deepanshu.whatsappdemo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {
    SupportMapFragment mapFragment;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    Intent intentThatCalled;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    private GoogleMap googleMap;
    public String bestProvider;
    public Button userLocation, shareLocationBtn;
    public EditText placeText, MobileNO;
    private static final String GOOGLE_API_KEY = "AIzaSSDFSDF8Kv2eP0PM8adf5dSDFysdfas323SD3HA";
    private int PROXIMITY_RADIUS = 5000;
    private SmsManager smsManager;
    private String mobNo;
    StringBuffer smsBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        userLocation = findViewById(R.id.userLocation);
        MobileNO = findViewById(R.id.MobileNO);
        shareLocationBtn = findViewById(R.id.shareLocationBtn);
        shareLocationBtn.setOnClickListener(this);

        placeText = (EditText) findViewById(R.id.placeText);


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationSettings();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocation();
        userLocation.setOnClickListener(this);


    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    private void showLocationSettings() {
        /*Snackbar snackbar = Snackbar
                .make(mainCoordinatorLayout, "Location Error: GPS Disabled!",
                        Snackbar.LENGTH_LONG)
                .setAction("Enable", new View.OnClickListener() {
                    @Override                    public void onClick(View v) {

                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
   */
    }


    protected void getLocation() {
        if (isLocationEnabled(MapsActivity.this)) {
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(MapsActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                //searchNearestPlace(voice2text);
            } else {
                //This is what you need:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                    return;
                }

                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        } else {
            //prompt user to enable location....
            //.................
        }
    }


    private boolean isLocationEnabled(MapsActivity mapsActivity) {
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("I am here !..").snippet("....").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                break;
            case 111:
                if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    smsManager.sendTextMessage(mobNo, null, smsBody.toString(), null, null);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(this);

    }


    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Toast.makeText(MapsActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        //searchNearestPlace(voice2text);

        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //loadNearByPlaces(latitude, longitude);

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLocation:
                String type = placeText.getText().toString();
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&types=" + type);
                googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
                Toast.makeText(this, "search btn click", Toast.LENGTH_SHORT).show();

                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[2];
                toPass[0] = googleMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);
                break;
            case R.id.shareLocationBtn:
                /* it is used for sending the sms messagenger ..
                if (MobileNO.getText() != null && MobileNO.getText().length()>0) {
                    mobNo= MobileNO.getText().toString().trim();
                    smsManager= SmsManager.getDefault();
                    smsBody = new StringBuffer();
                    smsBody.append("Hey, this is my current location just click and see where i am now...");
                    smsBody.append("http://maps.google.com?q=");
                    smsBody.append( latitude);
                    smsBody.append(",");
                    smsBody.append(longitude);
                    if(ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED)
                    {smsManager.sendTextMessage(mobNo, null, smsBody.toString(), null, null);
                        Toast.makeText(this, "Message Sent successfully....", Toast.LENGTH_SHORT).show();}
                    else {
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},111);
                    }
                }
                else {
                */
                    String uri = "http://maps.google.com?q=" +latitude+","+longitude;
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String ShareSub = "Here is my location";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));


                break;


        }

    }
}