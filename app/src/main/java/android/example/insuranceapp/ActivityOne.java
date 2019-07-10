package android.example.insuranceapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

public class ActivityOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circlemenu);

        CircleMenu circleMenu = findViewById(R.id.circleMenu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {

            }
        });
        circleMenu.setEventListener(new CircleMenu.EventListener() {
            @Override
            public void onMenuOpenAnimationStart() {

            }

            @Override
            public void onMenuOpenAnimationEnd() {

            }

            @Override
            public void onMenuCloseAnimationStart() {

            }

            @Override
            public void onMenuCloseAnimationEnd() {

            }

            @Override
            public void onButtonClickAnimationStart(@NonNull CircleMenuButton menuButton) {

            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuButton menuButton) {

            }
        });

//        TextView txtInfo = (TextView)findViewById(R.id.txtInfo);
//        if(getIntent() != null)
//        {
//            String info = getIntent().getStringExtra("info");
//            txtInfo.setText(info);
//        }
    }
}