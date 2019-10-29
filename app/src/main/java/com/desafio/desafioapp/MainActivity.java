package com.desafio.desafioapp;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.desafio.desafioapp.Abas.ListaFragment;
import com.desafio.desafioapp.Abas.PerfilFragment;
import com.desafio.desafioapp.Abas.RadarFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    /*private int[] icons = {
            R.drawable.radar,
            R.drawable.list,
            R.drawable.profile,
    };*/
    private int[] iconsAtivos = {
            R.drawable.radar_active,
            R.drawable.list_active,
            R.drawable.profile_active,
    };

    private TabLayout tabLayout;
    private AlertDialog alertDialog;

    FusedLocationProviderClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Desafio App");
        setSupportActionBar(toolbar);

        Resources resources = getResources();
        AbasAdapter adapter = new AbasAdapter(getSupportFragmentManager());
        adapter.adicionar(new RadarFragment(), resources.getString(R.string.radar_fragment));
        adapter.adicionar(new ListaFragment(), resources.getString(R.string.lista_fragment));
        adapter.adicionar(new PerfilFragment(), resources.getString(R.string.perfil_fragment));



        ViewPager viewPager = findViewById(R.id.abas_view_pager);
        viewPager.setAdapter(adapter);

        tabLayout =  findViewById(R.id.abas);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        client = LocationServices.getFusedLocationProviderClient(this);


    }



    @Override
    protected void onResume() {
        super.onResume();

    }


    private void setupTabIcons() {
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(iconsAtivos[0]);
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(iconsAtivos[1]);
                Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(iconsAtivos[2]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filtro) {
            filtros();
        }
        return super.onOptionsItemSelected(item);
    }

    private void filtros(){
        final ArrayList<String> itens = new ArrayList<>();
        itens.add("Todos");
        itens.add("Aeroportos");
        itens.add("Restaurantes");
        itens.add("Baladas");
        itens.add("Supermercados");
        itens.add("Shopping centers");
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.listadapter,
                R.id.list_adapter, itens);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Categorias");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(stringArrayAdapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this, "Filtrando ", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }


}
