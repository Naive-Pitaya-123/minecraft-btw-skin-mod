package com.example.skinmixin.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Mixin(targets = "net.minecraft.src.ThreadDownloadImage")
public class ThreadDownloadImageMixin {
    @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Ljava/net/URL;openConnection()Ljava/net/URLConnection;"))
    private URLConnection redirectConnection(URL url) throws IOException {
        String urlStr = url.toString();
        if (urlStr.startsWith("http://crafatar.com/skins/")) {
            String userName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
            return new URL("http://localhost:5444/MinecraftSkins/" + userName + ".png").openConnection();
        }
        if (urlStr.startsWith("http://crafatar.com/capes/")) {
            String userName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
            return new URL("http://localhost:5444/MinecraftCloaks/" + userName + ".png").openConnection();
        }
        return url.openConnection();
    }
}