# Popular Movies Part 2

##Phase two of the udacity android project "Popular Movies"

NOTE: To run locally, please replace the key "tmdbApiKey" in gradle.properties with your API key

Main activity displays a grid of poular movies to the user, who can then click the action bar menu to toggle between
poplar movies or top rated movies from themoviedb.org.

Filtering options include:
1. Popular
2. Top Rated
3. Now Playing (currently for US region) - configurable in strings file
4. Coming soon (currently for US region) - configurable in strings file
5. Favorites

Note: pagination has been implemented for tmdb API. Currently capped at 10 pages for each category

Clicking a movie's poster will take the user to a details screen which will display an individual movie's:
1. title
2. poster
3. banner
4. release date (US theatrical release date if available)
5. running time
6. user vote average from themoviedb.org
7. MPAA rating icon if data is available
7. tagline if available
8. plot overview if available
9. movie's genres if available
10. cast list if available
11. Any YouTube trailers associated on tmdb
12. Any reviews listed on tmdb

User's state is saved on transition between activities, rotation, and when clicking the Android home button so that
fewer network calls will be made to the API at the movie database. Additionally the app will alert the user if 
there is no current network connection or it there is a problem retrieving a list of movies.

Favorites can be saved to a local db, along with their poster an banner images so all 
data for them is avilable in offline mode.

If user in on a device with a minimum display width of greather than 600dp, a two-pane mode
will be displayed with movie grid on the left and details on the right

###Screenshots

All views have been adjusted for portrait/landscape and device size buckets (see screenshots in 
the github wiki for this project.)

###Attributions

UI Styling:
Layout and elements mixed and matched from suggested UI, but mostly inspired by Plex
and thetvdb.com

MPAA rating icons:
Taken from wikicommons (with public domain licenses) and re-tooled by hand editing SVG files

Async tasks as class files:
https://stackoverflow.com/questions/26202568/android-pass-function-reference-to-asynctask

Margins for grid view:
http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing

Network http calls and url builder:
Adapted from the udacity Sunshine app and exercises

Storing API key in config file:
https://technobells.com/best-way-to-store-your-api-keys-for-your-android-studio-project-e4b5e8bb7d23

ConnectionChecker:
https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out

Youtube launching:
https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent

Data binding:
Udacity boarding pass lesson

Two-way data binding:
https://developer.android.com/topic/libraries/data-binding/index.html

Retrofit:
https://android.jlelse.eu/consuming-rest-api-using-retrofit-library-in-android-ed47aef01ecb

Share action provider:
https://developer.android.com/training/sharing/shareaction.html

Pagination:
http://blog.iamsuleiman.com/android-pagination-recyclerview-tutorial-api-retrofit-gson/
https://stackoverflow.com/questions/36127734/detect-when-recyclerview-reaches-the-bottom-most-position-while-scrolling

Bitmap storage:
https://stackoverflow.com/questions/9042932/getting-image-from-imageview
https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android

Collapsible toolbar:
http://www.feelzdroid.com/2015/08/collapsing-toolbars-android-example.html
https://stackoverflow.com/questions/27856603/lollipop-draw-behind-statusbar-with-its-color-set-to-transparent

Instantiating fragments/two pane mode
Udacity Android-Me lesson and stackoverflow
https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment

Changing progress loader color:
https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android

Master branch lints clean and passes code inspection in Android Studio
