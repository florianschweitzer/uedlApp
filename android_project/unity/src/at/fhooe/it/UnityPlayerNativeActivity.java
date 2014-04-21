package at.fhooe.it;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;

import com.unity3d.player.UnityPlayer;

/**
 * Main activity which contains a drawer and a 3D player for navigation
 * 
 * @author Florian Schweitzer
 * 
 */
public class UnityPlayerNativeActivity extends Activity implements
		OnChildClickListener, OnGroupExpandListener, OnGroupCollapseListener {
	private static final int RESULT_SETTINGS = 1;

	protected UnityPlayer mUnityPlayer; // don't change the name of this
										// variable; referenced from native code

	// Drawer specific variables
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private DrawerAdapter mDrawerAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private HashMap<String, List<String>> m_drawerContent;
	private int m_currSelectedGroup = 0;
	private int m_currSelectedDetail = 0;

	// Textual information variables
	private CharSequence mTitle;

	private String[] mMainStepTitles;
	private String[] mAnalysisStepsTitles;
	private String[] mDesignStepsTitles;
	private String[] mImplementationStepsTitles;
	private String[] mDeploymentStepsTitles;

	private String[] mAnalysisDescr;
	private String[] mDesignDescr;
	private String[] mImplementationDescr;
	private String[] mDeploymentDescr;

	/**
	 * The main layout for the Unity-Player
	 */
	private FrameLayout m_frameLayout;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		refreshTheme();

		super.onCreate(savedInstanceState);

		setContentView(R.layout.player_main);

		// init Unity player
		m_frameLayout = (FrameLayout) findViewById(R.id.content_frame);
		getWindow().setFormat(PixelFormat.RGB_565);

		mUnityPlayer = new UnityPlayer(this);

		int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
		boolean trueColor8888 = false;
		mUnityPlayer.init(glesMode, trueColor8888);

		View playerView = mUnityPlayer.getView();

		m_frameLayout.addView(playerView);

		mTitle = getTitle();

		// init drawer layout and its content

		mMainStepTitles = getResources()
				.getStringArray(R.array.mainsteps_array);
		mAnalysisStepsTitles = getResources().getStringArray(
				R.array.analysis_steps_array);
		mDesignStepsTitles = getResources().getStringArray(
				R.array.design_steps_array);
		mImplementationStepsTitles = getResources().getStringArray(
				R.array.implementation_steps_array);
		mDeploymentStepsTitles = getResources().getStringArray(
				R.array.deployment_steps_array);

		mAnalysisDescr = getResources().getStringArray(
				R.array.analysis_descr_array);
		mDesignDescr = getResources()
				.getStringArray(R.array.design_descr_array);
		mImplementationDescr = getResources().getStringArray(
				R.array.implementation_descr_array);
		mDeploymentDescr = getResources().getStringArray(
				R.array.deployment_descr_array);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer_expLv);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		m_drawerContent = new LinkedHashMap<String, List<String>>();

		m_drawerContent.put(mMainStepTitles[0],
				Arrays.asList(mAnalysisStepsTitles));
		m_drawerContent.put(mMainStepTitles[1],
				Arrays.asList(mDesignStepsTitles));
		m_drawerContent.put(mMainStepTitles[2],
				Arrays.asList(mImplementationStepsTitles));
		m_drawerContent.put(mMainStepTitles[3],
				Arrays.asList(mDeploymentStepsTitles));

		mDrawerAdapter = new DrawerAdapter(this, m_drawerContent);
		refreshTheme();
		mDrawerList.setAdapter(mDrawerAdapter);

		mDrawerList.setOnChildClickListener(this);
		mDrawerList.setOnGroupExpandListener(this);
		mDrawerList.setOnGroupCollapseListener(this);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				mDrawerAdapter.selectItem(m_currSelectedGroup,
						m_currSelectedDetail);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0, 0);
		}

		sendStringsToUnity(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.player_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.item_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.item_settings:
			Intent i = new Intent(this, UserSettingsActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			return true;
		case R.id.item_help:
			i = new Intent(this, HelpActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			applyUserSettings();
			break;

		}

	}

	/**
	 * Helper method to apply the user settings
	 */
	private void applyUserSettings() {
		refreshTheme();
	}

	/**
	 * Refreshes the theme
	 */
	private void refreshTheme() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String chosenTheme = sharedPrefs.getString("prefTheme", "1");

		if (chosenTheme.equalsIgnoreCase("1")) {
			Log.i("it", "theme 1 was chosen");
			getApplication().setTheme(R.style.Theme_Theme1);

			if (mDrawerAdapter != null) {
				mDrawerAdapter.setColors(Color.MAGENTA, Color.GREEN);
			}

		} else if (chosenTheme.equalsIgnoreCase("2")) {
			Log.i("it", "theme 2 was chosen");
			getApplication().setTheme(R.style.Theme_Theme2);

			if (mDrawerAdapter != null) {
				mDrawerAdapter.setColors(Color.BLUE, Color.RED);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setTitle(java.lang.CharSequence)
	 */
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onDestroy() {
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// onPause()/onResume() must be sent to UnityPlayer to enable pause and
	// resource recreation on resume.
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	protected void onPause() {
		super.onPause();
		mUnityPlayer.pause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		mUnityPlayer.resume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.Activity#onConfigurationChanged(android.content.res.Configuration
	 * )
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);

		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.onKeyMultiple(event.getKeyCode(),
					event.getRepeatCount(), event);
		return super.dispatchKeyEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ExpandableListView.OnChildClickListener#onChildClick(android
	 * .widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		selectItem(groupPosition, childPosition);

		mDrawerLayout.closeDrawer(mDrawerList);

		handlePhaseChange(groupPosition, childPosition);

		return true;
	}

	/**
	 * Helper method to communicate changes in the phases to Unity
	 * 
	 * @param _phase
	 *            the current selected phase index
	 * @param _step
	 *            the current selected detail index
	 */
	private void handlePhaseChange(int _phase, int _step) {
		switch (_phase) {
		case 0:
			UnityPlayer.UnitySendMessage("SceneController", "rotateToAnalysis",
					"");
			sendStringsToUnity(0);
			break;
		case 1:
			UnityPlayer.UnitySendMessage("SceneController", "rotateToDesign",
					"");
			sendStringsToUnity(1);

			break;
		case 2:
			UnityPlayer.UnitySendMessage("SceneController",
					"rotateToImplementation", "");
			sendStringsToUnity(2);
			break;
		case 3:
			UnityPlayer.UnitySendMessage("SceneController",
					"rotateToDeployment", "");
			sendStringsToUnity(3);
		}

		UnityPlayer.UnitySendMessage("SceneController", "rotateToDetail",
				String.valueOf(_step));
	}

	/**
	 * Sends the textual information to unity
	 * 
	 * @param _phase
	 *            the index of the current selected phase
	 */
	private void sendStringsToUnity(int _phase) {
		String serialTitles = "";
		String serialDescr = "";

		switch (_phase) {
		case 0:
			for (int i = 0; i < mAnalysisStepsTitles.length; i++) {
				serialTitles += mAnalysisStepsTitles[i] + "$";
			}

			for (int i = 0; i < mAnalysisDescr.length; i++) {
				serialDescr += mAnalysisDescr[i] + "$";
			}
			break;
		case 1:
			for (int i = 0; i < mDesignStepsTitles.length; i++) {
				serialTitles += mDesignStepsTitles[i] + "$";
			}

			for (int i = 0; i < mDesignDescr.length; i++) {
				serialDescr += mDesignDescr[i] + "$";
			}
			break;
		case 2:
			for (int i = 0; i < mImplementationStepsTitles.length; i++) {
				serialTitles += mImplementationStepsTitles[i] + "$";
			}

			for (int i = 0; i < mImplementationDescr.length; i++) {
				serialDescr += mImplementationDescr[i] + "$";
			}
			break;
		case 3:
			for (int i = 0; i < mDeploymentStepsTitles.length; i++) {
				serialTitles += mDeploymentStepsTitles[i] + "$";
			}

			for (int i = 0; i < mDeploymentDescr.length; i++) {
				serialDescr += mDeploymentDescr[i] + "$";
			}
			break;
		}

		String serialPhases = "";

		for (int i = 0; i < mMainStepTitles.length; i++) {
			serialPhases += mMainStepTitles[i] + "$";
		}
		UnityPlayer.UnitySendMessage("SceneController", "setPhasesString",
				serialPhases);

		UnityPlayer.UnitySendMessage("SceneController", "setDetailStrings",
				serialTitles);
		UnityPlayer.UnitySendMessage("SceneController", "setDescrStrings",
				serialDescr);
	}

	/**
	 * Select a specific detail or phase and refresh view
	 * 
	 * @param _groupPos
	 *            the index of the current selected phase
	 * @param _childPos
	 *            the idnex of the current selected detail
	 */
	private void selectItem(int _groupPos, int _childPos) {
		// update selected item and title, then close the drawer
		// mDrawerList.setItemChecked(_groupPos + _childPos, true);
		mDrawerAdapter.selectItem(_groupPos, _childPos);
		m_currSelectedDetail = _childPos;
		m_currSelectedGroup = _groupPos;

		handlePhaseChange(_groupPos, _childPos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ExpandableListView.OnGroupExpandListener#onGroupExpand
	 * (int)
	 */
	@Override
	public void onGroupExpand(int groupPosition) {
		// selectItem(groupPosition, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ExpandableListView.OnGroupCollapseListener#onGroupCollapse
	 * (int)
	 */
	@Override
	public void onGroupCollapse(int groupPosition) {
		// selectItem(groupPosition, 0);
	}

	/**
	 * Helper method to get informed when the user selects a new phase
	 * 
	 * @param _groupIndex
	 *            the current selected phase index
	 */
	public void phaseChanged(int _groupIndex) {
		Log.i("it", "phaseChanged method called from unity with parameter: "
				+ _groupIndex);

		m_currSelectedGroup = _groupIndex;

		sendStringsToUnity(_groupIndex);
		UnityPlayer.UnitySendMessage("SceneController", "rotateToDetail", "0");
	}

	public void detailChanged(int _detailIndex) {
		Log.i("it", "detailChanged method called from unity with parameter: "
				+ _detailIndex);

		m_currSelectedDetail = _detailIndex;
	}
}
