DevCon 2013 Android Apps
========================

DevCon 2013 is a developer conference held each year opened to all IT professionals who are involved in any kind of software and software related solution development.


This is the Android Application inspired by Google IO 2013. This is a work in a progress and some of the features may not work. (or even broke) . Please see the issues section to report any bugs or feature requests and to see the list of known issues.

APK Download
------------

This will be available on Play Store very soon. For the daily builds, you can find it [here][11] and download as a raw file.


Developed By
-------------

* YLA - <yelinaung@zwenex.com>
* Hein Win Toe - <hein@nexlabs.co>
* Ye Mon Kyaw - <indexer@myanmarplus.net>

Installing
----------
You will need the following tools.

* Git
* Android SDK
* ADT (or) Android Studio (or) IntelliJ

Once you have the above tools installed, you can install the app as follows:

###Cloning
First of all you will have to clone the library.
```shell
git clone
```

In Android Studio (or) ADT navigate the menus like this.
```
File -> Import Project ...
```

###Installing Dependencies
The dependencies (*.jar) are put inside of `libs` folder and you have to include them at Java Build Path.

* [Android Asynchronous Http Client][8]
* [Universal Image Loader for Android][9]
* [Crashlytics][10]

DevCon App has the following Library dependencies
* [Actionbarsherlock][2]
* [StickyListHeaders][3]
* [ViewPagerIndicator][4]
* [FacebookSDK][12]

Clone it and import it to your IDE and follow the rest of the instructions at [Referencing a Library Project][5]


You can create your own account and add your API keys at `res/values/api.xml`. See below "Important Notes"


Important Notes
----------------
I've omitted the xml file which stores the sensitive api keys. You have to create your own at `res\values\api.xml` with the following content.
App is is your Facebook App id.


```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!-- FILL UP YOUR API KEYS HERE -->
    <string name="mix_panel_api"> </string>
    <string name="crashlytics"> </string>
    <string name="app_id"> </string>

</resources>
```


Contributing
------------
You're very welcome!

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request


License
-------
    Copyright 2013 DevCon 2013

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: http://android-developers.blogspot.com/2011/03/fragments-for-all.html
 [2]: http://actionbarsherlock.com
 [3]: https://github.com/emilsjolander/StickyListHeaders
 [4]: https://github.com/JakeWharton/Android-ViewPagerIndicator
 [5]: http://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject
 [6]: https://www.crashlytics.com/
 [7]: https://mixpanel.com/
 [8]: https://loopj.com/android-async-http/
 [9]: https://github.com/nostra13/Android-Universal-Image-Loader
 [10]: http://try.crashlytics.com/sdk-android/
 [11]: https://github.com/DevCon-Myanmar/DevCon-Android/tree/master/daily_builds
 [12]: https://developers.facebook.com/docs/android/
