package at.fhooe.it;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Activity for the splash screen
 * 
 * @author Florian Schweitzer
 * 
 */
public class SplashScreen extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreen.this,
						UnityPlayerNativeActivity.class);
				startActivity(i);

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}