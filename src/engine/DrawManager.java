package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import Currency.RoundState;
import Currency.Gem;
import entity.*;
import screen.Screen;
import javax.imageio.ImageIO;

import screen.Background;
import entity.SkinEntity;
import entity.Skins;

/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class DrawManager {

	/** Singleton instance of the class. */
	public static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static Logger logger;
	/** Graphics context. */
	private static Graphics graphics;
	/** Buffer Graphics. */
	public static Graphics backBufferGraphics;   // Modifying Access Restrictor to public - Lee Hyun Woo
	/** Buffer image. */
	private static BufferedImage backBuffer;
	/** Normal sized font. */
	public static Font fontRegular;  // Modifying Access Restrictor to public - Lee Hyun Woo
	/** Normal sized font properties. */
	public static FontMetrics fontRegularMetrics; // Modifying Access Restrictor to public - Lee Hyun Woo
	/** Big sized font. */
	private static Font fontBig;
	/** Big sized font properties. */
	private static FontMetrics fontBigMetrics;

	/** ###TEAM INTERNATIONAL ### */
	private Background background;
	private BufferedImage backgroundImage;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, Color[][]> spriteMap;
	private int width;
	private int height;
	private int currentOption;

	private Graphics graphicsOverride = null;


	public void setGraphics(Graphics g) {
		this.graphicsOverride = g;
	}


	/** Sprite types. */
	public static enum SpriteType {
		/** Player ship. */
		Ship,
		/** Destroyed player ship. */
		ShipDestroyed,
		/** Player bullet. */
		Bullet,
		/** Enemy bullet. */
		EnemyBullet,
		/** First enemy ship - first form. */
		EnemyShipA1,
		/** First enemy ship - second form. */
		EnemyShipA2,
		/** Second enemy ship - first form. */
		EnemyShipB1,
		/** Second enemy ship - second form. */
		EnemyShipB2,
		/** Third enemy ship - first form. */
		EnemyShipC1,
		/** Third enemy ship - second form. */
		EnemyShipC2,
		/** First explosive enemy ship - first form. */
		ExplosiveEnemyShip1, // Edited by Enemy
		/** First explosive enemy ship - second form. */
		ExplosiveEnemyShip2, // Edited by Enemy
		/** Bonus ship. */
		EnemyShipSpecial,
		/** Destroyed enemy ship. */
		Explosion,
		/**HEART Graphics Produced by Nice HUD Team*/
		Heart, // Please have the Nice HUD team fix it. - Enemy team
		/**Item*/
		Item, // by enemy team
		/**Boss*/
		Boss, // by enemy team
		/** Player Lives. */
		/** Item */
		ItemHeart,
		ShipBarrierStatus,
		ItemCoin,
		ItemPierce,
		ItemBomb,
		ItemBarrier,
		ItemFeverTime,
		// Produced by Starter Team

		// Produced by Starter Team
		/** coin */
		Coin,
		/** add sign */
		AddSign,
		/** Gem - Added by CtrlS */
		Gem,
		ItemSpeedUp, ItemSpeedSlow, Obstacle,

		Skin1, Skin2, Skin3, Skin4, Skin5

	};

	/** For Achievement**/
	static int timer = 100;
	static String achievementText = null;

	/**
	 * Private constructor.
	 *
	 * Modifying Access Restrictor to public
	 * - HUDTeam - LeeHyunWoo
	 */
	public DrawManager() {
		fileManager = Core.getFileManager();
		logger = Core.getLogger();
		logger.info("Started loading resources.");

		try {
			spriteMap = new LinkedHashMap<SpriteType, Color[][]>();
			spriteMap.put(SpriteType.Obstacle, new Color[24][24]); // by Level Design Team  //1
			spriteMap.put(SpriteType.Ship, new Color[16][26]);//2
			spriteMap.put(SpriteType.ShipDestroyed, new Color[16][26]);//3
			spriteMap.put(SpriteType.Bullet, new Color[8][3]);//4
			spriteMap.put(SpriteType.EnemyBullet, new Color[8][5]);//5
			spriteMap.put(SpriteType.EnemyShipA1, new Color[16][24]);//6
			spriteMap.put(SpriteType.EnemyShipA2, new Color[16][24]);//7
			spriteMap.put(SpriteType.EnemyShipB1, new Color[16][24]);//8
			spriteMap.put(SpriteType.EnemyShipB2, new Color[16][24]);//9
			spriteMap.put(SpriteType.EnemyShipC1, new Color[16][24]);//10
			spriteMap.put(SpriteType.EnemyShipC2, new Color[16][24]);//11
			spriteMap.put(SpriteType.ExplosiveEnemyShip1, new Color[16][24]); // Edited by Enemy//12
			spriteMap.put(SpriteType.ExplosiveEnemyShip2, new Color[16][24]); // Edited by Enemy//13
			spriteMap.put(SpriteType.EnemyShipSpecial, new Color[14][32]);//14
			spriteMap.put(SpriteType.Explosion, new Color[14][26]);//15
			spriteMap.put(SpriteType.Heart, new Color[13][8]);//16
			spriteMap.put(SpriteType.Boss, new Color[24][16]); // by Enemy team//17
			spriteMap.put(SpriteType.Coin, new Color[5][5]); // by Starter Team//18
			spriteMap.put(SpriteType.AddSign, new Color[5][5]); // by Starter Team//19
			spriteMap.put(SpriteType.Gem, new Color[7][6]); // CtrlS: res/graphics, line 20
			// by Item team
			spriteMap.put(SpriteType.ItemHeart, new Color[7][5]);//21
			spriteMap.put(SpriteType.ItemBarrier, new Color[9][10]);//22
			spriteMap.put(SpriteType.ItemBomb, new Color[7][9]);//23
			spriteMap.put(SpriteType.ShipBarrierStatus, new Color[13][8]);   // temporary//24
			spriteMap.put(SpriteType.ItemCoin, new Color[7][7]);//25
			spriteMap.put(SpriteType.ItemFeverTime, new Color[9][9]);//26
			spriteMap.put(SpriteType.ItemPierce, new Color[7][7]);//27
			spriteMap.put(SpriteType.ItemSpeedUp, new Color[9][9]);//28
			spriteMap.put(SpriteType.ItemSpeedSlow, new Color[9][9]);//29

			spriteMap.put(SpriteType.Skin1, new Color[13][8]); //30
			spriteMap.put(SpriteType.Skin2, new Color[13][8]); //31
			spriteMap.put(SpriteType.Skin3, new Color[13][8]); //32
			spriteMap.put(SpriteType.Skin4, new Color[13][8]); //33
			spriteMap.put(SpriteType.Skin5, new Color[13][8]); //34

			fileManager.loadSprite(spriteMap);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}
	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	public static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
		Background.getInstance().initialize(frame);
	}

	/**
	 * First part of the drawing process. Initialices buffers, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics
				.fillRect(0, 0, screen.getWidth(), screen.getHeight());

		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);

		// drawBorders(screen);
		// drawGrid(screen);
	}

	/**
	 * Draws the completed drawing on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void completeDrawing(final Screen screen) {
		graphics.drawImage(backBuffer, frame.getInsets().left,
				frame.getInsets().top, frame);
	}

	/**
	 * Draws an entity, using the apropiate image.
	 *
	 * @param entity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public static void drawEntity(final Entity entity, final int positionX,
								  final int positionY) {
		Color[][] image = spriteMap.get(entity.getSpriteType());
		for (int i = 0; i < image.length; i++){
			for (int j = 0; j < image[i].length; j++){
				if (image[i][j] != null){
					backBufferGraphics.setColor(image[i][j]);
					backBufferGraphics.drawRect(positionX + j * 2, positionY
							+ i * 2, 1, 1);
				}
			}
		}
	}
	public static void drawEntityWithGraphics(final Graphics g, final Entity entity, final int positionX, final int positionY) {
		try {
			Color[][] image = spriteMap.get(entity.getSpriteType());
			g.setColor(entity.getColor());

			for (int i = 0; i < image.length; i++) {
				for (int j = 0; j < image[i].length; j++) {
					if (image[i][j]!= null) {
						g.fillRect(positionX + i * 7, positionY + j * 7, 7, 7);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	/**
	 * For debugging purpouses, draws the canvas borders.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
//   @SuppressWarnings("unused")
//   private void drawBorders(final Screen screen) {
//      backBufferGraphics.setColor(Color.GREEN);
//      backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
//      backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
//      backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
//            screen.getWidth() - 1, screen.getHeight() - 1);
//      backBufferGraphics.drawLine(0, screen.getHeight() - 1,
//            screen.getWidth() - 1, screen.getHeight() - 1);
//   }

	/**
	 * For debugging purpouses, draws a grid over the canvas.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
//   @SuppressWarnings("unused")
//   private void drawGrid(final Screen screen) {
//      backBufferGraphics.setColor(Color.DARK_GRAY);
//      for (int i = 0; i < screen.getHeight() - 1; i += 2)
//         backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
//      for (int j = 0; j < screen.getWidth() - 1; j += 2)
//         backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
//   }

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 */
//   public void drawScore(final Screen screen, final int score) {
//      backBufferGraphics.setFont(fontRegular);
//      backBufferGraphics.setColor(Color.WHITE);
//      String scoreString = String.format("%04d", score);
//      backBufferGraphics.drawString(scoreString, screen.getWidth() - 60, 25);
//   }

	/**
	 * Draws number of remaining lives on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param lives
	 *            Current lives.
	 */
	public void drawLives(final Screen screen, final int lives) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
//      backBufferGraphics.drawString(Integer.toString(lives), 20, 25);

		Entity heart = new Entity(0, 0, 13 * 2, 8 * 2, Color.RED) {

		};
		heart.setSpriteType(SpriteType.Heart);

		for (int i = 0; i < lives; i++)
			drawEntity(heart, 20 + 30 * i, 10);
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
		backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Draws game title.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawTitle(final Screen screen) {
		String titleString = "Invaders";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() * 3 / 10);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 4);
	}

	/**
	 * Draws main menu.
	 *
	 * @param screen Screen to draw on.
	 * @param option Option selected.
	 */
	static SpriteType selectedSpriteType = null;
	public void drawMenu(final Screen screen, final int option, final int option2, final int option3, final int option4) {
		String onePlayerModeString = "1 player mode";
		String twoPlayerModeString = "2 player mode";
		String mode = onePlayerModeString;
		String RecentRecord = "Recent Records";
		String playString = "Play";
		String highScoresString = "High scores";
		String exitString = "exit";
		String merchant = "Merchant";
		String bulletCountString = String.format("bullet count up"); // Starter
		String shipSpeedString = String.format("ship speed up"); // Starter
		String attackSpeedString = String.format("attack speed up"); // Starter
		String coinGainString = String.format("coin gain up"); // Starter
		String merchantState = merchant;
		String lockedString = "Locked Skin";
		String customString = "Custom";
		String randomSkinString = "Draw";


		AddSign addSign = new AddSign();


		// Play (Starter)
		if (option == 2 && option2 == 0)
			backBufferGraphics.setColor(Color.CYAN);
		else if (option == 2 && option2 == 1)
			backBufferGraphics.setColor(Color.MAGENTA);
		else
			backBufferGraphics.setColor(Color.WHITE);
		if (option2 == 1) {mode = twoPlayerModeString;} // 2 player mode (Starter), default: 1 player mode
		if (option == 2) {mode = "<- " + mode + " ->";}
		drawCenteredRegularString(screen, mode, screen.getHeight()
				/ 4 * 2); // adjusted Height

		// High scores (Starter)
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, highScoresString, screen.getHeight()
				/ 4 * 2 + fontRegularMetrics.getHeight() * 2); // adjusted Height

		if (option3 == 0) {merchantState = merchant;}
		try {
			if (option3 == 1) {
				merchantState = bulletCountString + MerchantTxt(Core.getUpgradeManager().getBulletCount(),1);
			}
			if (option3 == 2) {
				merchantState = shipSpeedString + MerchantTxt(Core.getUpgradeManager().getSpeedCount(),2);
			}
			if (option3 == 3) {
				merchantState = attackSpeedString + MerchantTxt(Core.getUpgradeManager().getAttackCount(),3);
			}
			if (option3 == 4) {
				merchantState = coinGainString + MerchantTxt(Core.getUpgradeManager().getCoinCount(),4);
			}
			if (option == 4) {
				merchantState = "<- " + merchantState + " ->";
			}
		} catch (IOException e){
			throw new RuntimeException(e);
		}

		if (option == 4 && option3 == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else if (option == 4 && option3 != 0)
			backBufferGraphics.setColor(Color.CYAN);
		else
			backBufferGraphics.setColor(Color.WHITE);

		drawCenteredRegularString(screen, merchantState, screen.getHeight()
				/ 4 * 2 + fontRegularMetrics.getHeight() * 4);
      /*drawEntity(addSign, screen.getWidth()/2 + 50, screen.getHeight()
            / 4 * 2 + fontRegularMetrics.getHeight() * 6 - 12);*/

		// Record scores (Team Clove)
		if (option == 5)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, RecentRecord, screen.getHeight()
				/ 4 * 2 + fontRegularMetrics.getHeight() * 6); // adjusted Height

		// Custom
		if(option == 6) {
			backBufferGraphics.setColor(Color.GREEN);
			int yPosition = screen.getHeight() / 4 * 2 + fontRegularMetrics.getHeight() * 8;

			if (option4 == 0) {
				// Custom 텍스트를 화살표 사이에 표시
				String customDisplay = "<- " + customString + " ->";
				drawCenteredRegularString(screen, customDisplay, yPosition);
			}
			else if(option4 == 1){
				selectedSpriteType = SpriteType.Ship;
				String arrowDisplay = "<-              ->";
				drawCenteredRegularString(screen, arrowDisplay, yPosition);
				// 스킨을 화살표 사이에 그리기
				int positionX = screen.getWidth() / 2 - 30; // 중앙에 위치 조정
				int positionY = yPosition - 18; // 약간 위쪽으로 위치 조정
				SkinEntity tempEntity = new SkinEntity(selectedSpriteType); // 임시 Entity 생성
				drawEntity(tempEntity, positionX, positionY); }
			else if (option4 >= 2 && option4 <= 6) {
				switch (option4) {
					case 2:
						selectedSpriteType = SpriteType.Skin1;
						break;
					case 3:
						selectedSpriteType = SpriteType.Skin2;
						break;
					case 4:
						selectedSpriteType = SpriteType.Skin3;
						break;
					case 5:
						selectedSpriteType = SpriteType.Skin4;
						break;
					case 6:
						selectedSpriteType = SpriteType.Skin5;
						break;
					default:
						selectedSpriteType = SpriteType.Ship;
						break;
				}
				if (Skins.unlockedSkins.contains(selectedSpriteType)) {
					// 중앙에 화살표를 표시
					String arrowDisplay = "<-              ->";
					drawCenteredRegularString(screen, arrowDisplay, yPosition);

					// 스킨을 화살표 사이에 그리기
					int positionX = screen.getWidth() / 2 - 16; // 중앙에 위치 조정
					int positionY = yPosition - 10; // 약간 위쪽으로 위치 조정
					SkinEntity tempEntity = new SkinEntity(selectedSpriteType); // 임시 Entity 생성
					drawEntity(tempEntity, positionX, positionY); // drawEntity 메서드 호출
				}
				else {
					String lockedDisplay = "<- " + lockedString + " " + (option4 - 1) + " ->";
					drawCenteredRegularString(screen, lockedDisplay, yPosition);
					selectedSpriteType = SpriteType.Ship;
				}
			} else if (option4 == 7) {
				int drawCount = Skins.unlockedSkins.size() + 1;
				int drawCost = 50 * drawCount;
				String randomDisplay;

				if (Skins.unlockedSkins.size() == 5) {
					randomDisplay = "<- DRAW MAX ->";
				} else {
					randomDisplay = "<- DRAW -" + drawCost + " COIN ->";
				}
				backBufferGraphics.setColor(Color.magenta);
				drawCenteredRegularString(screen, randomDisplay, yPosition);
			}


		}
		else {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, customString, screen.getHeight()
					/ 4 * 2 + fontRegularMetrics.getHeight() * 8);
		}

		// Exit (Starter)
		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, exitString, screen.getHeight()
				/ 4 * 2 + fontRegularMetrics.getHeight() * 10); // adjusted Height


	}

	public static SpriteType getselectedSpriteType(){
		return selectedSpriteType != null ? selectedSpriteType : SpriteType.Ship;}
	/**
	 * Draws game results.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining
	 *            Lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param accuracy
	 *            Total accuracy.
	 */

	// Ctrl S - add Coin String
	public void drawResults(final Screen screen, final int score,
							final int livesRemaining, final int shipsDestroyed,
							final float accuracy, final GameState gameState) {
		String scoreString = String.format("score: %04d", score);
		String livesRemainingString = "lives remaining: " + livesRemaining;
		String shipsDestroyedString = "enemies destroyed: " + shipsDestroyed;
		String accuracyString = String
				.format("accuracy: %.2f%%", accuracy * 100);
		String coinString = "Total earned  $ " + gameState.getCoin() + "  Coins!";

		int height = 4;

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, scoreString, screen.getHeight()
				/ height);
		drawCenteredRegularString(screen, livesRemainingString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 2);
		drawCenteredRegularString(screen, shipsDestroyedString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 4);
		//Change the accuracy String when player does not shoot any bullet
		if (accuracy != accuracy) {
			accuracyString = "You didn't shoot any bullet.";
		}
		drawCenteredRegularString(screen, accuracyString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 6);
		drawCenteredRegularString(screen, coinString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 8);
	}

	/**
	 * Draws interactive characters for name input.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param name
	 *            Current name selected.
	 * @param nameCharSelected
	 *            Current character selected for modification.
	 */
	// Ctrl-S : move to lower position
	public void drawNameInput(final Screen screen, final char[] name,
							  final int nameCharSelected) {
		String newRecordString = "New Record!";
		String introduceNameString = "Introduce name:";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredRegularString(screen, newRecordString, screen.getHeight()
				/ 4 + fontRegularMetrics.getHeight() * 12);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, introduceNameString,
				screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 14);

		// 3 letters name.
		int positionX = screen.getWidth()
				/ 2
				- (fontRegularMetrics.getWidths()[name[0]]
				+ fontRegularMetrics.getWidths()[name[1]]
				+ fontRegularMetrics.getWidths()[name[2]]
				+ fontRegularMetrics.getWidths()[' ']) / 2;

		for (int i = 0; i < 3; i++) {
			if (i == nameCharSelected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
			positionX = i == 0 ? positionX
					: positionX
					+ (fontRegularMetrics.getWidths()[name[i - 1]]
					+ fontRegularMetrics.getWidths()[' ']) / 2;

			backBufferGraphics.drawString(Character.toString(name[i]),
					positionX,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight()
							* 16);
		}
	}

	/**
	 * Draws basic content of game end screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param acceptsInput
	 *            If the screen accepts input.
	 */
	// CtrlS
	public void drawGameEnd(final Screen screen, final boolean acceptsInput, boolean isGameClear) {
		String gameEndString = isGameClear ? "Game Clear" : "Game Over";
		String continueOrExitString =
				"Press Space to play again, Escape to exit";
		String lostBonus = "You lost your Bonus on this level. Try Harder!";

		int height = 4;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gameEndString, screen.getHeight()
				/ height - fontBigMetrics.getHeight() * 2);
		if (!isGameClear) {
			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularString(screen, lostBonus, screen.getHeight()
					/ height - fontRegularMetrics.getHeight() - 20);
		}

		if (acceptsInput)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, continueOrExitString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}

	/**
	 * Draws high score screen title and instructions.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawHighScoreMenu(final Screen screen) {
		String highScoreString = "High Scores";
		String instructionsString = "Press Space to return";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, highScoreString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5);
	}

	/**
	 * Draws recent score(record) screen title and instructions.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * Team Clove
	 */
	public void drawRecordMenu(final Screen screen) {
		String recentScoreString = "Recent Records";
		String instructionsString = "Press Space to return";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, recentScoreString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5);
	}

	/**
	 * Draws high scores.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
	public void drawHighScores(final Screen screen,
							   final List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";

		for (Score score : highScores) {
			scoreString = String.format("%s        %04d           %04d", score.getName(),
					score.getScore(), score.getPlayTime());
			drawCenteredRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			i++;
		}
	}

	/**
	 * Draws recent scores.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param recentScores
	 *            List of recent scores.
	 * Team Clove
	 */
	public void drawRecentScores(final Screen screen,
								 final List<Score> recentScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		boolean isFirstLine = true;
		int[] attributeXPosition = {50, 200, 295, 380, 480};
		int[] instanceXPostition = {25, 205, 300, 400, 515};

		if (isFirstLine) { // Create Header
			String[] Attribute = {"Date", "Score", "Level", "Destroy"};
			for(int k=0; k<4; k++){
				drawRightedRegularString(screen, Attribute[k], attributeXPosition[k],
						screen.getHeight() / 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			}
			isFirstLine = false;

			i++;
		}

		for (Score score : recentScores) {
			String[] Instance = new String[4];
			Instance[0] = String.format("%s",score.getDate());
			Instance[1] = String.format("%04d",score.getScore());
			Instance[2] = String.format("%04d",score.getHighestLevel());
			Instance[3] = String.format("%04d", score.getShipDestroyed());

			for(int k=0; k<4; k++){
				drawRightedRegularString(screen, Instance[k], instanceXPostition[k],
						screen.getHeight() / 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			}
			i++;
		}
	}


	/**
	 * Draws a righted string on regular font
	 *
	 * @param screen
	 *             Screen to draw on.
	 * @param string
	 *             String to draw.
	 * @param height
	 *             Height of the drawing.
	 *
	 *       //Clove
	 */
	public void drawRightedRegularString(final Screen screen,
										 final String string, final int width, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, width, height);
	}

	/**
	 * Draws a centered string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws a centered string on big font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
									  final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Countdown to game start.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 */
	public void drawCountDown(final Screen screen, final int level,
							  final int number, final boolean bonusLife) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);
		if (number >= 4)
			// Adjust the numbers here to match the appropriate boss levels.
			if (level == 3) { // Edited by team Enemy // ex) (level == 3 || level == 6 || level == 9)
				drawCenteredBigString(screen, "BOSS",
						screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
			} else if (!bonusLife) {
				drawCenteredBigString(screen, "Level " + level,
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			} else {
				drawCenteredBigString(screen, "Level " + level
								+ " - Bonus life!",
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			}
		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3);
	}

	// Ctrl-S
	/**
	 * Show ReceiptScreen
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param roundState
	 *            State of one game round
	 */

	public void drawReceipt(final Screen screen, final RoundState roundState, final GameState gameState) {
		String stageScoreString = "Stage Score";
		String totalScoreString = "Total Score : ";
		String stageCoinString = "Coins Obtained";
		String instructionsString = "Press Space to Continue to get more coin!";
		String hitrateBonusString = "HitRate Bonus: $ " + roundState.getAccuracyBonus_amount() + "  Coins";
		String timeBonusString = "Time Bonus: $ " + roundState.getTimeBonus_amount() + "  Coins";
		String levelBonusString = "Level Bonus: $ " + roundState.getLevelBonus_amount() + "  Coins";
		//draw Score part
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, stageScoreString, screen.getHeight() / 8);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredBigString(screen, Integer.toString(roundState.getRoundScore()), screen.getHeight() / 8 + fontBigMetrics.getHeight() / 2 * 3);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, totalScoreString + gameState.getScore(), screen.getHeight() / 8 + fontRegularMetrics.getHeight() / 2 * 7);
		//draw Coin part
		backBufferGraphics.setColor(Color.LIGHT_GRAY);
		drawCenteredBigString(screen, stageCoinString, screen.getHeight() / 3 - 30);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredBigString(screen, Integer.toString(roundState.getBaseCoin_amount()), (screen.getHeight() / 3) - 30 + fontBigMetrics.getHeight() / 2 * 3);

		//draw HitRate Bonus part
		if (roundState.getAccuracyBonus_amount() != 0) {
			backBufferGraphics.setColor(Color.LIGHT_GRAY);
			backBufferGraphics.setFont(fontRegular);
			backBufferGraphics.drawString(hitrateBonusString, screen.getWidth() / 2 - fontRegularMetrics.stringWidth(hitrateBonusString) / 2, (screen.getHeight() / 3) - 30 + fontRegularMetrics.getHeight() / 2 * 7);
		}
		//draw Time Bonus part
		if (roundState.getTimeBonus_amount() != 0) {
			backBufferGraphics.setColor(Color.LIGHT_GRAY);
			backBufferGraphics.setFont(fontRegular);
			backBufferGraphics.drawString(timeBonusString, screen.getWidth() / 2 - fontRegularMetrics.stringWidth(timeBonusString) / 2, (screen.getHeight() / 3) - 30 + fontRegularMetrics.getHeight() / 2 * 9);
		}
		//draw level Bonus part
		if (roundState.getLevelBonus_amount() != 0) {
			backBufferGraphics.setColor(Color.LIGHT_GRAY);
			backBufferGraphics.setFont(fontRegular);
			backBufferGraphics.drawString(levelBonusString, screen.getWidth() / 2 - fontRegularMetrics.stringWidth(levelBonusString) / 2, (screen.getHeight() / 3) - 30 + fontRegularMetrics.getHeight() / 2 * 11);

		}
		//draw Total coins part
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, "Total Round Coins", screen.getHeight() / 3 + 120);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredBigString(screen, Integer.toString(roundState.getRoundCoin()), screen.getHeight() / 3 + 120 + fontBigMetrics.getHeight() / 2 * 3);

		//draw instructionString part
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}

	/**
	 * draw current coin.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param coin
	 *            Current Coin.
	 */
	public void drawCurrentCoin(final Screen screen , final int coin) {
		Coin coinImage = new Coin();
		int coinX = 10; //Starter edited
		int coinY = 7; //Adjust the y position value - Starter
		drawEntity(coinImage, coinX, coinY);
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(coin), coinX + coinImage.getWidth() + 10, 20);
	}

	public void drawCurrentGem(final Screen screen , final int gem) {
		Gem gemImage = new Gem();
		int coinX = 10; //Starter edited
		int coinY = 24; //Adjust the y position value - Starter
		drawEntity(gemImage, coinX, coinY);
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(gem), coinX + gemImage.getWidth() + 10, 35);
	}
	/**
	 * ### TEAM INTERNATIONAL ###
	 * Background draw and update method
	 */

	public void loadBackground(int levelNumber) {
		background = Background.getInstance();
		// I still have no clue how relative pathing or class pathing works
		InputStream imageStream = Background.getBackgroundImageStream(levelNumber);
		try {
			assert imageStream != null;
			backgroundImage = ImageIO.read(imageStream);
			background.backgroundReset(backgroundImage.getHeight(),backgroundImage.getWidth());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void drawBackground(boolean backgroundMoveRight, boolean backgroundMoveLeft) {
		int verticalOffset = background.getVerticalOffset();
		int horizontalOffset = background.getHorizontalOffset(backgroundMoveRight, backgroundMoveLeft);

		backBufferGraphics.drawImage(backgroundImage, horizontalOffset, verticalOffset, null);
	}

	/**
	 * ### TEAM INTERNATIONAL ###
	 *
	 * Wave draw method
	 * **/
	public void drawWave(final Screen screen, final int wave, final int number) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);
		if (number >= 4)

			drawCenteredBigString(screen, "Wave " + wave,
					screen.getHeight() / 2
							+ fontBigMetrics.getHeight() / 3);

		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3);
	}


	/**
	 * Draw the item that player got
	 *
	 * @param screen
	 *           Screen to draw on.
	 *
	 * HUD Team - Jo Minseo
	 */
	public void drawItem(final Screen screen){
		//Bomb
		Entity itemBomb = new Entity(0, 0, 13 * 2, 8 * 2, Color.gray) {

		};
		itemBomb.setSpriteType(SpriteType.ItemBomb);

		if(Bomb.getIsBomb() && Bomb.getCanShoot()){
			drawEntity(itemBomb, screen.getWidth() / 5, screen.getHeight() - 50);
		}
	}

	public String MerchantTxt(int count, int number){
		if ((number == 1 && count > 3) ||
				(count != 0 && Core.getUpgradeManager().LevelCalculation(count) > 9)){
			return " max";
		}
		else {
			return " +" + Core.getUpgradeManager().LevelCalculation
					(count) + "   " + Core.getUpgradeManager().Price(number) + " "
					+ Core.getUpgradeManager().whatMoney(count,number);
		}
	}


	/** For achievement**/
	public static int getAchievementTimer(){return timer;}
	public static String getAchievementText(){return achievementText;}
	public static void addAchievementTimer(){timer++;}
	public static void achieveAchievement(String Text){
		timer = 0;
		achievementText = Text;
		// Sound Operator
		SoundManager.getInstance().playES("achievement");
	}
	public void drawOption(final Screen screen) {
		// OPTION 제목 그리기
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, "OPTION", screen.getHeight() / 8); // 화면 상단에 "OPTION" 표시

		// 'PRESS P TO RESUME', 'PRESS Q TO TERMINATE' 안내문구 그리기
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, "PRESS P TO RESUME", screen.getHeight() / 5);
		drawCenteredRegularString(screen, "PRESS Q TO TERMINATE", screen.getHeight() / 4);// 화면 중간에 안내문구 표시
	}

	public void drawSoundOption(final Screen screen, final int option, final int volumeIndex, final int BGMIndex, final List<String> bgmOptions) {
		String bgmString = "Background Music";
		String selectedBGM = bgmOptions.get(BGMIndex);

		String[] volumes = {"VOLUME0", "VOLUME1", "VOLUME2", "VOLUME3", "VOLUME4", "VOLUME5"};
		String volume = volumes[volumeIndex];

		if (option == 1)
			backBufferGraphics.setColor(Color.CYAN);
		else
			backBufferGraphics.setColor(Color.WHITE);

		String bgmDisplay = "< " + selectedBGM + " >";
		drawCenteredRegularString(screen, bgmString + ": " + bgmDisplay, screen.getHeight() / 4 * 2);

		backBufferGraphics.setColor(Color.PINK);
		drawCenteredRegularString(screen, "VOLUME", screen.getHeight() / 4 * 2 + fontRegularMetrics.getHeight() * 6);

		if (option == 2) {
			switch (volumeIndex) {
				case 0:
					backBufferGraphics.setColor(new Color(200, 200, 200));
					break;
				case 1:
					backBufferGraphics.setColor(new Color(173, 216, 255));
					break;
				case 2:
					backBufferGraphics.setColor(new Color(144, 238, 144));
					break;
				case 3:
					backBufferGraphics.setColor(new Color(64, 224, 208));
					break;
				case 4:
					backBufferGraphics.setColor(new Color(255, 255, 0));
					break;
				case 5:
					backBufferGraphics.setColor(new Color(255, 0, 0));
					break;
				default:
					backBufferGraphics.setColor(Color.WHITE);
			}
			volume = "<- " + volume + " ->";
		} else {
			backBufferGraphics.setColor(Color.WHITE); // 기본 색상
		}
		drawCenteredRegularString(screen, volume, screen.getHeight() / 4 * 2 + fontRegularMetrics.getHeight() * 8);

	}
}
