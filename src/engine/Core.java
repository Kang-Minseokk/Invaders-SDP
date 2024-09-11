package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.GameScreen;
import screen.HighScoreScreen;
import screen.ScoreScreen;
import screen.Screen;
import screen.TitleScreen;

/**
 * Implements core game logic.
 * 게임의 핵심 로직을 구현합니다.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class Core {

	/** Width of current screen. */
	/** 현재 화면의 너비 */
	private static final int WIDTH = 448;
	/** Height of current screen. */
	/** 현재 화면의 높이 */
	private static final int HEIGHT = 520;
	/** Max fps of current screen. */
	/** 현재 화면의 최대 프레임 수 */
	private static final int FPS = 60;

	/** Max lives. */
	/** 최대 생명 수 */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	/** 추가 생명을 받을 수 있는 레벨 간격 */
	private static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Total number of levels. */
	/** 총 레벨 수 */
	private static final int NUM_LEVELS = 7;

	/** Difficulty settings for level 1. */
	/** 레벨 1에 대한 난이도 설정. GameSettings class 의 각 매개변수를 이용함. */
	private static final GameSettings SETTINGS_LEVEL_1 =
			new GameSettings(5, 4, 60, 2000);
	/** Difficulty settings for level 2. */
	/** 레벨 2에 대한 난이도 설정 */
	private static final GameSettings SETTINGS_LEVEL_2 =
			new GameSettings(5, 5, 50, 2500);
	/** Difficulty settings for level 3. */
	/** 레벨 3에 대한 난이도 설정 */
	private static final GameSettings SETTINGS_LEVEL_3 =
			new GameSettings(6, 5, 40, 1500);
	/** Difficulty settings for level 4. */
	/** 레벨 4에 대한 난이도 설정 */
	private static final GameSettings SETTINGS_LEVEL_4 =
			new GameSettings(6, 6, 30, 1500);
	/** Difficulty settings for level 5. */
	/** 레벨 5에 대한 난이도 설정 */
	private static final GameSettings SETTINGS_LEVEL_5 =
			new GameSettings(7, 6, 20, 1000);
	/** Difficulty settings for level 6. */
	/** 레벨 6에 대한 난이도 설정 */
	private static final GameSettings SETTINGS_LEVEL_6 =
			new GameSettings(7, 7, 10, 1000);
	/** Difficulty settings for level 7. */
	/** 레벨 7에 대한 난이도 설정 */
	private static final GameSettings SETTINGS_LEVEL_7 =
			new GameSettings(8, 7, 2, 500);

	/** Frame to draw the screen on. */
	/** 화면을 그릴 프레임 */
	private static Frame frame;
	/** Screen currently shown. */
	/** 현재 표시되고 있는 화면 */
	private static Screen currentScreen;
	/** Difficulty settings list. */
	/** 난이도 설정 리스트 */
	private static List<GameSettings> gameSettings;
	/** Application logger. */
	/** 애플리케이션 로거 */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	/** 디스크에 기록하는 로거 핸들러 */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	/** 콘솔에 출력하는 로거 핸들러 */
	private static ConsoleHandler consoleHandler;


	/**
	 * Test implementation.
	 * 테스트 구현입니다.
	 *
	 * @param args
	 *            Program args, ignored.
	 *            프로그램 인자, 무시됩니다.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			// 예외 처리 추가 필요
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		gameSettings = new ArrayList<GameSettings>();
		gameSettings.add(SETTINGS_LEVEL_1);
		gameSettings.add(SETTINGS_LEVEL_2);
		gameSettings.add(SETTINGS_LEVEL_3);
		gameSettings.add(SETTINGS_LEVEL_4);
		gameSettings.add(SETTINGS_LEVEL_5);
		gameSettings.add(SETTINGS_LEVEL_6);
		gameSettings.add(SETTINGS_LEVEL_7);

		GameState gameState;

		int returnCode = 1;
		do {
			gameState = new GameState(1, 0, MAX_LIVES, 0, 0);

			switch (returnCode) {
				case 1:
					// Main menu.
					// 메인 메뉴
					currentScreen = new TitleScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " title screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing title screen.");
					break;
				case 2:
					// Game & score.
					// 게임 및 점수. 남은 생명이 1 이상이거나, 레벨이 NUM_LEVELS 이하일 경우 게임 진행(while문으로 조건 확인).

					do {
						// One extra live every few levels.
						// 몇 레벨마다 추가 생명 제공
						boolean bonusLife = gameState.getLevel()
								% EXTRA_LIFE_FRECUENCY == 0
								&& gameState.getLivesRemaining() < MAX_LIVES;

						currentScreen = new GameScreen(gameState,
								gameSettings.get(gameState.getLevel() - 1),
								bonusLife, width, height, FPS);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " game screen at " + FPS + " fps.");
						frame.setScreen(currentScreen);
						LOGGER.info("Closing game screen.");

						gameState = ((GameScreen) currentScreen).getGameState();

						gameState = new GameState(gameState.getLevel() + 1,
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed());

					} while (gameState.getLivesRemaining() > 0
							&& gameState.getLevel() <= NUM_LEVELS);

					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(width, height, FPS, gameState);
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing score screen.");
					break;
				case 3:
					// High scores.
					// 하이 스코어. currentScreen에 HighScoreScreen을 출력(너비, 높이, FPS 매개변수 이용)
					currentScreen = new HighScoreScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " high score screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing high score screen.");
					break;
				default:
					break;
			}

		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 * 생성자, 호출되지 않습니다.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 로거에 대한 접근을 제어합니다.
	 *
	 * @return Application logger.
	 *         애플리케이션 로거를 반환합니다.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 드로잉 매니저에 대한 접근을 제어합니다.
	 *
	 * @return Application draw manager.
	 *         애플리케이션 드로잉 매니저를 반환합니다.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 입력 매니저에 대한 접근을 제어합니다.
	 *
	 * @return Application input manager.
	 *         애플리케이션 입력 매니저를 반환합니다.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 파일 매니저에 대한 접근을 제어합니다.
	 *
	 * @return Application file manager.
	 *         애플리케이션 파일 매니저를 반환합니다.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 새로운 쿨다운 생성을 제어합니다.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 *            쿨다운 지속 시간 (밀리초 단위).
	 * @return A new cooldown.
	 *         새로운 쿨다운을 반환합니다.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 변동성을 가진 새로운 쿨다운 생성을 제어합니다.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 *            쿨다운 지속 시간 (밀리초 단위).
	 * @param variance
	 *            Variation in the cooldown duration.
	 *            쿨다운 지속 시간의 변동성.
	 * @return A new cooldown with variance.
	 *         변동성을 가진 새로운 쿨다운을 반환합니다.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
											   final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}
