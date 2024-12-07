package entity;

import java.util.Set;

public class SpeedItem {

    private boolean isSpeedUp;     // true - SpeedUp / false - SpeedDown
    private boolean isActive;

    private long startTime;
    private long effectDuration = 10000;
    private double increaseSpeedMultiplier = 3.0;
    private double decreaseSpeedMultiplier = 0.25;

    private Set<EnemyShip> enemyShips;

    public SpeedItem() {
        this.isActive = false;
    }

    // active
    public void activate(boolean isSpeedUp, Set<EnemyShip> enemyShips) {
        this.isActive = true;
        this.isSpeedUp = isSpeedUp;
        this.startTime = System.currentTimeMillis();
        this.enemyShips = enemyShips;

        for (EnemyShip enemyShip : this.enemyShips) {
            if (this.isSpeedUp) {
                enemyShip.setSpeedMultiplier(increaseSpeedMultiplier); //increase enemy's speed
            } else {
                enemyShip.setSpeedMultiplier(decreaseSpeedMultiplier); // decrease enemy's speed
            }

        }
    }

    public void deActivate() {
        this.isActive = false;
        for (EnemyShip enemyShip : this.enemyShips) {
            enemyShip.resetSpeedMultiplier();
        }
    }

    public void update() {
        if (isActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > effectDuration) { // 5 seconds
                deActivate();
            }
        }
    }
}