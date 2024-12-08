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
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


public class Skins {
    public static List<SpriteType> lockedSkins = new ArrayList<>(Arrays.asList(SpriteType.Skin1, SpriteType.Skin2, SpriteType.Skin3,SpriteType.Skin4, SpriteType.Skin5));
    public static ArrayList<Object> AllSkins = new ArrayList<>(Arrays.asList(SpriteType.Skin1, SpriteType.Skin2, SpriteType.Skin3,SpriteType.Skin4, SpriteType.Skin5));;
    public static ArrayList<SpriteType> unlockedSkins = new ArrayList<>();

    public static void unlockSkin(SpriteType skin) {
        if (!unlockedSkins.contains(skin) && lockedSkins.contains(skin)) {
            unlockedSkins.add(skin);
            lockedSkins.remove(skin); // Remove from locked skins
            saveSkins();
            Core.getLogger().info("Unlocked skin: " + skin);
            showUnlockDialog(skin, new DrawManager());
        }
    }

    public static boolean isSkinUnlocked(SpriteType skin) {
        return unlockedSkins.contains(skin);
    }

    public static void loadSkins() {

        try {
            Properties properties = FileManager.loadUnlockedSkins();
            for (String skinName : properties.stringPropertyNames()) {
                if (properties.getProperty(skinName).equals("true")) {
                    unlockedSkins.add(SpriteType.valueOf(skinName));
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
    private static void showUnlockDialog(SpriteType unlockedSkin, DrawManager drawManager) {
        SkinEntity unlockedSkinEntity = new SkinEntity(unlockedSkin) ;
        JDialog dialog = new JDialog();
        dialog.setSize(250, 150);
        dialog.setLocationRelativeTo(null); // 화면 중앙에 표시
        dialog.setUndecorated(true);
        FileManager fileManager = new FileManager();
        Font customFont;
        try {
            customFont = fileManager.loadFont(14f); // 16px 크기의 폰트 로드
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Serif", Font.PLAIN, 14); // 로드 실패 시 기본 폰트
        }

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int centerX = getWidth() / 2 - (unlockedSkinEntity.getWidth() / 2) - 61;
                int centerY = getHeight() / 2 - (unlockedSkinEntity.getHeight() / 2) - 10;
                g.setColor(Color.GREEN);

                // 새로운 drawEntityWithGraphics 메서드를 호출하여 JPanel에 그리기
                DrawManager.drawEntityWithGraphics(g, unlockedSkinEntity, centerX, centerY);
            }
        };
        contentPanel.setBackground(Color.BLACK);
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2)); //테두리
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("Unlock " + unlockedSkinEntity.getSpriteType(), SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE); // 텍스트 색
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setFont(customFont);

        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(10)); // 여백 추가

        dialog.addKeyListener(new KeyAdapter() {  // 스페이스바를 누르면 창 닫는 Keylistener
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    dialog.dispose();
                }
            }
        });
        dialog.setContentPane(contentPanel);
        dialog.setFocusable(true); // KeyListener가 동작하도록 설정
        dialog.setVisible(true); // 창 표시
    }


 /* public static void resetUnlockedSkins() {
        unlockedSkins.clear(); // 해금된 스킨 초기화
        saveSkins(); // 저장된 파일도 초기화
        Core.getLogger().info("Unlocked skins have been reset.");
    }
*/
}
