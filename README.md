# hQiblaDirection

An open source Android (Java) Native Library to incorporate/start Android Qibla Direction Activity in your Native Android Applcation.


## Configurations
### Dependency
```markdown
implementation 'com.hassanjamil:qibla:0.2'
```
### Root Level build.gradle
```
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
```
<activity
	android:name="com.hassanjamil.hqibla.CompassActivity"
	android:label="@string/app_name"
	android:screenOrientation="portrait"/>
```
### Sample Code
```
startActivity(new Intent(MainActivity.this, CompassActivity.class));
```
### Screenshot
![Alt text](/Screenshot.jpeg?raw=true "Preview")
### Contributor
Muhammad Hassan Jamil - Team Lead Android Development - TPL Maps - hassan.jamil@tplmaps.com

Please contirbute more to help it improve.

### Reference
This code is based on https://github.com/iutinvg/compass/
