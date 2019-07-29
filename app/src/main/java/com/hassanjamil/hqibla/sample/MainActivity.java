package com.hassanjamil.hqibla.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hassanjamil.hqibla.CompassActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CompassActivity.class);
                //intent.putExtra(Constants.TOOLBAR_NAV_ICON, "ic_arrow_back_custom");

                /*intent.putExtra(Constants.COMPASS_BG_COLOR, "#FFFFFF");
                intent.putExtra(Constants.TOOLBAR_TITLE, "My App");
                intent.putExtra(Constants.TOOLBAR_BG_COLOR, "#FFFFFF");
                intent.putExtra(Constants.TOOLBAR_TITLE_COLOR, "#000000");
                intent.putExtra(Constants.ANGLE_TEXT_COLOR, "#000000");
                intent.putExtra(Constants.DRAWABLE_DIAL, R.drawable.dial);
                intent.putExtra(Constants.DRAWABLE_QIBLA, R.drawable.ic_arrow_back_custom);
                intent.putExtra(Constants.FOOTER_IMAGE_VISIBLE, View.GONE);
                intent.putExtra(Constants.LOCATION_TEXT_VISIBLE, View.VISIBLE);*/
                startActivity(intent);
            }
        });
    }
}
