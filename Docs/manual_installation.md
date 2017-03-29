# Estimote SDK for android manual installation

*Eclipse users:* Mark Murphy [on his blog explained](https://commonsware.com/blog/2014/07/03/consuming-aars-eclipse.html) how to use `aar` format in Eclipse.

1. Create `libs` directory inside your project and copy there [estimote-sdk.aar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/estimote-sdk.aar).
2. In your `build.gradle` add `flatDir` entry to your repositories

  ```groovy
  repositories {
    mavenCentral()
      flatDir {
        dirs 'libs'
      }
  }
```
3. Add dependency to Estimote SDK. All needed permissions (`BLUETOOTH`, `BLUETOOTH_ADMIN` and `INTERNET`) and services will be merged from SDK's `AndroidManifest.xml` to your application's `AndroidManifest.xml`.

  ```groovy
  dependencies {
    compile(name:'estimote-sdk', ext:'aar')
  }
```
4. Initialize Estimote SDK in your Application class onCreate() method - if you are using [Estimote Cloud](http://cloud.estimote.com):

  ```java
  //  App ID & App Token can be taken from App section of Estimote Cloud.
  EstimoteSDK.initialize(applicationContext, appId, appToken);
  // Optional, debug logging.
  EstimoteSDK.enableDebugLogging(true);
  ```
