package com.example.myvnu;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.admin.SystemUpdatePolicy;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.myvnu.databinding.ActivityMapsBinding;
import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.example.myvnu.databinding.ActivityMaps2Binding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap=null;
    private @NonNull ActivityMapsBinding binding;
    private DBAction dbAction = new DBAction();
    private String flag = "Pended";
    private Marker destMarker = null;
    private Marker startMarker = null;
    private Marker selectedMarker = null;
    private List<Marker> pendedMarkers = new ArrayList<Marker>();
    private List<Marker> placeMarkers = new ArrayList<Marker>();
    private List<Marker> customPlaceMarkers = new ArrayList<Marker>();
    private SearchView searchView;
    private ImageButton btnGetGPS;
    private ImageButton btnCheckIn;
    private ImageButton btnGetVNU;
    private ImageButton btnHome;
    private Button btnTT;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private List<CustomPlace> customPlaces;
    private CustomPlace customPlace;
    private List<Place> places;
    private Place place;
    private Bitmap bitmap;
    private LatLng vnuhcm;
    private LinearLayout cameraLayout;


    private String pictureImagePath;
    private LatLng currentPos;
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        setSearchView();
        setGPS();
        setCheckIn();
        setHome();
        setVNU();
        btnTT = (Button)findViewById(R.id.btnTT);
        //goToPos();
        mapFragment.getMapAsync(this);
    }

    private void setVNU() {
        btnGetVNU = (ImageButton) findViewById(R.id.btnGetVNU);
        btnGetVNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(MapsActivity.this, "Về Làng Đại Học", Toast.LENGTH_SHORT);
                toast.show();
                goToVNU();
            }
        });
    }

    private void setHome() {
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(MapsActivity.this, "Về trang chủ", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setGPS() {
        btnGetGPS = (ImageButton)findViewById(R.id.btnGetGPS);
        btnGetGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(MapsActivity.this, "Vị trí hiện tại", Toast.LENGTH_SHORT);
                toast.show();
                goToPos();
            }
        });
    }

    private void setCheckIn(){
        btnCheckIn = (ImageButton) findViewById(R.id.btnCheckIn);
        cameraLayout = (LinearLayout)findViewById(R.id.cameraLayout);
        initLaucher();

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                cameraLayout.setVisibility(View.VISIBLE);
            }
        });

    }
    public void exitOption(View view){
        cameraLayout.setVisibility(View.INVISIBLE);
    }
    public void arCamera(View view){
        Intent intent = new Intent(MapsActivity.this, ArCameraActivity.class);
        Log.d("huheo", "ar camera");
        startActivity(intent);
    }
    public void camera(View view){
        pictureImagePath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.TMP_IMAGE_FILE;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgFile = new File(pictureImagePath);
        Log.d("huheo", "camera");
        Uri uri = FileProvider.getUriForFile(MapsActivity.this, BuildConfig.APPLICATION_ID + ".provider", imgFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activityResultLauncher.launch(cameraIntent);
    }
    public void initLaucher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //if(imgFile.exists()){
                            Intent intent = new Intent(MapsActivity.this, AddNewPlaceActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putString("CNTN19", pictureImagePath);
                            bundle.putDouble("lat", selectedMarker.getPosition().latitude);
                            bundle.putDouble("lng", selectedMarker.getPosition().longitude);
                            intent.putExtra("BUNDLE", bundle);
                            startActivity(intent);

                            //}
                        }
                    }
                }
        );
    }



    private void goToPos(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        try{
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address addr = addresses.get(0);
                            currentPos = new LatLng(addr.getLatitude(), addr.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentPos).title(addr.getLocality()));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(lls.get(0)));
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 12));
                            gotoPosition(currentPos.latitude, currentPos.longitude);
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        else{
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap!=null) getAction();
    }



    private void setSearchView() {
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                removeAllPendMarker();
                String location = searchView.getQuery().toString();
                Log.d("heoheo location", location);
                List<CustomPlace> customPlaces = null;
                List<Address> addresses=null;
                List<Place> places=null;
                List<LatLng>lls = new ArrayList<LatLng>();
                if(location!=null || !location.equals("")){
                    DBAction dbAction = new DBAction();
                    places = dbAction.findPlaceWithTitle(MapsActivity.this, location);
                    customPlaces = dbAction.findCustomPlaceWithTitle(MapsActivity.this, location);
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    Log.d("heoheo number of places", Integer.toString(places.size()));
                    try{
                        addresses = geocoder.getFromLocationName(location, 5);
                        Log.d("heoheo number of addresses", Integer.toString(addresses.size()));
                    } catch (IOException e) {
                        Log.d("heoheo failed", "failed");
                        e.printStackTrace();
                    }
                    for(int i = 0; i < places.size(); ++i) {
                        Place tmp = places.get(i);
                        lls.add(new LatLng(tmp.getLat(), tmp.getLng()));
                    }

                    for(int i = 0; i < customPlaces.size(); ++i) {
                        CustomPlace tmp = customPlaces.get(i);
                        lls.add(new LatLng(tmp.getLat(), tmp.getLng()));
                    }

                    for(int i = 0; i < addresses.size(); ++i) {
                        Address tmp = addresses.get(i);
                        lls.add(new LatLng(tmp.getLatitude(), tmp.getLongitude()));
                    }
                    if(lls.size() > 0) {
                        //mMap.addMarker(new MarkerOptions().position(lls.get(0)).title(location));
                        for(int i = 0; i < lls.size(); ++i){
                            pendedMarkers.add(addFlagMarkerOnMap(lls.get(i), "Pended"));
                        }
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(lls.get(0)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lls.get(0), 15));
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    private void getAction() {
        Bundle bundle = getIntent().getBundleExtra("data");
        if(bundle != null) {
            double lat = bundle.getDouble("lat", 0.1);
            double lng = bundle.getDouble("lng", 0.1);
            gotoPosition(lat, lng);
        }
        else goToPos();
    }
    private void goToVNU() {
        //gotoPosition(10.8785166,106.7959034);
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(vnuhcm, 12));
    }

    private void gotoPosition(double lat, double lng) {
        LatLng position = new LatLng(lat,lng);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        if(selectedMarker!=null)
            selectedMarker.remove();
        selectedMarker = addFlagMarkerOnMap(position,"Selected");
        if(btnTT.getVisibility() == View.VISIBLE) btnTT.setVisibility(View.INVISIBLE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        // Add a marker in HCMUS and move + zoom the camera
        vnuhcm = new LatLng(10.8693508, 106.7962367);
        addCustomPlaceMarkers();
        addPlaceMarkers();
        getAction();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMapClickListener(this);
    }

    private void addPlaceMarkers() {
        //addMarkerOnMap(10.8756461,106.7991699, "Trường Đại học Khoa học Tự nhiên", "hcmus", false);
        //addMarkerOnMap(10.8750095,106.8012331, "Nhà văn hoá sinh viên - Đại học Quốc gia TPHCM", "nvhsv", false);
        //addMarkerOnMap(10.8880703,106.7891710, "Kí túc xá khu B - Đại học Quốc gia TPHCM", "ktxb", false);
        //addMarkerOnMap(10.8797654,106.7951268, "Nhà khách Đại học Quốc gia TPHCM","nhakhach", false);
        places = dbAction.getAllDefaultPlaces(MapsActivity.this);
        System.out.println(places.size());
        Iterator<Place> iter = places.iterator();

        while (iter.hasNext()){
            place = iter.next();
            System.out.println(place.getImg());
            bitmap = loadBitmapFromAsset(place.getImg());
            if (bitmap == null){
                continue;
            }
            double lat = place.getLat();
            double lng = place.getLng();
            String title = place.getTitle();
            placeMarkers.add(addMarkerOnMap(lat,lng,title,false));

        }
    }

    private void addCustomPlaceMarkers() {
        customPlaces = dbAction.getAllCustomPlaces(MapsActivity.this);
        Iterator<CustomPlace> iter = customPlaces.iterator();

        while (iter.hasNext()){
            customPlace = iter.next();
            bitmap = loadImage(customPlace.getImg());
            if (bitmap == null){
                continue;
            }
            double lat = customPlace.getLat();
            double lng = customPlace.getLng();
            String title = customPlace.getTitle();
            customPlaceMarkers.add(addMarkerOnMap(lat,lng,title,true));
        }
    }

    private Marker addMarkerOnMap(double lat, double lng, String title, Boolean isCustomPlace) {
        bitmap = makeMarkerBitmap(isCustomPlace);
        LatLng position = new LatLng(lat,lng);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .alpha(1f)
                .visible(true)
                .draggable(false);
        if (!isCustomPlace)
            markerOptions.rotation(180);

        Marker marker = mMap.addMarker(markerOptions);

        if(isCustomPlace) marker.setTag("CustomPlace");
        else marker.setTag("Place");
        return marker;
    }

    private Bitmap makeMarkerBitmap(Boolean isCustomPlace) {
        bitmap = resizeBitmap(bitmap, 200,200);
        bitmap = getCroppedBitmap();
        if (!isCustomPlace)
            bitmap = RotateBitmap(bitmap);

        String markerTempFile;
        if (isCustomPlace)
            markerTempFile = "yellow_marker_temp";
        else
            markerTempFile = "blue_marker_temp";
        Bitmap markerBitmap = loadBitmapFromDrawable(markerTempFile);
        Bitmap markerTemp = resizeBitmap(markerBitmap,250,337);

        //Bitmap markerBitmap = Bitmap.createBitmap(markerTemp.getWidth(), markerTemp.getHeight(), markerTemp.getConfig());
        //Canvas canvas = new Canvas(markerBitmap);
        Canvas canvas = new Canvas(markerTemp);
        Paint paint = new Paint();
        //canvas.drawBitmap(markerTemp,0,0,paint);
        canvas.drawBitmap(bitmap,25,25,null);
        return markerTemp;
    }

    public static Bitmap RotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(180f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap getCroppedBitmap() {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public Bitmap loadImage(String imgName){
        String fullPath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.IMAGE_FOLDER + imgName;
        try {
            Bitmap b = BitmapFactory.decodeFile(fullPath);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Bitmap loadBitmapFromAsset(String fileName){
        try{
            bitmap = BitmapFactory.decodeFile(getApplicationContext().getCacheDir().getAbsolutePath() + "/places/" + fileName);
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap loadBitmapFromDrawable(String markerTempFile) {
        return BitmapFactory.decodeResource(getResources(),
                getResources().getIdentifier(markerTempFile, "drawable", getPackageName()));
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height){
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private void moveSelectedMarker(LatLng pos){
        if(selectedMarker!=null)
            selectedMarker.remove();
        selectedMarker = addFlagMarkerOnMap(pos,"Selected");
        btnTT.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        focusPosition(marker.getPosition());
        String tag = (String)marker.getTag();
        LatLng pos = marker.getPosition();
        double lat = pos.latitude;
        double lng = pos.longitude;
        if (tag == "CustomPlace") {
            moveSelectedMarker(pos);
            btnTT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapsActivity.this, CustomPlaceActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("item", dbAction.findCustomPlaceWithLatLng(MapsActivity.this, lat, lng));
                    intent.putExtra("data", data);
                    startActivity(intent);
                }
            });

        }
        else if (tag == "Place") {
            moveSelectedMarker(pos);
            btnTT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapsActivity.this, PlaceActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("item", dbAction.findPlaceWithLatLng(MapsActivity.this, lat, lng));
                    intent.putExtra("data", data);
                    startActivity(intent);
                }
            });

        } else {

        }
        return false;
    }

    private void focusPosition(LatLng position) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Marker marker = addFlagMarkerOnMap(latLng,flag);
        if (flag == "Dest") {
            if(destMarker!=null)
                destMarker.remove();
            destMarker = marker;
        } else if (flag == "Start") {
            if(startMarker!=null)
                startMarker.remove();
            startMarker = marker;
        } else {
            pendedMarkers.add(marker);
        }
    }

    private Marker addFlagMarkerOnMap(LatLng position, String tag) {
        String imageName;
        if (tag == "Dest")
            imageName = "red_flag";
        else if (tag == "Start")
            imageName = "green_flag";
        else if (tag == "Pended")
            imageName = "yellow_flag";
        else
            imageName = "profile";
        bitmap = loadBitmapFromDrawable(imageName);
        bitmap = resizeBitmap(bitmap, 180, 165);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .alpha(1f)
                .visible(true)
                .draggable(true);


        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(tag);
        return marker;
    }

    @Override
    public void onCameraChange(@NonNull CameraPosition cameraPosition) {
        showPlaceMarkerByZoomLevel();
    }

    private void showPlaceMarkerByZoomLevel() {
        float zoomLevel = mMap.getCameraPosition().zoom;
        for (Marker marker:placeMarkers) {
            Place place = dbAction.findPlaceWithLatLng(MapsActivity.this,
                    marker.getPosition().latitude, marker.getPosition().longitude);
            System.out.println(place.getTitle() + Integer.toString(place.getMinZoom()));
            if (place.getMinZoom() <= zoomLevel * 4 && zoomLevel * 4 <= place.getMaxZoom()) {
                marker.setVisible(true);
            }
            else marker.setVisible(false);

        }
    }

    private void removeAllPendMarker() {
        for (Marker marker : pendedMarkers) {
            marker.remove();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(selectedMarker!=null)
            selectedMarker.remove();
        selectedMarker = addFlagMarkerOnMap(latLng,"Selected");
        if(btnTT.getVisibility() == View.VISIBLE) btnTT.setVisibility(View.INVISIBLE);
    }
    public void convert2Voice(View view){
        Toast toast = Toast.makeText(MapsActivity.this, "Khám phá", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(MapsActivity.this, RecommendActivity.class);
        startActivity(intent);
    }
}