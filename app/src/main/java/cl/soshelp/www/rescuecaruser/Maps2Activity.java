package cl.soshelp.www.rescuecaruser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps2Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double lat=0.0,lng=0.0;
    Double latC=0.0, lngC=0.0;
    String id_mob, mob_chofer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        obtenerInfo();
        // Controles UI
       mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void obtenerInfo(){
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            lat=(Double) b.get("latAuto");
            lng=(Double) b.get("longAuto");
            latC =(Double) b.get("latGrua");
            lngC =(Double) b.get("longGrua");
            id_mob =(String) b.get("id_mob");
            mob_chofer =(String) b.get("mob_chofer");
            AgregarMarker();
        }

    }


    private void AgregarMarker(){

        // Add a marker in Sydney and move the camera
        LatLng Auto = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(Auto).title("Auto"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Auto));
        // Add a marker in Sydney and move the camera
        LatLng grua = new LatLng(latC, lngC);
        mMap.addMarker(new MarkerOptions().position(grua).title("Camion"));
        //goToLocationZoom(latA, lngA, 13);

    }

    public static float obtenerResultado(float celsius){
        float result = ((celsius*9)/5)+32;
        return result+6;
    }
}