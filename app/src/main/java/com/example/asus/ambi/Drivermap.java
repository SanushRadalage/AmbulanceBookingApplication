package com.example.asus.ambi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Drivermap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    ImageView mLogout, mprofile;

    Button mridestatus;

    private String patientId = "", destination;

    private LatLng destinationLatLng;

    private static final int REQUEST_CODE = 102;

    private Boolean isLogout = false;

    int status = 0;

    private SupportMapFragment mapFragment;
    private RelativeLayout pinfo;
    private ImageView pprofI, hide;
    private TextView pnm, mdesti, cn, blood;
    private FloatingActionButton flt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivermap);

        polylines = new ArrayList<>();

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
// ask permissions here using below code

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mdesti = findViewById(R.id.patientdestination);
        pinfo = findViewById(R.id.pinfo);
        pprofI = (ImageView) findViewById(R.id.pprofileimage);
        pnm = (TextView) findViewById(R.id.name);
        cn = findViewById(R.id.phone);
        mridestatus = findViewById(R.id.ridestatus);
        hide = findViewById(R.id.hide);
        flt = findViewById(R.id.flt);
        blood = findViewById(R.id.blood);

        final Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.open);
        final Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.clock);
        final Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.inversclock);
        final Animation animation4 = AnimationUtils.loadAnimation(this, R.anim.close);


        flt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mprofile.getVisibility() == View.VISIBLE && mLogout.getVisibility() == View.VISIBLE)
                {
                    flt.setAnimation(animation3);
                    mprofile.setAnimation(animation1);
                    mLogout.setAnimation(animation1);
                    mprofile.setVisibility(View.INVISIBLE);
                    mLogout.setVisibility(View.INVISIBLE);

                }
                else
                {
                    flt.setAnimation(animation2);
                    mprofile.setAnimation(animation4);
                    mprofile.setAnimation(animation4);
                    mprofile.setVisibility(View.VISIBLE);
                    mLogout.setVisibility(View.VISIBLE);
                }
            }

        });

        //case 1 p is wait for driver & 2 is driver is on the destination with p

        mridestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Drivermap.this, "fuck", Toast.LENGTH_SHORT).show();
                switch (status) {
                    case 1:
                        status = 2;
                        erasePolylines();
                        if (destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0) {
                            getRouteToMarker(destinationLatLng);
                        }
                        mridestatus.setText("Drive Completed");
                        break;

                    case 2:
                        recordRide();
                        endRide();
                        break;
                }
            }
        });


        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pinfo.getVisibility() == View.VISIBLE) {
                    pinfo.setVisibility(View.INVISIBLE);
                } else
                    pinfo.setVisibility(View.VISIBLE);

            }
        });


        mprofile = findViewById(R.id.profile);
        mprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drivermap.this, driverprofile.class);
                startActivity(intent);
                return;
            }
        });

        mLogout = findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogout = true;
                disconnectdr();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Drivermap.this, main.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        getAssignedCustomer();
    }



    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(driverId).child("PatientRequest").child("PatientRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status = 1;
                    patientId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerinfo();
                }else{
                    endRide();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;

    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("PatientRequest").child(patientId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !patientId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng pickupLatLang = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLang).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.mask)));
                    getRouteToMarker(pickupLatLang);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getRouteToMarker(LatLng pickupLatLang) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLang)
                .key("AIzaSyBCsZm3RAYHd8Un8BiItCIjLGhc2wE4z38")
                .build();
        routing.execute();
    }

    private void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(driverId).child("PatientRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map.get("Destination") != null)
                    {
                        destination = map.get("Destination").toString();
                        mdesti.setText("Destination" + destination);
                    }
                    else{
                        mdesti.setText("Destination : -- ");
                    }

                    Double deslat = 0.0;
                    Double deslng = 0.0;
                    if(map.get("DestinationLat") != null)
                    {
                        deslat = Double.valueOf(map.get("DestinationLat").toString());
                    }
                    if(map.get("DestinationLng") != null)
                    {
                        deslng = Double.valueOf(map.get("DestinationLng").toString());
                        destinationLatLng = new LatLng(deslat, deslng);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void endRide()
    {
        mridestatus.setText("Picked Patient");
        erasePolylines();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(userId).child("PatientRequest");
            driverRef.removeValue();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PatientRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(patientId);
        patientId = "";

        if(pickupMarker != null){
            pickupMarker.remove();
        }

        if (assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        pinfo.setVisibility(View.GONE);
        pnm.setText("");
        cn.setText("");
        blood.setText("");
        mdesti.setText("Destination: --");
        pprofI.setImageResource(R.mipmap.prof);

    }

    //ratings

    private void recordRide()
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(userId).child("History");
        DatabaseReference patientref = FirebaseDatabase.getInstance().getReference().child("Users").child("Patient").child(patientId).child("History");
        DatabaseReference historyref = FirebaseDatabase.getInstance().getReference().child("History");

        String requestId = historyref.push().getKey();

        driverRef.child(requestId).setValue(true);
        patientref.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("Driver", userId);
        map.put("Patient", patientId);
        map.put("Rating", 0);

        historyref.child(requestId).updateChildren(map);
    }

    private void getAssignedCustomerinfo()
    {
        pinfo.setVisibility(View.VISIBLE);
        DatabaseReference pdatab = FirebaseDatabase.getInstance().getReference().child("Users").child("Patient").child(patientId);
        pdatab.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null)
                    {
                        pnm.setText(map.get("Name").toString());
                    }
                    if (map.get("ContactNumber") != null)
                    {
                        cn.setText(map.get("ContactNumber").toString());
                    }
                    if(map.get("profileImageUrl") != null)
                    {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(pprofI);
                    }
                    if (map.get("BloodGroup") != null)
                    {
                        blood.setText(map.get("BloodGroup").toString());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("drivermap", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("drivermap", "Can't find style. Error: ", e);
        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null){

            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("DriversAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("DriversWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (patientId){
                case "":
                    geoFireWorking.removeLocation(userId);
                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;

                default:
                    geoFireAvailable.removeLocation(userId);
                    geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
            }






        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void disconnectdr()
    {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isLogout){
            disconnectdr();
        }

    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {

            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRoutingCancelled() {

    }

    private  void erasePolylines()
    {
        for(Polyline line : polylines)
        {
            line.remove();
        }
        polylines.clear();
    }
}
