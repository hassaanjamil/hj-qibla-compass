package com.hassanjamil.hqibla;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.view.View.INVISIBLE;


public class CompassActivity extends AppCompatActivity {
    private static final String TAG = CompassActivity.class.getSimpleName();
    private Compass compass;
    private ImageView qiblatIndicator;
    private ImageView imageDial;
    private TextView tvAngle;
    //private TextView tvYourLocation;
    //public Menu menu;
    //public MenuItem item;
    private float currentAzimuth;
    SharedPreferences prefs;
    GPSTracker gps;
    private final int RC_Permission = 1221;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        setUserChangesToViews(getIntent());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /////////////////////////////////////////////////
        prefs = getSharedPreferences("", MODE_PRIVATE);
        gps = new GPSTracker(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //////////////////////////////////////////
        qiblatIndicator = findViewById(R.id.qibla_indicator);
        imageDial = findViewById(R.id.dial);
        tvAngle = findViewById(R.id.angle);
        //tvYourLocation = findViewById(R.id.tv_your_location);

        //////////////////////////////////////////
        qiblatIndicator.setVisibility(INVISIBLE);
        qiblatIndicator.setVisibility(View.GONE);

        setupCompass();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        if (compass != null) {
            compass.start(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (compass != null) {
            compass.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (compass != null) {
            compass.start(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        if (compass != null) {
            compass.stop();
        }
        if (gps != null) {
            gps.stopUsingGPS();
            gps = null;
        }
    }

    private void setUserChangesToViews(Intent intent) {
        try {
            // Toolbar Background Color
            findViewById(R.id.toolbar).setBackgroundColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_BG_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.TOOLBAR_BG_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, R.color.app_red))));
            // Toolbar Title Color
            ((Toolbar) findViewById(R.id.toolbar)).setTitleTextColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_TITLE_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.TOOLBAR_TITLE_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, android.R.color.white))));
            // Root Background Color
            findViewById(R.id.root).setBackgroundColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.COMPASS_BG_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.COMPASS_BG_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, R.color.app_red))));
            // Qibla Degrees Text Color
            ((TextView) findViewById(R.id.angle)).setTextColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.ANGLE_TEXT_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.ANGLE_TEXT_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, android.R.color.white))));
            // Toolbar Nav Icon
            /*((Toolbar) findViewById(R.id.toolbar)).setNavigationIcon(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_NAV_ICON)) ?
                            intent.getExtras().getInt(Constants.TOOLBAR_NAV_ICON) : R.drawable.ic_arrow_back);*/
            // Dial
            ((ImageView) findViewById(R.id.dial)).setImageResource(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.DRAWABLE_DIAL)) ?
                            intent.getExtras().getInt(Constants.DRAWABLE_DIAL) : R.drawable.dial);
            // Qibla Indicator
            ((ImageView) findViewById(R.id.qibla_indicator)).setImageResource(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.DRAWABLE_QIBLA)) ?
                            intent.getExtras().getInt(Constants.DRAWABLE_QIBLA) : R.drawable.qibla);
            // Bottom Image
            findViewById(R.id.bottom_image).setVisibility(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.BOTTOM_IMAGE_VISIBLE)) ?
                            intent.getExtras().getInt(Constants.BOTTOM_IMAGE_VISIBLE) : View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCompass() {
        Boolean permission_granted = GetBoolean("permission_granted");
        if (permission_granted) {
            getBearing();
        } else {
            tvAngle.setText(getResources().getString(R.string.msg_permission_not_granted_yet));
            //tvYourLocation.setText(getResources().getString(R.string.msg_permission_not_granted_yet));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        RC_Permission);
            } else {
                fetch_GPS();
            }
        }


        compass = new Compass(this);
        Compass.CompassListener cl = new Compass.CompassListener() {

            @Override
            public void onNewAzimuth(float azimuth) {
                // adjustArrow(azimuth);
                adjustGambarDial(azimuth);
                adjustArrowQiblat(azimuth);
            }
        };
        compass.setListener(cl);

        ////////////// ADDED CODE ///////////////
        //fetch_GPS();
    }


    public void adjustGambarDial(float azimuth) {
        // Log.d(TAG, "will set rotation from " + currentAzimuth + " to "                + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        imageDial.startAnimation(an);
    }

    public void adjustArrowQiblat(float azimuth) {
        //Log.d(TAG, "will set rotation from " + currentAzimuth + " to "                + azimuth);

        float kiblat_derajat = GetFloat("kiblat_derajat");
        Animation an = new RotateAnimation(-(currentAzimuth) + kiblat_derajat, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        qiblatIndicator.startAnimation(an);
        if (kiblat_derajat > 0) {
            qiblatIndicator.setVisibility(View.VISIBLE);
        } else {
            qiblatIndicator.setVisibility(INVISIBLE);
            qiblatIndicator.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    public void getBearing() {
        // Get the location manager

        float kiblat_derajat = GetFloat("kiblat_derajat");
        if (kiblat_derajat > 0.0001) {
            /*String strYourLocation;
            if(gps.getLocation() != null)
                strYourLocation = getResources().getString(R.string.your_location)
                        + " " + gps.getLocation().getLatitude() + ", " + gps.getLocation().getLongitude();
            else
                strYourLocation = getResources().getString(R.string.unable_to_get_your_location);*/
            //tvYourLocation.setText(strYourLocation);
            String strKaabaDirection = String.format(Locale.ENGLISH, "%.0f", kiblat_derajat)
                    + " " + getResources().getString(R.string.degree);
            tvAngle.setText(strKaabaDirection);
            // MenuItem item = menu.findItem(R.id.gps);
            //if (item != null) {
            //item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_off));
            //}
            qiblatIndicator.setVisibility(View.VISIBLE);
        } else {
            fetch_GPS();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_Permission) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                SaveBoolean("permission_granted", true);
                tvAngle.setText(getResources().getString(R.string.msg_permission_granted));
                //tvYourLocation.setText(getResources().getString(R.string.msg_permission_granted));
                qiblatIndicator.setVisibility(INVISIBLE);
                qiblatIndicator.setVisibility(View.GONE);

                fetch_GPS();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_permission_required), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    /*public void SaveString(String Judul, String tex) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Judul, tex);
        edit.apply();
    }

    public String GetString(String Judul) {
        return prefs.getString(Judul, "");
    }*/

    public void SaveBoolean(String Judul, Boolean bbb) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Judul, bbb);
        edit.apply();
    }

    public Boolean GetBoolean(String Judul) {
        return prefs.getBoolean(Judul, false);
    }

   /* public void Savelong(String Judul, Long bbb) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(Judul, bbb);
        edit.apply();
    }

    public Long Getlong(String Judul) {
        Long xxxxxx = prefs.getLong(Judul, 0);
        return xxxxxx;
    }*/

    public void SaveFloat(String Judul, Float bbb) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putFloat(Judul, bbb);
        edit.apply();
    }

    public Float GetFloat(String Judul) {
        return prefs.getFloat(Judul, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        // this.menu = menu;
        // menu.getItem(0). setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_off));
        // getMenuInflater().inflate(R.menu.gps, menu);
        // MenuItem item = menu.findItem(R.id.gps);
        //inflater.inflate(R.menu.gps, menu);
        //item = menu.findItem(R.id.gps);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

        // Handle presses on the action bar items
        /*switch (item.getItemId()) {
            case R.id.gps:
                //logout code
                fetch_GPS();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
    }

    public void fetch_GPS() {


        double result;
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            /*double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // \n is for new line
            String strYourLocation = getResources().getString(R.string.your_location)
                    + " " + latitude + ", " + longitude;*/
            //tvYourLocation.setText(strYourLocation);
            //Toast.makeText(getApplicationContext(), "Lokasi anda: - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            Log.e("TAG", "GPS is on");
            double lat_saya = gps.getLatitude();
            double lon_saya = gps.getLongitude();
            if (lat_saya < 0.001 && lon_saya < 0.001) {
                // qiblatIndicator.isShown(false);
                qiblatIndicator.setVisibility(INVISIBLE);
                qiblatIndicator.setVisibility(View.GONE);
                tvAngle.setText(getResources().getString(R.string.location_not_ready));
                //tvYourLocation.setText(getResources().getString(R.string.location_not_ready));
                /*if (item != null) {
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_off));
                }*/
                // Toast.makeText(getApplicationContext(), "Location not ready, Please Restart Application", Toast.LENGTH_LONG).show();
            } else {
                /*if (item != null) {
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_on));
                }*/
                double longitude2 = 39.826206; // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double latitude2 = Math.toRadians(21.422487); // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double latitude1 = Math.toRadians(lat_saya);
                double longDiff = Math.toRadians(longitude2 - lon_saya);
                double y = Math.sin(longDiff) * Math.cos(latitude2);
                double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
                result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
                SaveFloat("kiblat_derajat", (float) result);
                String strKaabaDirection = String.format(Locale.ENGLISH, "%.0f", (float) result)
                        + " " + getResources().getString(R.string.degree);
                tvAngle.setText(strKaabaDirection);
                qiblatIndicator.setVisibility(View.VISIBLE);

                /*Location kaaba = new Location("Kaaba");
                kaaba.setLatitude(39.826206);
                kaaba.setLongitude(21.422487);
                Location currentLocation = gps.getLocation();
                if(currentLocation != null) {
                    float bearTo = currentLocation.bearingTo(kaaba);
                    if(bearTo < 0)
                        bearTo = bearTo + 360;


                }*/
            }
            //  Toast.makeText(getApplicationContext(), "lat_saya: "+lat_saya + "\nlon_saya: "+lon_saya, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();

            // qiblatIndicator.isShown(false);
            qiblatIndicator.setVisibility(INVISIBLE);
            qiblatIndicator.setVisibility(View.GONE);
            tvAngle.setText(getResources().getString(R.string.pls_enable_location));
            //tvYourLocation.setText(getResources().getString(R.string.pls_enable_location));
            /*if (item != null) {
                item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_off));

            }*/
            // Toast.makeText(getApplicationContext(), "Please enable Location first and Restart Application", Toast.LENGTH_LONG).show();
        }
    }
    ////////////////////////////////////
}
