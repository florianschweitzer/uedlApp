package at.fhooe.it;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

/**
 * PreferenceActivity for the user to define specific settings (e.g. theme)
 * 
 * @author Florian Schweitzer
 * 
 */
public class UserSettingsActivity extends PreferenceActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		refreshTheme();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		sharedPrefs.registerOnSharedPreferenceChangeListener(spChanged);

		addPreferencesFromResource(R.xml.settings);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	/**
	 * Listener to get informed when a preference was changed
	 */
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			recreate();
		}
	};

	/**
	 * Refreshes the theme of the view according to the given preferences
	 */
	public void refreshTheme() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String chosenTheme = sharedPrefs.getString("prefTheme", "1");

		if (chosenTheme.equalsIgnoreCase("1")) {
			Log.i("it", "theme 1 was chosen");
			setTheme(R.style.Theme_Theme1);
			// finish();
		} else if (chosenTheme.equalsIgnoreCase("2")) {
			Log.i("it", "theme 2 was chosen");
			setTheme(R.style.Theme_Theme2);
			// finish();
		}
	}
}
