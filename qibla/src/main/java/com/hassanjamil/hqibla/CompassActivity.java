package com.hassanjamil.hqibla;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private TextView tvYourLocation;
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

        setUserChanges(getIntent());

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
        //tvAngle = findViewById(R.id.angle);
        //tvYourLocation = findViewById(R.id.your_location);

        //////////////////////////////////////////
        /*qiblatIndicator.setVisibility(INVISIBLE);
        qiblatIndicator.setVisibility(View.GONE);*/

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

    private void setUserChanges(Intent intent) {
        try {
            // Toolbar Title
            /*((Toolbar) findViewById(R.id.toolbar)).setTitle(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_TITLE)) ?
                            intent.getExtras().getString(Constants.TOOLBAR_TITLE) : getString(R.string.app_name));*/

            // Toolbar Title Color
            /*((Toolbar) findViewById(R.id.toolbar)).setTitleTextColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_TITLE_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.TOOLBAR_TITLE_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, android.R.color.white))));*/

            // Toolbar Background Color
            /*findViewById(R.id.toolbar).setBackgroundColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_BG_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.TOOLBAR_BG_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, R.color.app_red))));*/

            // Toolbar Back Arrow Icon
            /*Drawable drawable2 = getDrawable(getApplicationContext(),
                    intent.getExtras().getString(Constants.TOOLBAR_NAV_ICON));
            Drawable drawable1 = ContextCompat
                    .getDrawable(getApplicationContext(), intent.getExtras().getInt(Constants.TOOLBAR_NAV_ICON));
            Drawable drawable = ContextCompat
                    .getDrawable(getApplicationContext(), (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.TOOLBAR_NAV_ICON)) ?
                            intent.getExtras().getInt(Constants.TOOLBAR_NAV_ICON) :
                            R.drawable.ic_arrow_back);
            ((Toolbar) findViewById(R.id.toolbar)).setNavigationIcon(drawable2);*/

            // Root Background Color
            /*findViewById(R.id.root).setBackgroundColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.COMPASS_BG_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.COMPASS_BG_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, R.color.app_red))));*/

            // Qibla Degrees Text Color
            /*((TextView) findViewById(R.id.angle)).setTextColor(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.ANGLE_TEXT_COLOR)) ?
                            Color.parseColor(intent.getExtras().getString(Constants.ANGLE_TEXT_COLOR)) :
                            Color.parseColor("#" + Integer.toHexString(
                                    ContextCompat.getColor(this, android.R.color.white))));*/

            /*// Dial
            ((ImageView) findViewById(R.id.dial)).setImageResource(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.DRAWABLE_DIAL)) ?
                            intent.getExtras().getInt(Constants.DRAWABLE_DIAL) : R.drawable.dial);

            // Qibla Indicator
            ((ImageView) findViewById(R.id.qibla_indicator)).setImageResource(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.DRAWABLE_QIBLA)) ?
                            intent.getExtras().getInt(Constants.DRAWABLE_QIBLA) : R.drawable.qibla);*/

            // Footer Image
            /*findViewById(R.id.footer_image).setVisibility(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.FOOTER_IMAGE_VISIBLE)) ?
                            intent.getExtras().getInt(Constants.FOOTER_IMAGE_VISIBLE) : View.VISIBLE);*/

            // Your Location TextView
            /*findViewById(R.id.your_location).setVisibility(
                    (intent.getExtras() != null &&
                            intent.getExtras().containsKey(Constants.LOCATION_TEXT_VISIBLE)) ?
                            intent.getExtras().getInt(Constants.LOCATION_TEXT_VISIBLE) : View.VISIBLE);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public Drawable getDrawable(Context context, String resource_name) {
        try {
            int resId = context.getResources().getIdentifier(resource_name, "drawable", context.getPackageName());
            if (resId != 0) {
                return ContextCompat.getDrawable(context, resId);
            }
        } catch (Exception e) {
            Log.e(TAG, "getDrawable - resource_name: " + resource_name);
            e.printStackTrace();
        }

        return null;
    }*/

    private void setupCompass() {
        Boolean permission_granted = GetBoolean("permission_granted");
        if (permission_granted) {
            getBearing();
        } else {
            /*tvAngle.setText(getResources().getString(R.string.msg_permission_not_granted_yet));
            tvYourLocation.setText(getResources().getString(R.string.msg_permission_not_granted_yet));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        RC_Permission);
            } else {
                fetch_GPS();
            }*/
        }


        compass = new Compass(this);
        Compass.CompassListener cl = new Compass.CompassListener() {

            @Override
            public void onNewAzimuth(float azimuth) {
                /*adjustGambarDial(azimuth);
                adjustArrowQiblat(azimuth);*/
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

        float kaabaDegs = GetFloat("kiblat_derajat");
        if (kaabaDegs > 0.0001) {
            String strYourLocation;
            if(gps.getLocation() != null)
                strYourLocation = getResources().getString(R.string.your_location)
                        + " " + gps.getLocation().getLatitude() + ", " + gps.getLocation().getLongitude();
            else
                strYourLocation = getResources().getString(R.string.unable_to_get_your_location);
            tvYourLocation.setText(strYourLocation);
            String strKaabaDirection = String.format(Locale.ENGLISH, "%.0f", kaabaDegs)
                    + " " + getResources().getString(R.string.degree) + " " + getDirectionString(kaabaDegs);
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

    private String getDirectionString(float azimuthDegrees) {
        String where = "NW";

        if (azimuthDegrees >= 350 || azimuthDegrees <= 10)
            where = "N";
        if (azimuthDegrees < 350 && azimuthDegrees > 280)
            where = "NW";
        if (azimuthDegrees <= 280 && azimuthDegrees > 260)
            where = "W";
        if (azimuthDegrees <= 260 && azimuthDegrees > 190)
            where = "SW";
        if (azimuthDegrees <= 190 && azimuthDegrees > 170)
            where = "S";
        if (azimuthDegrees <= 170 && azimuthDegrees > 100)
            where = "SE";
        if (azimuthDegrees <= 100 && azimuthDegrees > 80)
            where = "E";
        if (azimuthDegrees <= 80 && azimuthDegrees > 10)
            where = "NE";

        return where;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_Permission) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                SaveBoolean("permission_granted", true);
                tvAngle.setText(getResources().getString(R.string.msg_permission_granted));
                tvYourLocation.setText(getResources().getString(R.string.msg_permission_granted));
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
            double myLat = gps.getLatitude();
            double myLng = gps.getLongitude();
            // \n is for new line
            String strYourLocation = getResources().getString(R.string.your_location)
                    + " " + myLat + ", " + myLng;
            tvYourLocation.setText(strYourLocation);
            //Toast.makeText(getApplicationContext(), "Lokasi anda: - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            Log.e("TAG", "GPS is on");
            if (myLat < 0.001 && myLng < 0.001) {
                // qiblatIndicator.isShown(false);
                qiblatIndicator.setVisibility(INVISIBLE);
                qiblatIndicator.setVisibility(View.GONE);
                tvAngle.setText(getResources().getString(R.string.location_not_ready));
                tvYourLocation.setText(getResources().getString(R.string.location_not_ready));
                /*if (item != null) {
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_off));
                }*/
                // Toast.makeText(getApplicationContext(), "Location not ready, Please Restart Application", Toast.LENGTH_LONG).show();
            } else {
                /*if (item != null) {
                    item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_on));
                }*/
                double kaabaLng = 39.826206; // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double kaabaLat = Math.toRadians(21.422487); // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double myLatRad = Math.toRadians(myLat);
                double longDiff = Math.toRadians(kaabaLng - myLng);
                double y = Math.sin(longDiff) * Math.cos(kaabaLat);
                double x = Math.cos(myLatRad) * Math.sin(kaabaLat) - Math.sin(myLatRad) * Math.cos(kaabaLat) * Math.cos(longDiff);
                result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
                SaveFloat("kiblat_derajat", (float) result);
                String strKaabaDirection = String.format(Locale.ENGLISH, "%.0f", (float) result)
                        + " " + getResources().getString(R.string.degree) + " " + getDirectionString((float) result);
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
            tvYourLocation.setText(getResources().getString(R.string.pls_enable_location));
            /*if (item != null) {
                item.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gps_off));

            }*/
            // Toast.makeText(getApplicationContext(), "Please enable Location first and Restart Application", Toast.LENGTH_LONG).show();
        }
    }
    ////////////////////////////////////
}
