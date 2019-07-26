# hQiblaDirection

An open source Android (Java) Native Library to incorporate/start Android Qibla Direction Activity in your Native Android Applcation.


## Configurations
### Dependency
```markdown
implementation 'com.hassanjamil:qibla:0.2.1'
```
### Root Level build.gradle
``` groovy
allprojects {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven { url "http://api.tplmaps.com:8081/artifactory/example-repo-local/" }
    }
}
```
### AndroidManifest.xml
``` xml
<activity
	android:name="com.hassanjamil.hqibla.CompassActivity"
	android:label="@string/app_name"
	android:screenOrientation="portrait"/>
```
### Sample Code
``` java
Intent intent = new Intent(MainActivity.this, CompassActivity.class);
intent.putExtra(Constants.COMPASS_BG_COLOR, "#FFFFFF");		// Compass background color
intent.putExtra(Constants.TOOLBAR_BG_COLOR, "#FFFFFF");		// Toolbar Background color
intent.putExtra(Constants.TOOLBAR_TITLE_COLOR, "#000000");	// Toolbar Title color
intent.putExtra(Constants.ANGLE_TEXT_COLOR, "#000000");		// Angle Text color
intent.putExtra(Constants.DRAWABLE_DIAL, R.drawable.dial);	// Your dial drawable resource
intent.putExtra(Constants.DRAWABLE_QIBLA, R.drawable.qibla); 	// Your qibla indicator drawable resource
intent.putExtra(Constants.BOTTOM_IMAGE_VISIBLE, View.VISIBLE|View.INVISIBLE|View.GONE);	// Bottom Image visibility
startActivity(intent);
```
### Screenshot
![Preview](Screenshots/preview.png?raw=true "Preview")

### Sample APK
[APK](Apk/app-debug.apk)

### Contributor
Muhammad Hassan Jamil - Team Lead Android Development - TPL Maps - hassan.jamil@tplmaps.com

Please contirbute more to help it improve.

### Reference
This code is based on https://github.com/iutinvg/compass/
