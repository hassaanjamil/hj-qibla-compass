package com.islam.compass_noads;

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
                /*intent.putExtra(Constants.COMPASS_BG_COLOR, "#FFFFFF");
                intent.putExtra(Constants.TOOLBAR_BG_COLOR, "#FFFFFF");
                intent.putExtra(Constants.TOOLBAR_TITLE_COLOR, "#000000");
                intent.putExtra(Constants.ANGLE_TEXT_COLOR, "#000000");
                intent.putExtra(Constants.DRAWABLE_DIAL, R.drawable.dial);
                intent.putExtra(Constants.DRAWABLE_QIBLA, R.drawable.ic_arrow_back);
                intent.putExtra(Constants.BOTTOM_IMAGE_VISIBLE, View.GONE);*/
                startActivity(intent);
            }
        });
    }
}
