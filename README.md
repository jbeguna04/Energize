# Energize - Monitor your Battery
This application is designed to monitor your battery state and provide useful information about the current charging level, the estimated time to charge or discharge and some more useful information.

## Download a pre-build version
You can download a pre-build version of Energize from the [Google Play][8] store.

## Build the application with [Eclipse][7]
For building the application by yourself follow the following steps:

  1. Clone the application repository

          git clone https://github.com/thuetz/Energize.git
       
  2. Change into the cloned directory

          cd Energize
          
  3. Initialize and update the submodules

          git submodule init
          git submodule update
          
  4. Create an Android project for each submodule using the `Android Project from Existing Code` function of Eclipse
  5. Create an Android project for the actual application (using the `Android Project from Existing Code` function of Eclipse)
  6. Build the application :)

## Translations
As soon as the used strings is nearly fixed, you can help us on our project page of [GetLocalization][1] to translate the app in many languages.

## Application icon
The application icon is based upon the [Android Robot][3] logo made by Google Inc. This logo is licensed under the terms of the [Creative Commons Attribution license][4]. The battery icon was designed by the authors of the [GNOME High contrast icon set][5] and is licensed under the terms of the [GNU Lesser General Public License][6].

## Changelog
Version 0.6.3 *(2012-08-22)*
----------------------------
* Fixed a bug which caused an application crash while rotating the view
* Fixed a bug which caused that the battery temperature was not logged
* Added a tab where a temperature graph is displayed
* Improved the design of the battery capacity graph

Version 0.6 *(2012-08-19)*
--------------------------
* The main screen shows the temperature of the battery
* Added an option to chose between Celsius and Fahrenheit
* Added several pages to the main screen to clean up the UI and prepare for a Tablet layout
* Resized the graph to make it better readable
* Updated the themes to optimize the look of the app
* Cleaned up the preference of the app
* Done a important code cleanup to remove unused code, optimize the speed and the power consumption of the app and make it easier to implement new features

Version 0.5 *(2012-08-12)*
--------------------------
* Redesigned the main screen of the application
* Changed the way battery change information are logged
* Updated the statistics database to store the battery temperature
* The main screen will show a graph about the charging and discharging process of the battery
* Done some more internal code cleanup

Version 0.4.1 *(2012-08-10)*
----------------------------
* Added license information for the application icon
* Applied a fix for the -1 estimation bug
* Fixed the bug that clicking the notification item won't show the main activity
* Added support for API level 14 (which includes plain Android 4.0.0)
* The notification bar item will now show if the battery is charges or discharges

Version 0.4 *(2012-08-07)*
--------------------------
* Basic code cleanup
* Cleanup of the preference screens
* The remaining time will show hours and minutes instead of just minutes
* Added an option to disable the automatic startup of the boot service

Version 0.3 *(2012-08-05)*
--------------------------
* Added the first (but simple) remaining time estimation
* Added the license text to the application (see About settings)
* Updated the application icon
* First version which is available on the Google Play store for everyone
* Prepared the code to be able to handle in-app billing stuff (for easy donations)

Version 0.2 *(2012-07-13)*
--------------------------
* Added a preference dialog for clearing the obtained battery statistics
* Added a status bar indicator for the current charging level of the battery
* Added a "What's new" dialog on the first startup of a new version

Version 0.1.3 *(2012-07-03)*
----------------------------
* Changed the layout of the battery statistics database

Version 0.1.2 *(2012-07-02)*
----------------------------
* Fixed a bug which caused that the preferences were not accessible
* Added a first debug option to send the battery statistic database via mail
* Updated the infrastructure to establish communication between the service and the app

Version 0.1 *(2012-06-29)*
--------------------------
* Added code to obtain statistics about the battery charings
* Added GPL header to all source files
* Moved all strings to the resource file (for supporting localization)

Version 0.0.2 *(2012-04-22)*
---------------------------
* Added a preference dialog for changing the used theme of the app
* Added an dialog to show information about the application

Version 0.0.1 *(2012-04-17)*
----------------------------
* Initial version which is available in the Google Play Store

 [1]: http://www.getlocalization.com/energize
 [2]: http://www.mentalrey.it
 [3]: http://developer.android.com/distribute/googleplay/promote/brand.html
 [4]: http://developer.android.com/license.html#attribution
 [5]: http://commons.wikimedia.org/wiki/GNOME_High_contrast_icons/authors
 [6]: http://www.gnu.org/licenses/lgpl.html
 [7]: http://www.eclipse.org/
 [8]: https://play.google.com/store/apps/details?id=com.halcyonwaves.apps.energize
