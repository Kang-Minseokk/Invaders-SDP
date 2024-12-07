package entity;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.Timer;

import Enemy.*;

import engine.SoundManager;
import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.DrawManager.SpriteType;
import engine.GameSettings;

import Achievement.ScoreManager;

import screen.Screen;

import static java.lang.Math.*;
import Enemy.PiercingBulletPool;

/**
 * Groups enemy ships into a formation that moves together.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class EnemyShipFormation implements Iterable<EnemyShip> {
	private boolean isCircle = false;
	// Sound Operator
	private static SoundManager sm;
	/** Number of iteration of movement */
	private int iteration = 0;

	/** Initial position in the x-axis. */
	private static final int INIT_POS_X = 60;
	/** Initial position in the y-axis. */
	private static final int INIT_POS_Y = 100;
	/** Distance between ships. */
	private static final int SEPARATION_DISTANCE = 60;
	private static final int SEPARATION_DISTANCE_CIRCLE = 70;
	/** Radius of circle */
	private int RADIUS=0;
	private int MINIRADIUS= 0;

	/** Proportion of C-type ships. */
	private static final double PROPORTION_C = 0.2;
	/** Proportion of B-type ships. */
	private static final double PROPORTION_B = 0.4;
	/** Lateral speed of the formation. */
	private static final int X_SPEED = 8;
	/** Downwards speed of the formation. */
	private static final int Y_SPEED = 4;
	/** Speed of the bullets shot by the members. */
	private static final int BULLET_SPEED = 4;
	/** Proportion of differences between shooting times. */
	private static final double SHOOTING_VARIANCE = .2;
	/** Margin on the sides of the screen. */
	private static final int SIDE_MARGIN = 20;
	/** Margin on the bottom of the screen. */
	private static final int BOTTOM_MARGIN = 80;
	/** Distance to go down each pass. */
	private static final int DESCENT_DISTANCE = 20;
	/** Minimum speed allowed. */
	private static final int MINIMUM_SPEED = 10;

	/** DrawManager instance. */
	private DrawManager drawManager;
	/** Application logger. */
	private Logger logger;
	/** Screen to draw ships on. */
	private Screen screen;

	/** List of enemy ships forming the formation. */
	private List<List<EnemyShip>> enemyShips;
	/** Minimum time between shots. */
	private List<SpeedItem> activeSpeedItems;
	private Cooldown shootingCooldown;
	/** Number of ships in the formation - horizontally. */
	private int nShipsWide;
	/** Number of ships in the formation - vertically. */
	private int nShipsHigh;
	/** Time between shots. */
	private int shootingInterval;
	/** Variance in the time between shots. */
	private int shootingVariance;
	/** Initial ship speed. */
	private int baseSpeed;
	/** Speed of the ships. */
	private int movementSpeed;
	/** Current direction the formation is moving on. */
	private Direction currentDirection;
	/** Direction the formation was moving previously. */
	private Direction previousDirection;
	/** Interval between movements, in frames. */
	private int movementInterval;
	/** Total width of the formation. */
	private int width;
	/** Total height of the formation. */
	private int height;
	/** Position in the x-axis of the upper left corner of the formation. */
	private int positionX;
	/** Position in the y-axis of the upper left corner of the formation. */
	private int positionY;
	/** Width of one ship. */
	private int shipWidth;
	/** Height of one ship. */
	private int shipHeight;
	/** List of ships that are able to shoot. */
	private List<EnemyShip> shooters;
	/** Number of not destroyed ships. */
	private int shipCount;

	private ScoreManager scoreManager; //add by team Enemy
	private ItemManager itemManager; //add by team Enemy

	/** Directions the formation can move. */
	private enum Direction {
		/** Movement to the right side of the screen. */
		RIGHT,
		/** Movement to the left side of the screen. */
		LEFT,
		/** Movement to the bottom of the screen. */
		DOWN
	};

	//add by team Enemy
	//Setting Up Score Manager and ItemManager
	public void setScoreManager (ScoreManager scoreManager){
		this.scoreManager = scoreManager;
	}
	public void setItemManager (ItemManager itemManager){//add by team Enemy
		this.itemManager = itemManager;
	}

	/**
	 * Constructor, sets the initial conditions.
	 *
	 * @param gameSettings
	 *            Current game settings.
	 */
	public EnemyShipFormation(final GameSettings gameSettings) {
		this.drawManager = Core.getDrawManager();
		this.logger = Core.getLogger();
		this.enemyShips = new ArrayList<List<EnemyShip>>();
		this.activeSpeedItems = new ArrayList<>();
		this.currentDirection = Direction.RIGHT;
		this.movementInterval = 0;
		this.nShipsWide = gameSettings.getFormationWidth();
		this.nShipsHigh = gameSettings.getFormationHeight();
		this.shootingInterval = gameSettings.getShootingFrecuency();
		this.shootingVariance = (int) (gameSettings.getShootingFrecuency()
				* SHOOTING_VARIANCE);
		this.baseSpeed = gameSettings.getBaseSpeed();
		this.movementSpeed = this.baseSpeed;
		this.positionX = INIT_POS_X;
		this.positionY = INIT_POS_Y;
		this.shooters = new ArrayList<EnemyShip>();
		this.shipCount = 0;
		SpriteType spriteType = null;
		int hp=1;// Edited by Enemy
		Random rand= new Random();
		int n = rand.nextInt(2);
		if(n%2==1){ isCircle=true;
			this.logger.info("cercle"+ 2);
		}
		else isCircle=false;

		if(isCircle){
			RADIUS=gameSettings.getFormationHeight()*6;
			MINIRADIUS= gameSettings.getFormationHeight()*2;}

		this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
				+ " ship formation in (" + positionX + "," + positionY + ")");

		// Each sub-list is a column on the formation.
		for (int i = 0; i < this.nShipsWide; i++)
			this.enemyShips.add(new ArrayList<EnemyShip>());

		for (List<EnemyShip> column : this.enemyShips) {
			int x=0;
			int y=0;
			for (int i = 0; i < this.nShipsHigh; i++) {
				double angle = 2* PI * i / this.nShipsHigh;

				if (i / (float) this.nShipsHigh < PROPORTION_C)
					if (shipCount == (nShipsHigh*1)+1 ||shipCount == (nShipsHigh*3)+1) //Edited by Enemy
						spriteType = SpriteType.ExplosiveEnemyShip1;
					else if (i / (float) this.nShipsHigh < PROPORTION_C)
						spriteType = SpriteType.EnemyShipC1;
					else if (i / (float) this.nShipsHigh < PROPORTION_B + PROPORTION_C)
						spriteType = SpriteType.EnemyShipB1;
					else
						spriteType = SpriteType.EnemyShipA1;
				if(isCircle){
					x = (int) round(RADIUS * cos(angle) + positionX + ( SEPARATION_DISTANCE_CIRCLE* this.enemyShips.indexOf(column)));
					y = (int) (RADIUS * sin(angle)) + positionY;}
				else{
					x = positionX + (SEPARATION_DISTANCE * this.enemyShips.indexOf(column));
					y = positionY+ i*SEPARATION_DISTANCE;
				}

				if(shipCount == nShipsHigh*(nShipsWide/2))
					hp = 2; // Edited by Enemy, It just an example to insert EnemyShip that hp is 2.

				column.add(new EnemyShip(x, y, spriteType,hp,this.enemyShips.indexOf(column),i));// Edited by Enemy
				this.shipCount++;
				hp = 1;// Edited by Enemy
			}
		}


		this.shipWidth = this.enemyShips.get(0).get(0).getWidth();
		this.shipHeight = this.enemyShips.get(0).get(0).getHeight();

		this.width = (this.nShipsWide - 1) * SEPARATION_DISTANCE
				+ this.shipWidth;
		this.height = (this.nShipsHigh - 1) * SEPARATION_DISTANCE
				+ this.shipHeight;

		for (List<EnemyShip> column : this.enemyShips)
			this.shooters.add(column.get(column.size() - 1));

	}

	/**
	 * Associates the formation to a given screen.
	 * 
	 * @param newScreen
	 *            Screen to attach.
	 */
	public final void attach(final Screen newScreen) {
		screen = newScreen;
	}

	/**
	 * Draws every individual component of the formation.
	 */
	public final void draw() {
		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				drawManager.drawEntity(enemyShip, enemyShip.getPositionX(),
						enemyShip.getPositionY());
	}

	/**
	 * Updates the position of the ships.
	 */
	public final void update() {
		if(this.shootingCooldown == null) {
			this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.shootingCooldown.reset();
		}
		
		cleanUp();

		int movementX = 0;
		int movementY = 0;
		double remainingProportion = (double) this.shipCount
				/ (this.nShipsHigh * this.nShipsWide);
		this.movementSpeed = (int) (pow(remainingProportion, 2)
				* this.baseSpeed);
		this.movementSpeed += MINIMUM_SPEED;
		
		movementInterval++;
		if (movementInterval >= this.movementSpeed) {
			movementInterval = 0;

			int circleFormationPadding = 0;

			if (isCircle) {
				circleFormationPadding = 45;
			}

			boolean isAtBottom = positionY
					+ this.height + RADIUS > screen.getHeight() - BOTTOM_MARGIN;
			boolean isAtRightSide = positionX
					+ this.width + RADIUS >= screen.getWidth() - SIDE_MARGIN;
			boolean isAtLeftSide = positionX - RADIUS - circleFormationPadding <= SIDE_MARGIN;
			boolean isAtHorizontalAltitude = positionY % DESCENT_DISTANCE == 0;

			if (currentDirection == Direction.DOWN) {
				if (isAtHorizontalAltitude)
					if (previousDirection == Direction.RIGHT) {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 1");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 2");
					}
			} else if (currentDirection == Direction.LEFT) {
				if (isAtLeftSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 3");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 4");
					}
			} else {
				if (isAtRightSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 5");
					} else {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 6");
					}
			}

			if (currentDirection == Direction.RIGHT)
				movementX = X_SPEED;
			else if (currentDirection == Direction.LEFT)
				movementX = -X_SPEED;
			else
				movementY = Y_SPEED;


			positionX += movementX;
			positionY += movementY;

			// Cleans explosions.
			List<EnemyShip> destroyed;
			for (List<EnemyShip> column : this.enemyShips) {
				destroyed = new ArrayList<EnemyShip>();
				for (EnemyShip ship : column) {
					if (ship != null && ship.isDestroyed()) {
						destroyed.add(ship);
						this.logger.info("Removed enemy "
								+ column.indexOf(ship) + " from column "
								+ this.enemyShips.indexOf(column));
					}
				}
				column.removeAll(destroyed);
			}
			double angle = (PI/this.nShipsHigh);
			int temp=0;
			iteration++;
			for (List<EnemyShip> column : this.enemyShips){
				temp=0;
				for (EnemyShip enemyShip : column) {
					double currentAngle = angle * (temp+iteration);
					int distanceX = movementX + (int) (MINIRADIUS * cos(currentAngle));
					int distanceY = movementY + (int) (MINIRADIUS * sin(currentAngle));

					if (distanceX + enemyShip.positionX > screen.getWidth() - SIDE_MARGIN || distanceX + enemyShip.positionX < SIDE_MARGIN) {
						distanceX = 0;

					} else if (distanceY + enemyShip.positionY > screen.getHeight() - BOTTOM_MARGIN) {
						distanceY = 0;
					}

					enemyShip.move(
							distanceX,
							distanceY
					);
					enemyShip.update();
					temp++;
				}
			}
		}
	}

	/**
	 * Cleans empty columns, adjusts the width and height of the formation.
	 */
	private void cleanUp() {
		Set<Integer> emptyColumns = new HashSet<Integer>();
		int maxColumn = 0;
		int minPositionY = Integer.MAX_VALUE;
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				// Height of this column
				int columnSize = column.get(column.size() - 1).positionY
						- this.positionY + this.shipHeight;
				maxColumn = max(maxColumn, columnSize);
				minPositionY = min(minPositionY, column.get(0)
						.getPositionY());
			} else {
				// Empty column, we remove it.
				emptyColumns.add(this.enemyShips.indexOf(column));
			}
		}
		for (int index : emptyColumns) {
			this.enemyShips.remove(index);
			logger.info("Removed column " + index);
		}

		int leftMostPoint = 0;
		int rightMostPoint = 0;
		
		for (List<EnemyShip> column : this.enemyShips) {
			if (!column.isEmpty()) {
				if (leftMostPoint == 0)
					leftMostPoint = column.get(0).getPositionX();
				rightMostPoint = column.get(0).getPositionX();
			}
		}

		this.width = rightMostPoint - leftMostPoint + this.shipWidth;
		this.height = maxColumn;

		this.positionX = leftMostPoint;
		this.positionY = minPositionY;
	}

	/**
	 * Shoots a bullet downwards.
	 * 
	 * @param bullets
	 *            Bullets set to add the bullet being shot.
	 */
	public final void shoot(final Set<PiercingBullet> bullets) { // Edited by Enemy
		// For now, only ships in the bottom row are able to shoot.
		if (!shooters.isEmpty()) { // Added by team Enemy
			int index = (int) (random() * this.shooters.size());
			EnemyShip shooter = this.shooters.get(index);
			if (this.shootingCooldown.checkFinished()) {
				this.shootingCooldown.reset();
				sm = SoundManager.getInstance();
				sm.playES("Enemy_Gun_Shot_1_ES");
				bullets.add(PiercingBulletPool.getPiercingBullet( // Edited by Enemy
						shooter.getPositionX() + shooter.width / 2,
						shooter.getPositionY(),
						BULLET_SPEED,
						0)); // Edited by Enemy
			}
		}
	}

	/**
	 * Destroys a ship.
	 *
	 * @param destroyedShip
	 *            Ship to be destroyed.
	 */
	public final void destroy(final EnemyShip destroyedShip) {
			if (Bomb.getIsBomb()) {		// team Inventory
				Bomb.destroyByBomb(enemyShips, destroyedShip, this.itemManager , this.logger);
			} else {
				for (List<EnemyShip> column : this.enemyShips)
					for (int i = 0; i < column.size(); i++)
						if (column.get(i).equals(destroyedShip)) {
							column.get(i).destroy();
							this.logger.info("Destroyed ship in ("
									+ this.enemyShips.indexOf(column) + "," + i + ")");
						}
			}

		// Updates the list of ships that can shoot the player.
		if (this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = -1;

			for (List<EnemyShip> column : this.enemyShips)
				if (column.contains(destroyedShip)) {
					destroyedShipColumnIndex = this.enemyShips.indexOf(column);
					break;
				}

			EnemyShip nextShooter = getNextShooter(this.enemyShips
					.get(destroyedShipColumnIndex));

			if (nextShooter != null)
				this.shooters.set(destroyedShipIndex, nextShooter);
			else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to "
						+ this.shooters.size() + " members.");
			}
		}

		this.shipCount--;
	}
	/**
	 * Gets the ship on a given column that will be in charge of shooting.
	 * 
	 * @param column
	 *            Column to search.
	 * @return New shooter ship.
	 */
	public final EnemyShip getNextShooter(final List<EnemyShip> column) {
		Iterator<EnemyShip> iterator = column.iterator();
		EnemyShip nextShooter = null;
		while (iterator.hasNext()) {
			EnemyShip checkShip = iterator.next();
			if (checkShip != null && !checkShip.isDestroyed())
				nextShooter = checkShip;
		}

		return nextShooter;
	}

	/**
	 * Returns an iterator over the ships in the formation.
	 * 
	 * @return Iterator over the enemy ships.
	 */
	@Override
	public final Iterator<EnemyShip> iterator() {
		Set<EnemyShip> enemyShipsList = new HashSet<>();
		for (List<EnemyShip> column : this.enemyShips) {
			for (EnemyShip enemyShip : column) {
				enemyShipsList.add(enemyShip);
			}
		}
		return enemyShipsList.iterator();
	}

	/**
	 * Checks if there are any ships remaining.
	 * 
	 * @return True when all ships have been destroyed.
	 */
	public final boolean isEmpty() {
		return this.shipCount <= 0;
	}

	/**
	 * When EnemyShip is hit, its HP decrease by 1, and if the HP reaches 0, the ship is destroyed.
	 *
	 * @param bullet
	 *            Player's bullet
	 * @param destroyedShip
	 *            Ship to be hit
	 * @param isChainExploded
	 * 			  True if enemy ship is chain exploded
	 */
	public final int[] _destroy(final Bullet bullet, final EnemyShip destroyedShip, boolean isChainExploded) {
		int count = 0;
		int point = 0;

		if (shouldChainExplode(destroyedShip, isChainExploded)) {
			destroyedShip.chainExplode();
		}

		if (bullet.getSpriteType() == SpriteType.ItemBomb) {
			int[] score = isCircle
					? Bomb.destroyByBomb_isCircle(enemyShips, destroyedShip, this.itemManager, this.logger)
					: Bomb.destroyByBomb(enemyShips, destroyedShip, this.itemManager, this.logger);
			count = score[0];
			point = score[1];
		} else {
			int[] results = handleStandardDestruction(bullet, destroyedShip);
			count += results[0];
			point += results[1];
		}

		updateShooterList(bullet, destroyedShip);
		this.shipCount -= count;

		return new int[]{count, point};
	}

	private boolean shouldChainExplode(EnemyShip destroyedShip, boolean isChainExploded) {
		return isChainExploded &&
				!destroyedShip.spriteType.equals(SpriteType.ExplosiveEnemyShip1) &&
				!destroyedShip.spriteType.equals(SpriteType.ExplosiveEnemyShip2);
	}

	private int[] handleStandardDestruction(final Bullet bullet, final EnemyShip destroyedShip) {
		int count = 0;
		int point = 0;

		for (List<EnemyShip> column : this.enemyShips) {
			for (int i = 0; i < column.size(); i++) {
				if (column.get(i).equals(destroyedShip)) {
					int[] destructionResult = processShipDestruction(bullet, destroyedShip, column, i);
					count += destructionResult[0];
					point += destructionResult[1];
					dropItemIfNecessary(destroyedShip);
				}
			}
		}
		return new int[]{count, point};
	}

	private int[] processShipDestruction(final Bullet bullet, final EnemyShip destroyedShip, List<EnemyShip> column, int index) {
		int count = 0;
		int point = 0;

		switch (destroyedShip.spriteType) {
			case ExplosiveEnemyShip1:
			case ExplosiveEnemyShip2:
				point += handleExplosiveShipDestruction(destroyedShip, column, index);
				count++;
				break;
			default:
				point += handleNormalShipDestruction(destroyedShip, column, index);
				if (destroyedShip.getHp() <= 0) {
					count++;
				}
				break;
		}
		return new int[]{count, point};
	}

	private int handleExplosiveShipDestruction(EnemyShip destroyedShip, List<EnemyShip> column, int index) {
		HpEnemyShip.hit(destroyedShip);
		playExplosionSound(destroyedShip);

		int point = destroyedShip.getPointValue();
		int[] pointAndCount = explosive(destroyedShip.getX(), destroyedShip.getY(), this.enemyShips.indexOf(column), index, this.enemyShips);
		point += pointAndCount[0];

		this.logger.info("Destroyed ExplosiveEnemyship in (" + this.enemyShips.indexOf(column) + "," + index + ")");
		return point;
	}

	private int handleNormalShipDestruction(EnemyShip destroyedShip, List<EnemyShip> column, int index) {
		HpEnemyShip.hit(destroyedShip);

		if (destroyedShip.getHp() > 0) {
			this.logger.info("Enemy ship lost 1 HP in (" + this.enemyShips.indexOf(column) + "," + index + ")");
			return 0;
		} else {
			this.logger.info("Destroyed ship in (" + this.enemyShips.indexOf(column) + "," + index + ")");
			return column.get(index).getPointValue();
		}
	}

	private void playExplosionSound(EnemyShip destroyedShip) {
		if (destroyedShip.isDestroyed()) {
			SoundManager.getInstance().playES("enemy_explosion");
		}
	}

	private void dropItemIfNecessary(EnemyShip destroyedShip) {
		if (destroyedShip.getColor().equals(Color.MAGENTA)) {
			this.itemManager.dropItem(destroyedShip, 1, 1);
		}
	}

	private void updateShooterList(final Bullet bullet, final EnemyShip destroyedShip) {
		if (bullet.getSpriteType() == SpriteType.ItemBomb) {
			Bomb.nextShooterByBomb(enemyShips, shooters, this, logger);
		} else if (destroyedShip.isDestroyed() && this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = findDestroyedShipColumnIndex(destroyedShip);

			EnemyShip nextShooter = getNextShooter(this.enemyShips.get(destroyedShipColumnIndex));

			if (nextShooter != null) {
				this.shooters.set(destroyedShipIndex, nextShooter);
			} else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to " + this.shooters.size() + " members.");
			}
		}
	}

	private int findDestroyedShipColumnIndex(EnemyShip destroyedShip) {
		for (List<EnemyShip> column : this.enemyShips) {
			if (column.contains(destroyedShip)) {
				return this.enemyShips.indexOf(column);
			}
		}
		return -1;
	}


	/**
	 * A function that explosive up, down, left, and right when an explosive EnemyShip dies
	 *
	 * @param x
	 *            explosive EnemyShip's Initial x-coordinates
	 * @param y
	 *            explosive EnemyShip's Initial y-coordinates
	 * @param index_x
	 * 			  explosive EnemyShip's x-coordinates in EnemyShips
	 * @param index_y
	 * 			  explosive EnemyShip's y-coordinates in EnemyShips
	 * @param enemyShips
	 * 			  the current arrangement of the enemy
	 */
	public int[] explosive(final int x, final int y, final int index_x, final int index_y, List<List<EnemyShip>> enemyShips) {
		Queue<EnemyShip> targetShipQ = new LinkedList<>();
		Timer timer = new Timer(500, null);
		int range = 2;
		int point = 0;
		int mob = 0;

		Bullet bullet = new Bullet(0, 0, -1);

		findTargetsInRange(x, y, index_x, index_y, range, enemyShips, targetShipQ);

		ActionListener listener = createExplosionListener(targetShipQ, bullet);
		timer.addActionListener(listener);
		timer.start();

		return new int[]{point, mob};
	}

	private void findTargetsInRange(final int x, final int y, final int index_x, final int index_y, int range, List<List<EnemyShip>> enemyShips, Queue<EnemyShip> targetShipQ) {
		for (int i = 1; i <= range; i++) {
			checkAndAddTarget(enemyShips, targetShipQ, index_x + i, y, x + i, y); // right
			checkAndAddTarget(enemyShips, targetShipQ, index_x - i, y, x - i, y); // left
			checkAndAddTarget(enemyShips, targetShipQ, index_x, index_y - i, x, y - i); // up
			checkAndAddTarget(enemyShips, targetShipQ, index_x, index_y + i, x, y + i); // down
		}
		targetShipQ.add(new EnemyShip()); // Add a marker to signify the end of valid targets
	}

	private void checkAndAddTarget(List<List<EnemyShip>> enemyShips, Queue<EnemyShip> targetShipQ, int targetIndexX, int targetIndexY, int expectedX, int expectedY) {
		if (isValidIndex(targetIndexX, targetIndexY, enemyShips)) {
			EnemyShip targetShip = enemyShips.get(targetIndexX).get(targetIndexY);
			if (!targetShip.isDestroyed() && targetShip.getX() == expectedX && targetShip.getY() == expectedY) {
				targetShipQ.add(targetShip);
			}
		}
	}

	private boolean isValidIndex(int indexX, int indexY, List<List<EnemyShip>> enemyShips) {
		return indexX >= 0 && indexX < enemyShips.size() && indexY >= 0 && indexY < enemyShips.get(indexX).size();
	}

	private ActionListener createExplosionListener(Queue<EnemyShip> targetShipQ, Bullet bullet) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				while (!targetShipQ.isEmpty()) {
					EnemyShip targetShip = targetShipQ.poll();
					if (targetShip.getX() != -2) {
						_destroy(bullet, targetShip, true);
					} else {
						break;
					}
				}
				if (targetShipQ.isEmpty()) {
					((Timer) e.getSource()).stop();
				}
			}
		};
	}

	public final void BecomeCircle(boolean iscircle){
		this.isCircle=iscircle;
	}
}