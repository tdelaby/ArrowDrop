package com.RecursionLabs.ArrowDrop;

import java.util.ArrayList;
import java.util.Random;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class SceneManager {

	int CAMERA_WIDTH = 480;
	int CAMERA_HEIGHT = 800;

	private AllScenes currentScene;
	BaseGameActivity activity;
	Engine engine;
	Camera camera;
	Rectangle targetBar;
	final FixtureDef ARROW_FIX = PhysicsFactory.createFixtureDef(10.0f, 0.0f,
			0.0f);
	final FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(10.0f, 0.0f,
			0.0f);
	BitmapTextureAtlas splashTA, blueTexture, greenTexture, redTexture,
			yellowTexture, titleTA, gameOverTA, pauseTA, pausedTA, leaderBoardTA, medalsTA, playTA;
	ITextureRegion splashTR, blueTextureRegion, greenTextureRegion,
			redTextureRegion, yellowTextureRegion, titleTR, gameOverTR, pauseTR, pausedTR;
	Scene splashScene, gameScene, menuScene, gameOverScene;
	ArrayList<Body> arrowBodies = new ArrayList<Body>();
	ArrayList<Body> bodies = new ArrayList<Body>();
	ArrayList<Sprite> arrows = new ArrayList<Sprite>();
	PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0,
			SensorManager.GRAVITY_EARTH), false);
	Body body, targetBody;
	int score = 0, currentArrow = 0, arrowColor, randomX = 0, highScore, pause = 0,
			arrowColor2 = 10, timePassed = 0, firstContact = 0, startY = 60, adHeight = 75, finalScore = 0, pointsAdded = 10;
	boolean barBlue = true, barGreen = false, barRed = false, barYellow = false;
	HUD yourHud = new HUD();
	private Font font, finalFont;
	Text scoreText, highScoreText, finalScoreText, finalHighScoreText;
	TimerHandler spriteTimerHandler, timerHandler, timer, deleteTimer;
	float mEffectSpawnDelay = 2;
	Random rand = new Random();
	Sprite blueArrow, greenArrow, redArrow, yellowArrow, arrow, title, gameOver, pauseButton, paused, icon;
	protected SharedPreferences delegate;
	SharedPreferences.Editor editor;
	Rectangle blueButton, greenButton, redButton, yellowButton;
	IUpdateHandler mainUpdate;
	String get2500, get5000, get7500, get10000, get25000, get50000, get75000, get100000;
	ITiledTextureRegion playTR, leaderBoardTR, medalsTR;
	TiledSprite playButton, leaderBoard, medals;
	GameHelper helper;
	ArrowDrop gameServices;
	final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

	public enum AllScenes {
		SPLASH, MENU, GAME, OVER
	}

	public SceneManager(BaseGameActivity act, Engine eng, Camera cam, GameHelper help, ArrowDrop drop) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		this.helper = help;
		this.gameServices = drop;
		CAMERA_HEIGHT = (int) cam.getHeight();
		CAMERA_WIDTH = (int) cam.getWidth();
		
	}
	
	public void loadSplashResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTA = new BitmapTextureAtlas(this.activity.getTextureManager(),
				256, 256);
		splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				splashTA, this.activity, "RLSplash.png", 0, 0);
		splashTA.load();
	}

	public void loadGameResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		blueTexture = new BitmapTextureAtlas(this.activity.getTextureManager(),
				32, 32);
		blueTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(blueTexture, this.activity, "bluearrow.png",
						0, 0);
		blueTexture.load();

		greenTexture = new BitmapTextureAtlas(
				this.activity.getTextureManager(), 32, 32);
		greenTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(greenTexture, this.activity, "greenarrow.png",
						0, 0);
		greenTexture.load();

		redTexture = new BitmapTextureAtlas(this.activity.getTextureManager(),
				32, 32);
		redTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(redTexture, this.activity, "redarrow.png", 0,
						0);
		redTexture.load();

		yellowTexture = new BitmapTextureAtlas(
				this.activity.getTextureManager(), 32, 32);
		yellowTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(yellowTexture, this.activity,
						"yellowarrow.png", 0, 0);
		yellowTexture.load();
		
		pauseTA = new BitmapTextureAtlas(
				this.activity.getTextureManager(), 480, 600);
		pauseTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(pauseTA, this.activity,
						"pause.png", 0, 0);
		pauseTA.load();
		
		pausedTA = new BitmapTextureAtlas(
				this.activity.getTextureManager(), 400, 160);
		pausedTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(pausedTA, this.activity,
						"paused.png", 0, 0);
		pausedTA.load();

		font = FontFactory.create(this.activity.getFontManager(),
				this.activity.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC), 16f,
				Color.WHITE_ABGR_PACKED_INT);
		font.load();
		
		finalFont = FontFactory.create(this.activity.getFontManager(),
				this.activity.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 36f,
				Color.WHITE_ABGR_PACKED_INT);
		finalFont.load();
		
		get2500 = activity.getString(R.string.achievement_get2500);
		get5000 = activity.getString(R.string.achievement_get5000);
		get7500 = activity.getString(R.string.achievement_get7500);
		get10000 = activity.getString(R.string.achievement_get10000);
		get25000 = activity.getString(R.string.achievement_get25000);
		get50000 = activity.getString(R.string.achievement_get50000);
		get75000 = activity.getString(R.string.achievement_get75000);
		get100000 = activity.getString(R.string.achievement_get100000);

	}

	public void loadMenuResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		playTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 128, TextureOptions.NEAREST);
		playTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playTA, 
				this.activity, "playButtonTiled.png", 0, 0, 2, 1);
		playTA.load();
		
		leaderBoardTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 128, 64, TextureOptions.NEAREST);
		leaderBoardTR = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(leaderBoardTA, this.activity, "trophyButtonSmallTiled.png", 0, 0, 2, 1);
		leaderBoardTA.load();
		
		medalsTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 128, 64, TextureOptions.NEAREST);
		medalsTR = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(medalsTA, this.activity, "medalButtonSmallTiled.png", 0, 0, 2, 1);
		medalsTA.load();
		
		titleTA = new BitmapTextureAtlas(this.activity.getTextureManager(),
				400, 160);
		titleTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(titleTA, this.activity, "Title.png",
						0, 0);
		titleTA.load();
	}
	
	public void loadGameOverResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		gameOverTA = new BitmapTextureAtlas(this.activity.getTextureManager(),
				400, 160);
		gameOverTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(gameOverTA, this.activity, "GameOver.png",
						0, 0);
		gameOverTA.load();
	}

	public Scene createSplashScene() {
		splashScene = new Scene();
		splashScene.setBackground(new Background(0, 0, 0));

		icon = new Sprite(0, 0, splashTR,
				engine.getVertexBufferObjectManager());
		icon.setPosition((camera.getWidth() - icon.getWidth()) / 2,
				(camera.getHeight() - icon.getHeight()) / 2);
		splashScene.attachChild(icon);
		return splashScene;
	}

	public Scene createMenuScene() {
		menuScene = new Scene();
		menuScene.setBackground(new Background(0, 0, 0));
		
		title = new Sprite((camera.getWidth() - titleTR.getWidth()) / 2,
				(adHeight + 25), titleTR, engine.getVertexBufferObjectManager());
		menuScene.attachChild(title);
		
		createMenuAssets(menuScene);
		
		return menuScene;

	}

	public Scene createGameScene() {

		gameScene = new Scene();
		gameScene.setBackground(new Background(0, 0, 0));

		barBlue = true;
		barGreen = false;
		barRed = false;
		barYellow = false;
		deleteBodies();
		
		paused = new Sprite((camera.getWidth() - pausedTR.getWidth()) / 2,
				(camera.getHeight() - pausedTR.getHeight()) / 2, pausedTR,
				engine.getVertexBufferObjectManager());
		delegate = this.activity.getPreferences(Context.MODE_PRIVATE);
		editor = delegate.edit();
		highScore = delegate.getInt("HighScore", 0);
		physicsWorld.setContactListener(createContactListener());
		createHandlers();
		gameScene.registerUpdateHandler(physicsWorld);
		gameScene.registerUpdateHandler(mainUpdate);
		gameScene.registerUpdateHandler(timer);
		gameScene.registerUpdateHandler(deleteTimer);
		engine.registerUpdateHandler(spriteTimerHandler);
		createButtons();
		dropArrow();
		
		scoreText = new Text(10, adHeight + 36, font, "Score: 0123456789",
				this.activity.getVertexBufferObjectManager());
		highScoreText = new Text(10, adHeight + 10, font, "Score: 0123456789",
				this.activity.getVertexBufferObjectManager());
		gameScene.attachChild(scoreText);
		gameScene.attachChild(highScoreText);

		return gameScene;
	}

	public Scene createGameOverScene() {
		
		gameOverScene = new Scene();
		gameOverScene.setBackground(new Background(0, 0, 0));
		
		gameOver = new Sprite((camera.getWidth() - gameOverTR.getWidth()) / 2,
				(adHeight + 25), gameOverTR,
				engine.getVertexBufferObjectManager());
		gameOverScene.attachChild(gameOver);
		
		createMenuAssets(gameOverScene);

		scoreText = new Text((CAMERA_WIDTH /2) - 185, CAMERA_HEIGHT - 287, finalFont, "Final Score: " + finalScore,
				this.activity.getVertexBufferObjectManager());
		highScoreText = new Text((CAMERA_WIDTH /2) - 180, CAMERA_HEIGHT - 231, finalFont, "High Score: " + highScore,
				this.activity.getVertexBufferObjectManager());
		
		gameOverScene.attachChild(scoreText);
		gameOverScene.attachChild(highScoreText);
		
		return gameOverScene;
	}
	
	public void createMenuAssets(Scene scene) {
		
		playButton = new TiledSprite((camera.getWidth() - playTR.getWidth()) / 2, (camera.getHeight() - playTR.getHeight()) / 2, playTR,
				engine.getVertexBufferObjectManager()) {

			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					playButton.setCurrentTileIndex(1);
					
				}
				else if(touchEvent.isActionUp()) {
					playButton.setCurrentTileIndex(0);
					createGameScene();
					setCurrentScene(AllScenes.GAME);
				}
				return true;
			}
		};
        
		leaderBoard = new TiledSprite((camera.getWidth() / 2) + 114, (camera.getHeight() - leaderBoardTR.getHeight()) / 2, 
								leaderBoardTR, engine.getVertexBufferObjectManager())
		{
		        @Override
		        public boolean onAreaTouched(TouchEvent touchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (touchEvent.isActionDown()) {
					leaderBoard.setCurrentTileIndex(1);
				}
				else if(touchEvent.isActionUp()) {
					leaderBoard.setCurrentTileIndex(0);
					if(helper.isSignedIn()){
						gameServices.showLeaderboards();
					}
					else {
						gameServices.gameServicesSignIn();
					}
				}
		                             
		                return true;
		        }
		};
		
		medals = new TiledSprite((camera.getWidth() / 2) - 178, (camera.getHeight() - medalsTR.getHeight()) / 2,
				medalsTR, engine.getVertexBufferObjectManager()) 
		{
			@Override
			public boolean onAreaTouched(TouchEvent touchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (touchEvent.isActionDown()) {
					medals.setCurrentTileIndex(1);
				}
				else if(touchEvent.isActionUp()) {
					medals.setCurrentTileIndex(0);
					if(helper.isSignedIn()){
						gameServices.showAchievements();
					}
					else {
						gameServices.gameServicesSignIn();
					}
				}

				return true;
			}
		};
			
		targetBar = new Rectangle(0, CAMERA_HEIGHT - 145, CAMERA_WIDTH, 30, engine.getVertexBufferObjectManager());
		targetBar.setColor(new Color(0.03f, 0.0f, 0.56f));
		
		blueButton = new Rectangle(CAMERA_WIDTH / 2 - 180, CAMERA_HEIGHT - 95, 75,
				75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.03f, 0.0f, 0.56f));
				}
				return true;
			}
		};

		greenButton = new Rectangle(CAMERA_WIDTH / 2 - 85, CAMERA_HEIGHT - 95,
				75, 75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.0f, 0.75f, 0.0f));
				}
				return true;
			}
		};

		redButton = new Rectangle(CAMERA_WIDTH / 2 + 10, CAMERA_HEIGHT - 95, 75,
				75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.75f, 0.0f, 0.07f));
				}
				return true;
			}
		};

		yellowButton = new Rectangle(CAMERA_WIDTH / 2 + 105, CAMERA_HEIGHT - 95,
				75, 75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.88f, 0.85f, 0.01f));
				}
				return true;
			}
		};
	
		blueButton.setColor(new Color(0.03f, 0.0f, 0.56f));
		greenButton.setColor(new Color(0.0f, 0.75f, 0.0f));
		redButton.setColor(new Color(0.75f, 0.0f, 0.07f));
		yellowButton.setColor(new Color(0.88f, 0.85f, 0.01f));
		
		scene.registerTouchArea(playButton);
		scene.registerTouchArea(leaderBoard);
		scene.registerTouchArea(medals);
		scene.registerTouchArea(blueButton);
		scene.registerTouchArea(greenButton);
		scene.registerTouchArea(redButton);
		scene.registerTouchArea(yellowButton);
		scene.attachChild(playButton);
		scene.attachChild(leaderBoard);
		scene.attachChild(medals);
		scene.attachChild(targetBar);
		scene.attachChild(blueButton);
		scene.attachChild(greenButton);
		scene.attachChild(redButton);
		scene.attachChild(yellowButton);
	}
	
	private void createButtons() {

		targetBar = new Rectangle(0, CAMERA_HEIGHT - 145, CAMERA_WIDTH, 30,
				engine.getVertexBufferObjectManager());
		targetBar.setColor(new Color(0.03f, 0.0f, 0.56f));
		PhysicsFactory.createBoxBody(physicsWorld, targetBar,
				BodyType.StaticBody, WALL_FIX);
		targetBody = PhysicsFactory.createBoxBody(physicsWorld, targetBar,
				BodyType.StaticBody, WALL_FIX);
		targetBody.setUserData("TargetBar");
		targetBar.setZIndex(1);
		gameScene.attachChild(targetBar);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(targetBar,
				targetBody));

		blueButton = new Rectangle(CAMERA_WIDTH / 2 - 180, CAMERA_HEIGHT - 95, 75,
				75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.03f, 0.0f, 0.56f));
					barBlue = true;
					barGreen = false;
					barRed = false;
					barYellow = false;
				}
				return true;
			}
		};

		greenButton = new Rectangle(CAMERA_WIDTH / 2 - 85, CAMERA_HEIGHT - 95,
				75, 75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.0f, 0.75f, 0.0f));
					barBlue = false;
					barGreen = true;
					barRed = false;
					barYellow = false;
				}
				return true;
			}
		};

		redButton = new Rectangle(CAMERA_WIDTH / 2 + 10, CAMERA_HEIGHT - 95, 75,
				75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(0.75f, 0.0f, 0.07f));
					barBlue = false;
					barGreen = false;
					barRed = true;
					barYellow = false;
				}
				return true;
			}
		};

		yellowButton = new Rectangle(CAMERA_WIDTH / 2 + 105, CAMERA_HEIGHT - 95,
				75, 75, engine.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					targetBar.setColor(new Color(1.0f, 0.97f, 0.0f));
					barBlue = false;
					barGreen = false;
					barRed = false;
					barYellow = true;
				}
				return true;
			}
		};
		
		pauseButton = new Sprite((camera.getWidth() - pauseTR.getWidth()) /2, 55, pauseTR,
				engine.getVertexBufferObjectManager()) {

			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					pause++;
					if (pause % 2 == 1) {
						gameScene.unregisterUpdateHandler(physicsWorld);
						gameScene.unregisterUpdateHandler(timer);
						gameScene.unregisterUpdateHandler(mainUpdate);
						engine.unregisterUpdateHandler(spriteTimerHandler);
						gameScene.attachChild(paused);
						gameScene.unregisterTouchArea(blueButton);
						gameScene.unregisterTouchArea(greenButton);
						gameScene.unregisterTouchArea(redButton);
						gameScene.unregisterTouchArea(yellowButton);
					}
					else if(pause % 2 == 0) {
						gameScene.registerUpdateHandler(physicsWorld);
						gameScene.registerUpdateHandler(timer);
						gameScene.registerUpdateHandler(mainUpdate);
						engine.registerUpdateHandler(spriteTimerHandler);
						gameScene.detachChild(paused);
						gameScene.registerTouchArea(blueButton);
						gameScene.registerTouchArea(greenButton);
						gameScene.registerTouchArea(redButton);
						gameScene.registerTouchArea(yellowButton);
					}
				}
				return true;
			}
		};

		blueButton.setColor(new Color(0.03f, 0.0f, 0.56f));
		greenButton.setColor(new Color(0.0f, 0.75f, 0.0f));
		redButton.setColor(new Color(0.75f, 0.0f, 0.07f));
		yellowButton.setColor(new Color(1.0f, 0.97f, 0.0f));
		gameScene.registerTouchArea(pauseButton);
		gameScene.registerTouchArea(blueButton);
		gameScene.registerTouchArea(greenButton);
		gameScene.registerTouchArea(redButton);
		gameScene.registerTouchArea(yellowButton);
		gameScene.attachChild(pauseButton);
		gameScene.attachChild(blueButton);
		gameScene.attachChild(greenButton);
		gameScene.attachChild(redButton);
		gameScene.attachChild(yellowButton);
		//camera.setHUD(yourHud);
	}

	private void deleteBodies() {
		//if (!arrowBodies.isEmpty()) {
			for (int x = 0; x < bodies.size(); x++) {
				if(bodies.get(x).getUserData().equals("delete")){
				physicsWorld.destroyBody(bodies.get(x));
				arrows.get(x).detachSelf();
				bodies.remove(x);
				arrows.remove(x);
				System.gc();
				}
			}
		//}

	}
	
	public void createHandlers() {
		
		timer = new TimerHandler(1, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {

				if (timePassed == 14) {
					if(mEffectSpawnDelay > 0.75){
						mEffectSpawnDelay -= 0.25;
						spriteTimerHandler.setTimerSeconds(mEffectSpawnDelay);
					}
					pointsAdded += 10;
					timePassed = 0;
				}
				timePassed++;
			}

		});
		
		deleteTimer = new TimerHandler(0.001f, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {

				deleteBodies();
			}

		});
		
		mainUpdate = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				deleteBodies();
				scoreText.setText("Score: " + score);
				if (score > highScore) {
					highScore = score;
					editor.putInt("HighScore", highScore);
					editor.commit();
				}
				highScoreText.setText("High Score: " + highScore);
			}

			@Override
			public void reset() {
			}

		};
		
		spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
				new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						dropArrow();
					}
				});
	}
	
	private ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if (x1.getBody().getUserData() == null || x2.getBody().getUserData() == null) {
					
				} 
				else {
					
					if (x1.getBody().getUserData().equals("BlueArrow")
							|| x1.getBody().getUserData().equals("GreenArrow")
							|| x1.getBody().getUserData().equals("RedArrow")
							|| x1.getBody().getUserData().equals("YellowArrow")
							&& x2.getBody().getUserData().equals("TargetBar")) {
						
						if (barBlue && x1.getBody().getUserData().equals("BlueArrow"))
							score += pointsAdded;
						else if (barGreen && x1.getBody().getUserData().equals("GreenArrow"))
							score += pointsAdded;
						else if (barRed && x1.getBody().getUserData().equals("RedArrow"))
							score += pointsAdded;
						else if (barYellow && x1.getBody().getUserData().equals("YellowArrow"))
							score += pointsAdded;
						else {
							
								for(int x = 0; x < bodies.size(); x++){
									bodies.get(x).setUserData("delete");
								}
								gameScene.unregisterUpdateHandler(physicsWorld);
								gameScene.unregisterUpdateHandler(timer);
								engine.unregisterUpdateHandler(spriteTimerHandler);
								if(helper.isSignedIn()){
									gameServices.updateTopScoreLeaderboard(score);
									if(score >= 2500){
										gameServices.unlockAchievement(get2500);
									}
									if(score >= 5000){
										gameServices.unlockAchievement(get5000);
									}
									if(score >= 7500){
										gameServices.unlockAchievement(get7500);
									}
									if(score >= 10000){
										gameServices.unlockAchievement(get10000);
									}
									if(score >= 25000){
										gameServices.unlockAchievement(get25000);
									}
									if(score >= 50000){
										gameServices.unlockAchievement(get50000);
									}
									if(score >= 75000){
										gameServices.unlockAchievement(get75000);
									}
									if(score >= 100000){
										gameServices.unlockAchievement(get100000);
									}
								}
								finalScore = score;
								mEffectSpawnDelay = 2;
								timePassed = 0;
								score = 0;
								pointsAdded = 10;
								gameScene.unregisterUpdateHandler(mainUpdate);
								createGameOverScene();
								setCurrentScene(AllScenes.OVER);
							
						}
						bodies.get(0).setUserData("delete");
					} 
					else if (x2.getBody().getUserData().equals("BlueArrow")
							|| x2.getBody().getUserData().equals("GreenArrow")
							|| x2.getBody().getUserData().equals("RedArrow")
							|| x2.getBody().getUserData().equals("YellowArrow")
							&& x1.getBody().getUserData().equals("TargetBar")) {
						
						if (barBlue && x2.getBody().getUserData().equals("BlueArrow"))
							score += pointsAdded;
						else if (barGreen && x2.getBody().getUserData().equals("GreenArrow"))
							score += pointsAdded;
						else if (barRed && x2.getBody().getUserData().equals("RedArrow"))
							score += pointsAdded;
						else if (barYellow && x2.getBody().getUserData().equals("YellowArrow"))
							score += pointsAdded;
						else {
							
								for(int x = 0; x < bodies.size(); x++){
									bodies.get(x).setUserData("delete");
								}
								gameScene.unregisterUpdateHandler(physicsWorld);
								gameScene.unregisterUpdateHandler(timer);
								engine.unregisterUpdateHandler(spriteTimerHandler);
								if(helper.isSignedIn()){
									gameServices.updateTopScoreLeaderboard(score);
									if(score >= 2500){
										gameServices.unlockAchievement(get2500);
									}
									if(score >= 5000){
										gameServices.unlockAchievement(get5000);
									}
									if(score >= 7500){
										gameServices.unlockAchievement(get7500);
									}
									if(score >= 10000){
										gameServices.unlockAchievement(get10000);
									}
									if(score >= 25000){
										gameServices.unlockAchievement(get25000);
									}
									if(score >= 50000){
										gameServices.unlockAchievement(get50000);
									}
									if(score >= 75000){
										gameServices.unlockAchievement(get75000);
									}
									if(score >= 100000){
										gameServices.unlockAchievement(get100000);
									}
								}
								finalScore = score;
								mEffectSpawnDelay = 2;
								timePassed = 0;
								score = 0;
								pointsAdded = 10;
								gameScene.unregisterUpdateHandler(mainUpdate);
								createGameOverScene();
								setCurrentScene(AllScenes.OVER);
							
						}
						bodies.get(0).setUserData("delete");
					}
				}
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		};
		return contactListener;
	}

	private void dropArrow() {

		randomX = rand.nextInt((int) (camera.getWidth() - 32));
		

		if (arrowColor2 == 10) {
			arrowColor = rand.nextInt(4);
			if (arrowColor == 0) {
				arrow = new Sprite(randomX, adHeight + startY, blueTextureRegion,
						engine.getVertexBufferObjectManager());
				gameScene.attachChild(arrow);
			} else if (arrowColor == 1) {
				arrow = new Sprite(randomX, adHeight + startY, greenTextureRegion,
						engine.getVertexBufferObjectManager());
				gameScene.attachChild(arrow);
			} else if (arrowColor == 2) {
				arrow = new Sprite(randomX, adHeight + startY, redTextureRegion,
						engine.getVertexBufferObjectManager());
				gameScene.attachChild(arrow);
			} else if (arrowColor == 3) {
				arrow = new Sprite(randomX, adHeight + startY, yellowTextureRegion,
						engine.getVertexBufferObjectManager());
				gameScene.attachChild(arrow);
			}
			arrowColor2 = arrowColor;
		} 
		else {
			
			if(arrowColor2 == 0){
				arrowColor = rand.nextInt(3);
				 if (arrowColor == 0) {
					arrow = new Sprite(randomX, adHeight + startY, greenTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 1;
				} 
				 else if (arrowColor == 1) {
					arrow = new Sprite(randomX, adHeight + startY, redTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 2;
				} 
				 else if (arrowColor == 2) {
					arrow = new Sprite(randomX, adHeight + startY, yellowTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 3;
				}
			}
			else if(arrowColor2 == 1){
				arrowColor = rand.nextInt(3);
				if (arrowColor == 0) {
					arrow = new Sprite(randomX, adHeight + startY, blueTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 0;
				}
				else if (arrowColor == 1) {
					arrow = new Sprite(randomX, adHeight + startY, redTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 2;
				}
				else if (arrowColor == 2) {
					arrow = new Sprite(randomX, adHeight + startY, yellowTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 3;
				}
			}
			else if(arrowColor2 == 2){
				arrowColor = rand.nextInt(3);
				if (arrowColor == 0) {
					arrow = new Sprite(randomX, adHeight + startY, blueTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 0;
				} 
				else if (arrowColor == 1) {
					arrow = new Sprite(randomX, adHeight + startY, greenTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 1;
				}
				else if (arrowColor == 2) {
					arrow = new Sprite(randomX, adHeight + startY, yellowTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 3;
				}
			}
			else if(arrowColor2 == 3){
				arrowColor = rand.nextInt(3);
				if (arrowColor == 0) {
					arrow = new Sprite(randomX, adHeight + startY, blueTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 0;
				} 
				else if (arrowColor == 1) {
					arrow = new Sprite(randomX, adHeight + startY, greenTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 1;
				} 
				else if (arrowColor == 2) {
					arrow = new Sprite(randomX, adHeight + startY, redTextureRegion,
							engine.getVertexBufferObjectManager());
					gameScene.attachChild(arrow);
					arrowColor2 = 2;
				} 
				
			}
		}
		body = PhysicsFactory.createBoxBody(physicsWorld, arrow,
				BodyType.DynamicBody, ARROW_FIX);
		if (arrowColor2 == 0)
			body.setUserData("BlueArrow");
		else if (arrowColor2 == 1)
			body.setUserData("GreenArrow");
		else if (arrowColor2 == 2)
			body.setUserData("RedArrow");
		else if (arrowColor2 == 3)
			body.setUserData("YellowArrow");

		arrows.add(arrow);
		bodies.add(body);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(arrows.get(arrows.size() - 1), bodies.get(bodies.size() - 1),
				true, false));
		currentArrow++;

	}

	public AllScenes getCurrentScene() {
		return currentScene;
	}

	public void setCurrentScene(AllScenes currentScene) {
		this.currentScene = currentScene;
		switch (currentScene) {
		case SPLASH:
			break;
		case MENU:
			engine.setScene(menuScene);
			break;
		case GAME:
			engine.setScene(gameScene);
			break;
		case OVER:
			engine.setScene(gameOverScene);
			break;
		default:
			break;
		}
	}



}
