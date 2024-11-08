package entity;

import engine.DrawManager;
import engine.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


public class Skins {
    public static DrawManager.SpriteType[] lockedSkins = {DrawManager.SpriteType.Skin1, DrawManager.SpriteType.Skin2, DrawManager.SpriteType.Skin3, DrawManager.SpriteType.Skin4, DrawManager.SpriteType.Skin5};
    public static ArrayList<DrawManager.SpriteType> unlockedSkins = new ArrayList<>();

    public static void unlockSkin(DrawManager.SpriteType skin) {
        if (!unlockedSkins.contains(skin)) {
            unlockedSkins.add(skin);
            saveSkins();
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
            System.out.println("Unlocked skins loaded: " + unlockedSkins);
        } catch (IOException e) {
            System.err.println("Failed to load unlocked skins.");
            e.printStackTrace();
        }
    }

    public static void saveSkins() {
        Properties properties = new Properties();

        for (DrawManager.SpriteType skin : unlockedSkins) {
            properties.setProperty(skin.name(), "true");
        }

        try {
            FileManager.saveUnlockedSkins(properties);
            System.out.println("Unlocked skins saved.");
        } catch (IOException e) {
            System.err.println("Failed to save unlocked skins.");
            e.printStackTrace();
        }
    }



}
