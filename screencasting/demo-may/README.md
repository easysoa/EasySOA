## Video editing

This project has been built on "OpenShot Video Editor"

    sudo apt-get install openshot openshot-doc

Note that the project file stores links to its resources in absolute path, so you will need to use *ln -fs* or a replace function on `DemoMay.osp` to make it work. Also, it needs the raw video `ScreencastingRaw.ogv`, which is not included here. 

## Subtitles

The subtitles have been made with "Gnome Subtitles". They can be embedded in the video using "Avidemux".

    sudo apt-get install gnome-subtitles avidemux
