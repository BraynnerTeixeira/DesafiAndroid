package com.desafio.desafioapp.Abas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desafio.desafioapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class RadarFragment extends Fragment {

    private FusedLocationProviderClient client;
    private GoogleMap mMap;
    private static final String api_key = "AIzaSyBdarc8s7YWpRm9BGgc62koNTZEOFNSDIk";
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;

    public RadarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        int errorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        switch (errorCode) {
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                Log.d("Teste", "show dialog");
                GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), errorCode,
                        0, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                try {
                                    finalize();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        }).show();
                break;
            case ConnectionResult.SUCCESS:
                Log.d("Teste", "Google Play Services up-to-date");
                break;

        }
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if (location != null){

                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.drawable.profile);
                            Bitmap b=bitmapdraw.getBitmap();
                            Bitmap iconP = Bitmap.createScaledBitmap(b, width, height, false);

                            LatLng local = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconP))
                                    .position(local)
                                    .title("Local Atual"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local,14));
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);

                      /*  requesturl="https://maps.googleapis.com/maps/api/place/search/json?radius=500&sensor=false&key="+
                                   api_key+"&location="+local+"&input=posto";*/


                            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                @Override
                                public void onCameraIdle() {


                                    FindAutocompletePredictionsRequest filtro =  FindAutocompletePredictionsRequest.builder()
                                            .setCountry("BR")
                                            .setTypeFilter(TypeFilter.ESTABLISHMENT)
                                            .setSessionToken(token)
                                            .setLocationRestriction(RectangularBounds.newInstance(
                                                    mMap.getProjection().getVisibleRegion().latLngBounds
                                            ))
                                            .setQuery("supermercados")
                                            .build();

                                    placesClient.findAutocompletePredictions(filtro)
                                            .addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                                                @Override
                                                public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                                                    if (task.isSuccessful()){
                                                        FindAutocompletePredictionsResponse resultado = task.getResult();

                                                        if (resultado != null){
                                                            final List<AutocompletePrediction> predictions = resultado.getAutocompletePredictions();
                                                            for (AutocompletePrediction c: predictions){
                                                            /*
                                                            List<Place.Type> placeTypes = c.getPlaceTypes();
                                                            for (Place.Type type : placeTypes){
                                                                Log.i("Teste Place","Type" + type.name());
                                                            }*/

                                                                //  Log.i("Teste Place",c.getFullText(null).toString());
                                                                //Log.i("Teste Place",c.getPlaceId());

                                                                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                                                                FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(c.getPlaceId(), fields)
                                                                        .setSessionToken(token)
                                                                        .build();

                                                                placesClient.fetchPlace(placeRequest)
                                                                        .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                                                                            @Override
                                                                            public void onSuccess(FetchPlaceResponse response) {
                                                                                int height = 100;
                                                                                int width = 100;
                                                                                BitmapDrawable bitmapdraw =(BitmapDrawable)getResources().getDrawable(R.drawable.place_market);
                                                                                Bitmap b=bitmapdraw.getBitmap();
                                                                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                                                                                Place place = response.getPlace();
                                                                                LatLng latLng = place.getLatLng();
                                                                                assert latLng != null;
                                                                                mMap.addMarker(new MarkerOptions()
                                                                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                                                                        .position(latLng))
                                                                                        .setTitle(place.getName());


                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }else {
                                                        Log.i("Execption", Objects.requireNonNull(task.getException()).getMessage());
                                                    }
                                                }
                                            });

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5*1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("Teste", locationSettingsResponse.getLocationSettingsStates().isNetworkLocationPresent() + "");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException){
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(getActivity(),10);

                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        final LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null){
                    Log.i("Erro Local", "Local null");
                    return;
                }
                for (Location location: locationResult.getLocations()){
                    Log.i("Teste2",location.getLatitude()+"");
                }
            }
        };
        client.requestLocationUpdates(locationRequest,locationCallback,null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_radar, container, false);
        client = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        Places.initialize(getContext(),api_key);
        placesClient = Places.createClient(getContext());
        token = AutocompleteSessionToken.newInstance();



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setMinZoomPreference(6.0f);
                mMap.setMaxZoomPreference(20.0f);


            }

        });

        return view;
    }
}