## Estimote SDK example apps

**Hint:**  
You can generate yourself a ready-made project from one of these templates on https://cloud.estimote.com/#/apps/add. It will be automatically renamed for you, and have your App Token and beacon UUIDs already put in.

- **Blank**

  A blank Android Studio project with Estimote SDK already integrated.

- **Notification**

  > Uses: beacon monitoring, notifications

  Show a notification when the user enters or exits range of a monitored beacon.

- **ProximityContent**

  > Uses: ranging beacons, Estimote Cloud API to fetch the beacon's name & color

  Change the background color and text on the screen depending on which of the ranged beacons is the closest.

- **NFCStamps**

  > Uses: NFC (requires [new Proximity Beacons](http://blog.estimote.com/post/147038205465/announcing-next-gen-proximity-beacons-with), hardware revision "G")

  Tap your phone to a beacon to automatically open the app & collect a coffee stamp.

- **Showroom**

  > Uses: [sticker beacons](http://developer.estimote.com/nearables/stickers-vs-beacons/), Nearable packet

  Change the text on screen when user picks up a nearable (motion detection). Imagine, e.g., a tablet mounted in a showroom, and products with Estimote Stickers attached to them. Whenever a visitor picks an item up, the iPad shows information about the product.
