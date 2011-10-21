## Video editing

This project has been built on "OpenShot Video Editor"

    sudo apt-get install openshot openshot-doc

Note that the project file stores links to its resources in absolute path, so you will need to use *ln -fs* or a replace function on `DemoMay.osp` to make it work. Also, it needs the raw video `ScreencastingRaw.ogv`, which available [here](http://www.easysoa.org/wp-content/uploads/videos/EasySOA-Demo-0.1-Tour-Raw.ogv).

## Subtitles

The subtitles have been made with "Gnome Subtitles". They can be embedded in the video using "Avidemux".

    sudo apt-get install gnome-subtitles avidemux
    
## Make the video streaming-ready

In order to use the video on our website with pseudo-streaming enabled, we used the [BinKit](http://rodrigopolo.com/about/wp-stream-video/ffmpeg-binary-installers-for-win-mac-and-linux) scripts.
   
    ./BinKit-v2.0/16x9/480p.sh /path/to/sourcevideo
