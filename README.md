This app has two screens ('`Activities`' in the Android world), called `MainActivity` and `ActivityTwo`.

* `MainActivity` is the start screen. It has two buttons: `TO ACTIVITY TWO` and `START SERVICE`.
  * `TO ACTIVITY TWO` goes to the `ActivityTwo` activity/screen.
  * `START SERVICE` starts the `GPSService`.
* `ActivityTwo` has two buttons, `TO MAIN ACTIVITY` and `STOP SERVICE`.
  * `TO MAIN ACTIVITY` goes to the `MainActivity` activity/screen.
  * `STOP SERVICE` stop the `GPSService`.

The `GPSService` is a background `Service` that continues to run even if the application is exited or destroyed!

It uses the OpenStreetMap server at `isasdev.cim.mcgill.ca:44343/autour/getPlaces.php` to get the data on street intersections nearest to the user's current (latitude,longitude) coordinates.

The `GPSService` polls the current GPS location once per second. If `ActivityTwo` is not currently in focus AND the user is within 8 meters of a street intersection, `GPSService` starts up `ActivityTwo` and brings the user to it.
