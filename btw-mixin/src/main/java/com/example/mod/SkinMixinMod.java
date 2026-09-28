package com.example.skinmixin.mod;

import net.fabricmc.api.ModInitializer;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SkinMixinMod implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            File gameDir = new File(System.getProperty("user.dir"));
            File modsDir = new File(gameDir, "mods");
            File serverJar = new File(modsDir, "MinecraftLegacySkinServer-1.0.jar");

            if (!serverJar.exists()) {
                InputStream in = getClass().getResourceAsStream("/assets/skinmixin/MinecraftLegacySkinServer-1.0.jar");
                if (in == null) {
                    System.out.println("Skin server jar not found in mod resources");
                    return;
                }
                Files.copy(in, serverJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                in.close();
            }

            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(javaBin, "-Dfile.encoding=UTF-8", "-jar", serverJar.getAbsolutePath());
            final Process process = pb.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                process.destroy();
            }));

            System.out.println("Skin server started from " + serverJar.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}