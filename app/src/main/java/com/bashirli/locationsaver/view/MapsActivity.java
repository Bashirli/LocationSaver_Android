package com.bashirli.locationsaver.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bashirli.locationsaver.DataCollect.Data;
import com.bashirli.locationsaver.R;
import com.bashirli.locationsaver.database.DataDAO;
import com.bashirli.locationsaver.database.DataDB;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.bashirli.locationsaver.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
LocationManager locationManager;
LocationListener locationListener;
ActivityResultLauncher<String> permissionLauncher;
SharedPreferences sharedPreferences;
Data selectedData;
DataDB dataDB;
DataDAO dataDAO;
double selectedLat,selectedLon;
CompositeDisposable compositeDisposable=new CompositeDisposable();


boolean changeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
sharedPreferences=this.getSharedPreferences("com.bashirli.locationsaver",MODE_PRIVATE);
changeInfo=false;
        activity_launch();



   dataDB= Room.databaseBuilder(getApplicationContext(),DataDB.class,"Data").build();
   dataDAO=dataDB.dataDAO();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if(info.equals("new")){
            binding.editText.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.button.setVisibility(View.VISIBLE);
            binding.button2.setVisibility(View.GONE);
            binding.button.setEnabled(false);
            locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    changeInfo=sharedPreferences.getBoolean("info",false);
                    if (!changeInfo) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        sharedPreferences.edit().putBoolean("info",true).apply();
                    }
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    LocationListener.super.onStatusChanged(provider, status, extras);
                }
            };

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.getRoot(),"İcazə verilməyib!",Snackbar.LENGTH_INDEFINITE).setAction("İcazə ver.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //permission
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }else{
                    //permisson
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }else{
                //launch
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                mMap.setMyLocationEnabled(true);

            }
            mMap.setOnMapLongClickListener(this);
        }else{
            mMap.clear();
            selectedData= (Data) intent.getSerializableExtra("data");
            mMap.setMyLocationEnabled(true);
            binding.editText.setInputType(InputType.TYPE_NULL);
            binding.button.setVisibility(View.GONE);
            binding.button2.setVisibility(View.VISIBLE);
            binding.editText.setText(selectedData.name);
            selectedLon=selectedData.longtitude;
            selectedLat=selectedData.latitude;
            LatLng selectedLatLng=new LatLng(selectedLat,selectedLon);

            mMap.addMarker(new MarkerOptions().title(selectedData.name).position(selectedLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng,15));



        }




    }


    public void activity_launch(){
permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
    @Override
    public void onActivityResult(Boolean result) {
        if(result){
if(ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
    mMap.setMyLocationEnabled(true);
}
        }else{
            Toast.makeText(MapsActivity.this, "İcazə verilməyib.", Toast.LENGTH_SHORT).show();
        }
    }
});
    }

    public void save_data(View view){
        Data data=new Data(binding.editText.getText().toString(),selectedLat,selectedLon);
        compositeDisposable.add(dataDAO.insert(data).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::turn_back));
    }

    public void turn_back(){
        Intent intent=new Intent(MapsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        compositeDisposable.clear();
    }

    public void delete_data(View view){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Silmək");
        alert.setMessage("Silməyə əminsiniz?");
        alert.setPositiveButton("Bəli", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                compositeDisposable.add(dataDAO.delete(selectedData).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(MapsActivity.this::turn_back));
            }
        });
        alert.setNegativeButton("Xeyr", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
           return;
            }
        });
        alert.create().show();

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Seçilmiş məkan"));
        selectedLat=latLng.latitude;
        selectedLon=latLng.longitude;
        binding.button.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}