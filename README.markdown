## About the Book

[*Android's Architecture Components*](https://commonsware.com/AndroidArch/) 
covers the use of [the Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
released by Google in 2017.
This book is updated several times a year and is available through
[the Warescription](https://commonsware.com/warescription) program. Subscribers also have access to office
hours chats and other benefits.

This repository contains the source code for the sample apps profiled in the book. These 
samples are updated as the book is, with `git` tags applied to tie sample code versions to book
versions.

The book, and the samples, were written by Mark Murphy. You may also have run into him through
Stack Overflow:

<a href="http://stackoverflow.com/users/115145/commonsware">
<img src="http://stackoverflow.com/users/flair/115145.png" width="208" height="58" alt="profile for CommonsWare at Stack Overflow, Q&amp;A for professional and enthusiast programmers" title="profile for CommonsWare at Stack Overflow, Q&amp;A for professional and enthusiast programmers">
</a>

All of the source code in this archive is licensed under the
Apache 2.0 license except as noted.

All of the projects should have a `build.gradle` file suitable for
importing the project into Android Studio. Note, though, that you
may need to adjust the `compileSdkVersion` in `build.gradle` if it
requests an SDK that you have not downloaded and do not wish to
download. Similarly, you may need to adjust the `buildToolsVersion`
value to refer to a version of the build tools that you have downloaded
from the SDK Manager.

The samples also have stub Gradle wrapper files, enough to allow for
easy import into Android Studio. However,
**always check the `gradle-wrapper.properties` file before importing anything into Android Studio**,
as there is always the chance that somebody has published material linking you to a hacked Gradle installation.
