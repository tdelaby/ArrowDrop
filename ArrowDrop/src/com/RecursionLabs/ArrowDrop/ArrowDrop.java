package com.RecursionLabs.ArrowDrop;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.RecursionLabs.ArrowDrop.SceneManager.AllScenes;



public class ArrowDrop extends GBaseGameActivity {

	Scene scene;
	DisplayMetrics metrics = new DisplayMetrics();
	double width, height, ratio;
	protected int CAMERA_WIDTH = 480;
	protected final int CAMERA_HEIGHT = 800;
	SceneManager sceneManager;
	Camera mCamera;
	AdView adView;
	AdRequest adRequest;
	FrameLayout frameLayout;
	FrameLayout.LayoutParams frameLayoutLayoutParams, adViewLayoutParams;
	FrameLayout.LayoutParams surfaceViewLayoutParams;
	int adHeight;
	Context mContext;
	String leaderboard_Main;

	
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		ratio = height/width;
		CAMERA_WIDTH = (int) (800/ratio);
		if(CAMERA_WIDTH % 10 != 0)
			CAMERA_WIDTH += 10 - (CAMERA_WIDTH % 10);
		
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions options = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		return options;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {

		mContext = ArrowDrop.this;
		leaderboard_Main = getString(R.string.leaderboard_main);
		sceneManager = new SceneManager(this, mEngine, mCamera, ArrowDrop.this.mHelper, this);
		sceneManager.loadSplashResources();
		pOnCreateResourcesCallback.onCreateResourcesFinished();

	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {

		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());

	}

	@Override
    protected void onSetContentView() {

		frameLayout = new FrameLayout(this);
		frameLayoutLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);

		adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-9818181187193750/1314402024");
		adView.setAdSize(AdSize.SMART_BANNER);

		adView.refreshDrawableState();
		adView.setVisibility(AdView.VISIBLE);
		adViewLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		// getHeight returns 0
		// http://groups.google.com/group/admob-pu ... a874df3472
		adHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				50, getResources().getDisplayMetrics());
		// top of AD is at middle of the screen
		//adViewLayoutParams.topMargin = adHeight / 2;
		
		adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);
		
		this.mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(mEngine, this);

		surfaceViewLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);

	
		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
		frameLayout.addView(adView, adViewLayoutParams);

		this.setContentView(frameLayout, frameLayoutLayoutParams);
		
		
    }
	
	@Override
	  public void onPause() {
	    adView.pause();
	    super.onPause();
	  }

	  @Override
	  public void onResume() {
	    super.onResume();
	    adView.resume();
	  }

	  @Override
	  public void onDestroy() {
	    adView.destroy();
	    super.onDestroy();
	  }

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {

		
		 mEngine.registerUpdateHandler(new TimerHandler(3f, new
		 ITimerCallback() {
		
		 @Override
		 public void onTimePassed(TimerHandler pTimerHandler) {
		 // TODO Auto-generated method stub
		 mEngine.unregisterUpdateHandler(pTimerHandler);
		 sceneManager.loadGameResources();
		 sceneManager.loadMenuResources();
		 sceneManager.loadGameOverResources();
		 sceneManager.createMenuScene();
		 sceneManager.setCurrentScene(AllScenes.MENU);
		
		 }
		 }));

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}
	
	public void gameServicesSignIn() {
        runOnUiThread(new Runnable() {
            public void run() {
                beginUserInitiatedSignIn();
            }
        });

    }

    public void updateTopScoreLeaderboard(int score) {
        getGamesClient().submitScore(leaderboard_Main, score);
    }

    public void updateAchievement(String id, int percentage) {

        getGamesClient().incrementAchievement(id,percentage);
    }
    
    public void unlockAchievement(String id) {

        getGamesClient().unlockAchievement(id);
    }

    public void showLeaderboards() {
        runOnUiThread(new Runnable() {
            public void run() {
                startActivityForResult(getGamesClient().getLeaderboardIntent(leaderboard_Main), 5001);
            }
        });
    }

    public void showAchievements() {
        runOnUiThread(new Runnable() {
            public void run() {
                startActivityForResult(getGamesClient().getAchievementsIntent(), 5001);
            }
        });
    }

}

