# osuCatJ

Re-write of (osu! cat)[https://github.com/ZeCryptic/osu-cat]

Added Tablet and osu!Hotkey detecting.

Will not work when any window which running as Admin role is foreground. 

So don't run osu! in Admin mode.

lazy to do benchmark :(

requiring for Wacom offical driver Registry feature. (I'm using the third-party driver and don't want to install the wacom driver)


###Compile:
Clone the project , use Auto-import , when complete use mvn assembly, and you will get an .exe in target path.

**then copy the `\cat` dir to the directory where the exe file exists.**


###Streaming:
+ Run the program first.
+ Create a `Source` in type `Game Capture`.
+ Set Mode as `Capture Specific Window`.
+ Select the window `[javaw.exe]: osu!Cat J` from the `Window` drop-down box
+ Move the mouse to let the window refresh.
+ Resizing the Capture as you want, Done! 


