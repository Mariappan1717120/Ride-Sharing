package com.gbtrip.org.travel_share;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.widget.TextView;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.gbtrip.org.travel_share.Main_project.get_inst;
import static com.gbtrip.org.travel_share.Main_project.load_data;
import static com.google.android.libraries.places.internal.hy.f;

public class raccept extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        ResultCallback<Status> {

        private GoogleMap mMap;
        private GoogleApiClient client;
        private Location lastLocation;
        private LocationRequest locationRequest;
        private Marker currentLocationMarker,pickupMarker;
        public LatLng latLng, latLng1,destinationLatLng;
        private Marker pickMarker;
		private TextView mRiderName, mRiderPhone;
        Double checklat, checklon;
        private FirebaseAuth mAuth;
        private LatLng picklat;
        Float FinDist,distance;
        Float agecomp,ageset;
        String FinGender,FinAge,Destination;
        String destination;
        private FirebaseDatabase display,mdisplay;
		private Boolean requestBol = false;




    Marker mCurrLocationMarker;
        Button btn;

        Intent intent;

        public static final int REQUEST_LOCATION_CODE = 99;
        private static final int[] COLORS = new int[]{R.color.design_default_color_primary};


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            View view = inflater.inflate(R.layout.activity_raccept, null);
   
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            intent = new Intent(getActivity(), condition.class);
            mAuth = FirebaseAuth.getInstance();

           // mRiderName = (TextView) findViewById(R.id.RiderName);
			//mRiderPhone = (TextView) findViewById (R.id.RiderPhone);

            btn = (Button) view.findViewById(R.id.acceptbtn);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) 
				{
					if (requestBol){
                    endRide();


                }
			 else{
                   
                    String userrid = mAuth.getCurrentUser().getUid();
                    DatabaseReference reff = FirebaseDatabase.getInstance().getReference("accept");
                    GeoFire geoFire = new GeoFire(reff);
                    geoFire.setLocation(userrid, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Toast.makeText(getContext(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_LONG).show();
                            } else {

                            }
                        }
                    });
                    picklat = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    pickupMarker=mMap.addMarker(new MarkerOptions().position(picklat).title("pick"));
                    btn.setText("Getting your ride....");
                    Toast.makeText(getContext(), "geting", Toast.LENGTH_SHORT).show();
                    getClosestDriver();
                }
				}
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
			{
                checkLocationPermission();
            }
            return view;
        }
        private int radius = 1;
        private Boolean driverfound =false ;
        private String driverFoundID;
        GeoQuery geoQuery;
        private void getClosestDriver () {
            DatabaseReference shareloc = FirebaseDatabase.getInstance().getReference().child("Available");
            GeoFire geoFire = new GeoFire(shareloc);

            geoQuery = geoFire.queryAtLocation(new GeoLocation(picklat.latitude, picklat.longitude), radius);
            Toast.makeText(getContext(), "query", Toast.LENGTH_SHORT).show();
			geoQuery.removeAllListeners();

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override

                public void onKeyEntered(String key, GeoLocation location) {
                    Toast.makeText(getContext(), "loop", Toast.LENGTH_SHORT).show();
                    if (!driverfound && requestBol) {
                        driverfound = true;
                        driverFoundID = key;
                        DatabaseReference driveref = FirebaseDatabase.getInstance().getReference().child(driverFoundID);
                        String customerId = mAuth.getCurrentUser().getUid();
                        HashMap map = new HashMap();
                        map.put("customerId is", customerId);map.put("destination", destination);
                        map.put("destinationLat", destinationLatLng.latitude);
                        map.put("destinationLng", destinationLatLng.longitude);
						
                        driveref.updateChildren(map);
                        Toast.makeText(getContext(), "hmmm", Toast.LENGTH_SHORT).show();
                        getDriverLocation();
						getDriverInfo();
                        getHasRideEnded();
						
                        btn.setText("Looking for Rider Location...");
                    }
                }
                @Override
                public void onKeyExited(String key) {
                    Toast.makeText(getContext(), "NO driver found", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                    if (!driverfound) {
                        radius++;
                        getClosestDriver();
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
			});
		}

        private Marker Ridermarker;
		private DatabaseReference driverlocref;
        private ValueEventListener driverLocationRefListener;
        private void getDriverLocation () {
            Toast.makeText(getContext(), "this toast", Toast.LENGTH_SHORT).show();
            driverlocref = FirebaseDatabase.getInstance().getReference().child("Working").child(driverFoundID).child("l");
            driverLocationRefListener =driverlocref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(getContext(), "driverrr", Toast.LENGTH_SHORT).show();
                        List<Object> map = (List<Object>) dataSnapshot.getValue();
                        double locationlat = 0;
                        double locationlong = 0;
                        btn.setText("Rider Found....");
                        if (map.get(0) != null) {
                            locationlat = Double.parseDouble(map.get(0).toString());
                        }

                        if (map.get(1) != null) {
                            locationlong = Double.parseDouble(map.get(1).toString());
                        }
                        LatLng driverlatlng = new LatLng(locationlat, locationlong);

                        if (Ridermarker != null) {
                            Ridermarker.remove();
                        }
                        Location loc1 = new Location("");
                        loc1.setLatitude(picklat.latitude);
                        loc1.setLongitude(picklat.longitude);


                        Location loc2 = new Location("");
                        loc2.setLatitude(driverlatlng.latitude);
                        loc2.setLongitude(driverlatlng.longitude);

                        distance = loc1.distanceTo(loc2);
                        if (distance<100){
                        btn.setText("Driver's Here");
                    }else{
                        btn.setText("Driver Found: " + String.valueOf(distance));
                    }



                    Ridermarker = mMap.addMarker(new MarkerOptions().position(driverlatlng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

            }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
		
		private void getDriverInfo(){
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("username")!=null){
                        mRiderName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("userphone")!=null){
                        mRiderPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
     private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        driverlocref.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverfound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (Ridermarker != null){
            Ridermarker.remove();
        }
        btn.setText("accept");
        mRiderName.setText("");
        mRiderName.setText("");
    }


       
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            switch (requestCode) {
                case REQUEST_LOCATION_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            if (client == null) {
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_LONG).show();
                    }
                    return;
            }
        }

        @Override
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        protected synchronized void buildGoogleApiClient () {

            client = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            client.connect();
        }


        @Override
        public void onConnected (@Nullable Bundle bundle){
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
            }

        }

        public boolean checkLocationPermission () {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
                }
                return false;
            } else {
                return false;
            }
        }


        @Override
        public void onConnectionSuspended ( int i){

        }

        @Override
        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){

        }

        @Override
        public void onLocationChanged (Location location){
            lastLocation = location;
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            checklat = latLng.latitude;
            checklon = latLng.longitude;
            latLng1 = new LatLng(checklat, checklon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng1);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        }

        @Override
        public boolean onMarkerClick (Marker marker){
            return false;
        }

        @Override
        public void onMarkerDragStart (Marker marker){

        }

        @Override
        public void onMarkerDrag (Marker marker){

        }

        @Override
        public void onMarkerDragEnd (Marker marker){

        }

        @Override
        public void onResult (@NonNull Status status){

        }

        @Override
        public void onStop () {
            super.onStop();

        }
    }
		
