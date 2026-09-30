Hello, this is the usage documentation. The development documentation is further down below.

Download skinmixin-1.0-dev.jar.

Open the mods folder of your BTW CE 2.1.4 instance and drag it in.

Set up LittleSkin login in your launcher. Each launcher is different, so here we will only cover HMCL (Hello Minecraft Launcher). Click on Accounts, click "Add Auth Server" in the bottom left corner, enter https://littleskin.cn , then click "LittleSkin" in the left sidebar and enter your username and password.

Register for LittleSkin. Enter LittleSkin or click the built-in "LittleSkin Homepage" feature in some launchers to register or log in.

If you are a player outside of mainland China and https://littleskin.cn is not responding when you try to register, please turn on a VPN service.

The console is in Simplified Chinese. This does not matter; you can just minimize it and ignore it. If there is no skin, please send the error message to someone around you to help translate it for you.

## Developer Documentation

Download btw-mixin.zip, extract it, use the cd command in CMD to navigate to the btw-mixin folder, modify the src code to the style you need, and compile with gradlew.bat build.

> [!CAUTION]
> After compiling, you must use JBE (or another tool) to open skinmixin-1.0-dev.jar, navigate to com/example/skinmixin/, find ThreadDownloadImageMixin.class, and change net.minecraft.src.ThreadDownloadImage to net.minecraft.class_526. Otherwise it will not load!!!

### Compiling MinecraftLegacySkinServer-1.0

Download MinecraftLegacySkinServer-1.0.zip, extract it, compile the code in src into the format you want, and compile with gradlew.bat build.
