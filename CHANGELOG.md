Changelog - beta releases
=====================

## 0.14.0-beta (October 17, 2016)
- Added support for key-value storage on devices with firmware 4.9.0+
  * You can now push your key:value map via our SDK to device in the same way as typical setting:
  ```Java
   Map<String, String> map = new HashMap<>();
   map.put("myKey", "myValue");
   connection.settings.storage.writeStorage(map, new StorageManager.WriteCallback() {
      @Override
      public void onSuccess() {
        // HANDLE SUCCESS
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
        // HANDLE FAILURE
      }
    });
    ```
   * Reading stored values can be done without user being authorised - this will allow client apps to read stored data. For this purpose, there is a new method for quick connection to beacon: `deviceConnectionProvider.getConnectionForStorageRead(ConfigurableDevice)` 
 * Bear in mind that the more data is stored on device the more time it takes to read it.
 
- Improved Estimote Analytics precision and data persistence.
  
