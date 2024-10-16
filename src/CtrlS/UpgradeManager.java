package CtrlS;

import engine.Core;
import engine.FileManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Logger;
import inventory_develop.ShipStatus;

public final class UpgradeManager {

    /** Singleton instance of the class. */
    private static UpgradeManager instance;
    /** Application logger. */
    private static Logger logger;
    private static FileManager fileManager;

    // Upgrade keys
    private static final String COIN_ACQUISITION_MULTIPLIER = "coin_acquisition_multiplier";
    private static final String ATTACK_SPEED = "attack_speed";
    private static final String MOVEMENT_SPEED = "movement_speed";
    private static final String BULLET_NUM = "bullet_num";

    // inventory, Upgrade count
    private static final String Speed_Count = "speed_LevelCount";
    private static final String Attack_Count = "attack_LevelCount";
    private static final String Coin_Count = "Coin_LevelCount";
    private static final String Bullet_Count = "bullet_LevelCount";
    // load stat increase data
    private ShipStatus shipStatus;


    /** Decimal format to ensure values have one decimal place. */
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    /**
     * private constructor.
     */
    private UpgradeManager() {
        fileManager = Core.getFileManager();
        logger = Core.getLogger();
        try{
            Core.getFileManager().saveUpgradeStatus(Core.getFileManager().loadUpgradeStatus());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        // load stat increase data
        shipStatus = new ShipStatus();
        shipStatus.loadStatus();
    }

    /**
     * Returns shared instance of UpgradeManager.
     *
     * @return Shared instance of UpgradeManager.
     */
    public static UpgradeManager getInstance() {
        if (instance == null)
            instance = new UpgradeManager();
        return instance;
    }

    // Methods for coin acquisition multiplier

    /**
     * Get the current coin acquisition multiplier value.
     *
     * @return The current coin acquisition multiplier.
     * @throws IOException In case of loading problems.
     */
    public double getCoinAcquisitionMultiplier() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Double.parseDouble(properties.getProperty(COIN_ACQUISITION_MULTIPLIER, "1.0"));
    }

    /**
     * Add to the current coin acquisition multiplier.
     *
     *
     * @throws IOException In case of saving problems.
     */
    public void addCoinAcquisitionMultiplier() throws IOException {
        double currentValue = getCoinAcquisitionMultiplier();
        currentValue += shipStatus.getCoinIn();

        // Format the value to one decimal place
        String formattedValue = decimalFormat.format(currentValue);

        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(COIN_ACQUISITION_MULTIPLIER, formattedValue);
        fileManager.saveUpgradeStatus(properties);
    }

    // Methods for attack speed

    /**
     * Get the current attack speed value.
     *
     * @return The current attack speed.
     * @throws IOException In case of loading problems.
     */
    public int getAttackSpeed() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(ATTACK_SPEED, "1"));
    }

    /**
     * Add to the current attack speed.
     *
     *
     * @throws IOException In case of saving problems.
     */
    public void addAttackSpeed() throws IOException {
        int currentValue = getAttackSpeed();
        currentValue += shipStatus.getSuootingInIn();
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(ATTACK_SPEED, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }

    // Methods for movement speed

    /**
     * Get the current movement speed value.
     *
     * @return The current movement speed.
     * @throws IOException In case of loading problems.
     */
    public int getMovementSpeed() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(MOVEMENT_SPEED, "1"));
    }

    /**
     * Add to the current movement speed.
     *
     *
     * @throws IOException In case of saving problems.
     */
    public void addMovementSpeed() throws IOException {
        int currentValue = getMovementSpeed();
        currentValue += shipStatus.getSpeedIn();
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(MOVEMENT_SPEED, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }

    /**
     * Reset all upgrades to their default values.
     *
     * @throws IOException In case of saving problems.
     */
    public void resetUpgrades() throws IOException {
        Properties properties = fileManager.loadDefaultUpgradeStatus();
        fileManager.saveUpgradeStatus(properties);
    }

    // --- produce inventory team ---

    // Methods for bullet Number

    public int getBulletNum() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(BULLET_NUM, "1"));
    }

    public void addBulletNum() throws IOException {
        int currentValue = getBulletNum();
        currentValue += 1;
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(BULLET_NUM, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }


    public int getSpeedCount() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(Speed_Count, "1"));
    }
    public int getAttackCount() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(Attack_Count, "1"));
    }
    public int getBulletCount() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(Bullet_Count, "1"));
    }
    public int getCoinCount() throws IOException {
        Properties properties = fileManager.loadUpgradeStatus();
        return Integer.parseInt(properties.getProperty(Coin_Count, "1"));
    }


    public void addSpeedCount() throws IOException {
        int currentValue = getSpeedCount();
        currentValue += 1;
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(Speed_Count, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }
    public void addAttackCount() throws IOException {
        int currentValue = getAttackCount();
        currentValue += 1;
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(Attack_Count, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }
    public void addBulletCount() throws IOException {
        int currentValue = getBulletCount();
        currentValue += 1;
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(Bullet_Count, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }
    public void addCoinCount() throws IOException {
        int currentValue = getCoinCount();
        currentValue += 1;
        Properties properties = fileManager.loadUpgradeStatus();
        properties.setProperty(Coin_Count, Integer.toString(currentValue));
        fileManager.saveUpgradeStatus(properties);
    }

}