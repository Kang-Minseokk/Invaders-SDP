package Achievement;

import engine.DrawManager;
import screen.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

// TODO : Replace Object to Achievement Class
// Fixed, need testing
public class AchievementManager {
    /*
        Variables
     */
    private HashMap<Achievement, Boolean> achievementMap; // Object -> Achievement
    private ArrayList<AchievementChangedCallback> achievementChangedCallbacks;

    private AchievementConditions achievementConditions;

    public AchievementManager(DrawManager drawManager) {
        this.achievementConditions = new AchievementConditions(drawManager);
    }

    public void updateAchievements(Screen screen) {
        System.out.println("Updating achievements...");

        achievementConditions.checkAllAchievements();
    }
    /*
        Callbacks
     */
    @FunctionalInterface
    public interface AchievementChangedCallback {
        void onAchievementChanged(Achievement achievement, boolean value); // Object -> Achievement
    }

    public void addAchievementChangedCallback(AchievementChangedCallback callback){
        achievementChangedCallbacks.add(callback);
    }

    public void removeAchievementChangedCallback(AchievementChangedCallback callback){
        achievementChangedCallbacks.remove(callback);
    }

    /*
        Declare
     */
    public AchievementManager() { // HashMap<Achievement, Boolean>으로 초기화
        achievementMap = new HashMap<>();
        achievementChangedCallbacks = new ArrayList<>();
    }

    /*
        Functions
     */

    public boolean addAchievement(Achievement achievement, Boolean completed) { // Object -> Achievement
        if (achievementMap.containsKey(achievement))
            return false;
        achievementMap.put(achievement, completed);
        return true; // Changed code
    }

    public boolean addAchievement(Achievement achievement) { // Object -> Achievement로 변경
        return addAchievement(achievement, false);
    }

    public boolean hasAchivement(Achievement achievement){ // Object -> Achievement
        return achievementMap.containsKey(achievement);
    }

    public Set<Achievement> getAchievements() { // Object -> Achievement
        return achievementMap.keySet();
    }

    public boolean setAchievementValue(Achievement achievement, Boolean completed) { // Object -> Achievement
        if(!hasAchivement(achievement)){
            // TODO : Output a notification (or log) that setting an achievement failed
            //Completed writing log output code, needs testing
            System.out.println("Failed to set achievement: " + achievement.getAchievementName()); // 로그 추가
            return false;
        }
        achievementMap.replace(achievement, completed);
        for (AchievementChangedCallback callback : achievementChangedCallbacks) {
            callback.onAchievementChanged(achievement, completed);
        }
        return true;
    }

    public boolean getAchievementValue(Achievement achievement) { // Object -> Achievement
        // Converts Boolean type to boolean type and returns
        return Boolean.TRUE.equals(achievementMap.getOrDefault(achievement, false));
    }

    /*
    Convenience function
    (Function added to make the code easier to use)
    */

    public boolean completeAchievement(Achievement achievement) { // Added Code
        if (!achievement.isCompleted()) {
            achievement.completeAchievement();
            return setAchievementValue(achievement, true);
        }
        return false;
    }
}