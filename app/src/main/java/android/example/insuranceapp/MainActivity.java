package android.example.insuranceapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Pol> ins_pol;
    GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);





        ins_pol = new ArrayList<>();
        ins_pol.add(new Pol("Automotive","Category Pol","Description: Auto",R.drawable.car));
        ins_pol.add(new Pol("Health","Category Pol","Description: Health",R.drawable.health));
        ins_pol.add(new Pol("Travel","Category Pol","Description: Travel",R.drawable.travel));
        ins_pol.add(new Pol("House","Category Pol","Description: House",R.drawable.house));

        RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this, ins_pol);
        myrv.setLayoutManager(new GridLayoutManager(this,2));
        myrv.setAdapter(myAdapter);


    }
}