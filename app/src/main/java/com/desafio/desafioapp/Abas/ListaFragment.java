package com.desafio.desafioapp.Abas;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.desafio.desafioapp.HttpHandler;
import com.desafio.desafioapp.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ListaFragment extends Fragment {


  
    private ArrayList<HashMap<String, String>> cList;
    private ListView lv;
    private FusedLocationProviderClient client;
    private String key = "AIzaSyBdarc8s7YWpRm9BGgc62koNTZEOFNSDIk";


    @Override
    public void onResume() {
        super.onResume();


        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5*1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(Objects.requireNonNull(getContext()));
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

        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        cList = new ArrayList<>();
        lv = Objects.requireNonNull(getActivity()).findViewById(R.id.list);
        client = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        new googleplaces().execute();

        return view;

    }


    @SuppressLint("StaticFieldLeak")
    private class googleplaces extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //HttpHandler sh = new HttpHandler();
                    LatLng local = new LatLng(location.getLatitude(),location.getLongitude());


                    String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurantes&location="
                            +local+"&radius=10000" + "&key=" + key;

                    String jsonStr = HttpHandler.get(url);



                    Log.e("TAG", "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsonStr);


                            JSONArray locais = jsonObj.getJSONArray("results");

                            for (int i = 0; i < locais.length(); i++) {
                                JSONObject c = locais.getJSONObject(i);
                               // String id = c.getString("id");
                                String name = c.getString("name");
                                String endereco = c.getString("formatted_address");

                                HashMap<String, String> listLocais = new HashMap<>();


                                // listLocais.put("id", id);
                                listLocais.put("name", name);
                                listLocais.put("email", endereco);

                                cList.add(listLocais);
                            }
                        } catch (final JSONException e) {
                            Log.e("TAG", "Json parsing error: " + e.getMessage());
                            runOnUiThread();

                        }

                    } else {
                        Log.e("TAG", "Couldn't get json from server.");
                        runOnUiThread();
                    }

                }

                private void runOnUiThread() {
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }
}

