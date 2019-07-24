package android.example.insuranceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Pol_Activity extends AppCompatActivity {

    private TextView ptitle,pdescription,pcategory;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pol_view);

        ptitle = (TextView) findViewById(R.id.ititle);
        pdescription = (TextView) findViewById(R.id.iDesc);
        pcategory = (TextView) findViewById(R.id.iCat);
        img = (ImageView) findViewById(R.id.ithumbnail);

        // Receive data
        Intent intent = getIntent();
        String Title = intent.getExtras().getString("Title");
        String Description = intent.getExtras().getString("Description");
        int image = intent.getExtras().getInt("Thumbnail") ;

        // Setting values
        ptitle.setText(Title);
        pdescription.setText(Description);
        img.setImageResource(image);


    }
}
