package screen;

import engine.Frame;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Background {
    private static Background instance;
    private static final String titleBackground = "/backgrounds/night.jpg";

    private int backgroundWidth;
    private int backgroundHeight;
    private int screenWidth;
    private int screenHeight;
    private int horizontalOffset;
    private int verticalOffset;
    private int scrollSpeedHorizontal;
    private int scrollSpeedVertical;
    private int offsetUpdateInterval;

    private Background() {
        // Empty constructor
    }

    public static synchronized Background getInstance() {
        if (instance == null) {
            instance = new Background();
        }
        return instance;
    }

    public static InputStream getTitleBackgroundStream() {
        return Background.class.getResourceAsStream(titleBackground);
    }

    public void initialize(Frame frame) {
        this.screenWidth = frame.getWidth();
        this.screenHeight = frame.getHeight();
    }

    public void backgroundReset(int imageHeight, int imageWidth) {
        this.backgroundWidth = imageWidth;
        this.backgroundHeight = imageHeight;
        this.horizontalOffset = -screenWidth;
        this.verticalOffset = 0;
        this.scrollSpeedHorizontal = 3;
        this.scrollSpeedVertical = 1;
        this.offsetUpdateInterval = 0;
    }

    public static List<String> levelBackgrounds;
    // Static block to initialize levelBackgrounds
    static {
        levelBackgrounds = new ArrayList<>();
        levelBackgrounds.add("/backgrounds/background_level_1.jpg");
        levelBackgrounds.add("/backgrounds/background_level_2.jpg");
        levelBackgrounds.add("/backgrounds/background_level_3.jpg");
        levelBackgrounds.add("/backgrounds/background_level_4.jpg");
        levelBackgrounds.add("/backgrounds/background_level_5.jpg");
        levelBackgrounds.add("/backgrounds/background_level_6.jpg");
        levelBackgrounds.add("/backgrounds/background_level_7.jpg");
    }
    // Static method to get background image stream
    public static InputStream getBackgroundImageStream(int levelIndex) {
        if (levelBackgrounds == null || levelIndex < 1 || levelIndex > levelBackgrounds.size()) {
            throw new IllegalArgumentException("Invalid level index: " + levelIndex);
        }
        return Background.class.getResourceAsStream(levelBackgrounds.get(levelIndex - 1));
    }


    // Dynamic method to update background image vertical offset
    public int getVerticalOffset() {
        int scrollLimit = backgroundHeight - screenHeight;

        if (scrollLimit == -verticalOffset) {
            scrollSpeedVertical = 0;
        }

        if (offsetUpdateInterval % 3 == 0) {
            verticalOffset -= scrollSpeedVertical;
            offsetUpdateInterval = 0;
        }
        offsetUpdateInterval++;

        return verticalOffset;
    }

    // Dynamic method to update background image horizontal offset
    public int getHorizontalOffset(boolean backgroundMoveRight, boolean backgroundMoveLeft) {
        if (backgroundMoveRight) {
            horizontalOffset -= scrollSpeedHorizontal;
        }
        if (backgroundMoveLeft) {
            horizontalOffset += scrollSpeedHorizontal;
        }
        return horizontalOffset;
    }
}
