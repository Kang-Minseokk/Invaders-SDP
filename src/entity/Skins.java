package entity;

import engine.Core;
import engine.DrawManager;
import engine.DrawManager.SpriteType;
import engine.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class Skins {
    public static List<SpriteType> lockedSkins = new ArrayList<>(
            Arrays.asList(SpriteType.Skin1, SpriteType.Skin2,
                    SpriteType.Skin3,SpriteType.Skin4,
                    SpriteType.Skin5));
    public static ArrayList<SpriteType> unlockedSkins = new ArrayList<>();
    public static int Count = 1;
    public static int DrawCount = Count;

    //Draw 횟수
    public static void addDrawCount(){
        DrawCount ++;
        FileManager.saveDrawCount(DrawCount);
    }

    public static int getDrawCount(){
       return DrawCount;
    }

    public static void unlockSkin(SpriteType skin) {
        if (!unlockedSkins.contains(skin) && lockedSkins.contains(skin)) {
            unlockedSkins.add(skin);
            lockedSkins.remove(skin); // Remove from locked skins
            saveSkins();
            Core.getLogger().info("Unlocked skin: " + skin);
        }
    }

    public static boolean isSkinUnlocked(DrawManager.SpriteType skin) {
        return unlockedSkins.contains(skin);
    }

    public static void loadSkins() {

        try {
            Properties properties = FileManager.loadUnlockedSkins();
            for (String skinName : properties.stringPropertyNames()) {
                if (properties.getProperty(skinName).equals("true")) {
                    unlockedSkins.add(DrawManager.SpriteType.valueOf(skinName));
                }
            }
            Core.getLogger().info("Unlocked skins loaded: " + unlockedSkins);
        } catch (IOException e) {
            Core.getLogger().warning("Failed to load unlocked skins.");

        }
    }

    public static void saveSkins() {
        Properties properties = new Properties();

        for (SpriteType skin : unlockedSkins) {
            properties.setProperty(skin.name(), "true");
        }

        try {
            FileManager.saveUnlockedSkins(properties);
            Core.getLogger().info("Unlocked skins saved: " + unlockedSkins);
        } catch (IOException e) {
            Core.getLogger().warning("Failed to save unlocked skins.");
            //e.printStackTrace();
        }
    }
}
