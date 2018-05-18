#mrufinal
MRU-F-v1-FINAL

DONE:
A. GITHUB VIEW (Specs parts 1 - 7) >> MRU-A
+ (1,2) Using Retrofit library list all “tetris” Repos by the REST API call:
        https://api.github.com/search/repositories?q=tetris 
+ (3,4) RecyclerView grid layout showing Repo name, Owner’s login name and Repo size
+ (5) Different background colour for items that have "has_wiki" set to 'true'
+ (6) Pagination - Get 10 entries with every REST call and extend the list by 10 more whenever the list is scrolled to the end. Watch out to the rate limit
+ (7) Add a text field where a user can enter an arbitrary search string instead of "tetris”.

B. CONTACTS VIEW  (Specs part 8) >> MRU-B
+ RecyclerView that lists all the phone’s Contacts. 
+ Create a model for Contact which contains name, surname, picture if available and a phone number.
+ Tapping the cells should bring up the native phone’s Dialer with that contact’s number. 
+ Show Contacts Access Permission dialog until accepted, if dismissed cancel view.

C. INTERNET STATUS (Specs part 9) >> MRU-C
+ (9) Internet Connection Status. Use SnackBar for notification which will show up when the Internet is disconnected and will go away if the Internet is reconnected.

D. GLOBAL TIMER (Specs part 10) >> MRU-D
+ (10) Global Timer to create a local notification that will pop up on every 5 minutes.

COMMON
+ Usage of git. 
+ Use the Android native libraries. (besides Retrofit for 1)
