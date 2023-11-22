package org.landroo.amoba;

/*
Amoba

This is a simple five in a row game.
The goal to put five X in a row.
- You can play with Droid AI or with your friends on the Internet.
- You can resize the cells and the size of the playground.
- You can scroll and zoom the playground by two fingers.
- You can undo the last step.
- You can set the color of playground.
- It's show the last step.
- Save the last play table.
- If you login in the GameServer app, you can play with your friends on the Internet.
- You can list the other players.
- You can invite your friend for a game.
- You can send a short message to your friends.

v 1.2.0
- Save the last play table.
- Internet multiplayer support add on.

v 1.2.1
- Bug fix in save last dimensions.
- Bug fix url encode.
- Add hungarian language.

v 1.3
- Some bug fix in.
- Bug fix url encode.
- Add direct connection between players.

v 1.3.1
- Some bug fix in.
- Some small modification.

v 1.3.2
- Some bug fix in.
- Some small modification.

v 1.3.3
- Some bug fix in.
- Add background graphics.

v 1.3.4
- Some bug fix in.


v 1.3.5
- New message

Amőba
Ez egy egyszerű amőba játék.
Célja egymásután tenni öt X-et egy sorban.
- Játszható a gép ellen vagy társak ellen az Interneten.
- A játékmező és a kocka mérete beállítható.
- A játéktér görgethető illetve átméretezhető a két ujjunkkal.
- Az utolsó lépés visszavonható.
- A játékmező színe beállítható.
- Megjelöli az utolsó lépést.
- Kilépéskor elmenti a játékállást.
- Ha belépünk a játék kiszolgálóra (GameServer) akkor játszhatunk a társunkkal az Interneten.
- Kilistázhatjuk a belépett játékosokat.
- Meghívhatjuk őket egy játékra.
- Küldhetünk üzenetet egy rövid a tárunknak.

v 1.2.0
- Kilépéskor elmenti a játékállást.
- Internetes többjátékos mód hozzáadása.

v 1.2.1
- Hibajavítás a mentett állás visszatöltésénél.
- Hibajavítás az URL kódolásnál.
- Magyar nyelv támogatás hozzáadása.

v 1.3
- Néhány hibajavítás.
- Hibajavítás az URL kódolásnál.
- Közvetlen kapcsolat hozzáadása a játékosok között.

v 1.3.1
- Néhány hibajavítás.
- Néhány apró módosítás.

v 1.3.2
- Néhány hibajavítás.
- Néhány apró módosítás.

v 1.3.3
- Néhány hibajavítás.
- Háttérkép hozzáadása.

v 1.3.4
- Néhány hibajavítás.

v 1.3.5
- Új üzenet.

 */
//TODO
// logout menu					ok
// scroll to table position		ok
// logout if no server			ok
// server error on xy server	ok
// undo error					??
// players buttons				??
// resume last game border		??
// save last step				ok
// polling interval				ok
// secoind net game error		ok
// setField(AmobaClass.java:117) ArrayIndexOutOfBoundsException: length=22; index=-7 	ok
// cannot set field in the last line													ok 
// if wifi enabled and name is set auto login											ok

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.landroo.http.HttpServer;
import org.landroo.http.WebClass;
import org.landroo.ui.UI;
import org.landroo.ui.UIInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class AmobaActivity extends Activity implements UIInterface
{

	private static final String TAG = "AmobaActivity";
	private static final int SWIPE_INTERVAL = 10;

	private static final int MAX_Parter = 4;
	private static final int buttonHeight = 60;
	private static final int httpport = 8484;

	private static Bitmap bitmap = null; // the paper

	private AmobaClass[] amobaList = new AmobaClass[MAX_Parter]; // the main game class list
	private int activePlayerNo = 0;

	private WebClass webClass = null;

	private UI ui = null;
	private AmobaView amobaview;

	private BitmapDrawable bitmapDrawable;
	private BitmapDrawable backDrawable;

	private int displayWidth = 0; // display width
	private int displayHeight = 0; // display height

	private float sX = 0;
	private float sY = 0;
	private float mX = 0;
	private float mY = 0;

	private float xPos = 0;
	private float yPos = 0;

	public float tableWidth;
	public float tableHeight;
	public float origWidth;
	public float origHeight;

	private Timer timer = null;
	private float swipeDistX = 0;
	private float swipeDistY = 0;
	private float swipeVelocity = 0;
	private float swipeSpeed = 0;
	private float backSpeedX = 0;
	private float backSpeedY = 0;
	private float offMarginX = 0;
	private float offMarginY = 0;

	private float zoomSize = 0;

	private Paint paint;

	private int cellSize = 40;
	private float tablesizeX = 1;
	private float tablesizeY = 1;
	private boolean showScore = true;
	private boolean zoomable = true;
	private int players = 2;
	private int colors = 1;
	private String playerName = "Player";
	private String address = "landroo.dyndns.org";

	private String[] score = new String[4];

	private SensorManager sensorManager;
	private Sensor sensor;
	private float currOri = 0f;
	private float lastOri = -1f;

	private boolean bFirst = true;
	private boolean bInit = true;
	private int errorCnt = 0;

	private String sNetError;// Error access server:
	private String sUserError;// Username alredy in use!
	private String sUserSuccess;// Login success!
	private String sNameError;//Please change your ninck name in settings!
	private String sAlredyError;
	private String sFirstError;
	private String sOfflineError;

	private HttpServer http;

	private int dialogMode = 0;

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			String ip = msg.getData().getString("ipaddress");
			if (ip != null && errorCnt == 0) Toast.makeText(AmobaActivity.this, sNetError + " " + ip, Toast.LENGTH_LONG).show();

			if (msg.what == 12) Toast.makeText(AmobaActivity.this, sUserError, Toast.LENGTH_LONG).show();
			if (msg.what == 13)
			{
				amobaview.invalidate();
				Toast.makeText(AmobaActivity.this, sUserSuccess, Toast.LENGTH_LONG).show();
			}
			if (msg.what == 14 && errorCnt++ > 4) logout(); // logout if 5 times network error

			String update = msg.getData().getString("update");
			if (update != null)
			{
				processUpdate(update);
				errorCnt = 0;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		ui = new UI(this);

		timer = new Timer();
		timer.scheduleAtFixedRate(new SwipeTask(), 0, SWIPE_INTERVAL);

		paint = new Paint();
		paint.setTextSize(24);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(3, 0, 0, Color.BLACK);

		for (int i = 0; i < 4; i++)
			score[i] = "";

		amobaview = new AmobaView(this);
		setContentView(amobaview);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(senzorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

		sNetError = getResources().getString(R.string.net_error);
		sUserError = getResources().getString(R.string.user_error);
		sUserSuccess = getResources().getString(R.string.net_success);
		sNameError = getResources().getString(R.string.name_error);
		sAlredyError = getResources().getString(R.string.alredy_error);
		sFirstError = getResources().getString(R.string.first_login);
		sOfflineError = getResources().getString(R.string.only_offline);
	}

	private void initApp(int size, float tableWidthMul, float tableHeightMul, String sFields)
	{
		try
		{
			if (amobaList[activePlayerNo] == null)
			{
				tableWidth = displayWidth * tableWidthMul;
				tableHeight = displayHeight * tableHeightMul;
				origWidth = tableWidth;
				origHeight = tableHeight;

				amobaList[activePlayerNo] = new AmobaClass(size);
				amobaList[activePlayerNo].setColors(colors);
				amobaList[activePlayerNo].setName("Droid");
				amobaList[activePlayerNo].newEvent = true;
				amobaList[activePlayerNo].initGame((int) tableWidth, (int) tableHeight);
				if (bitmap != null)
				{
					bitmap.recycle();
					bitmap = null;
					System.gc();
				}
				bitmap = amobaList[activePlayerNo].drawGame();
				if (bitmap == null) System.exit(1);
				if (!sFields.equals(""))
				{
					amobaList[activePlayerNo].setAllFields(sFields, true);
					amobaList[activePlayerNo].drawLast();

					scrollToPos(amobaList[activePlayerNo].miLast1[0], amobaList[activePlayerNo].miLast1[1]);
				}
				else scrollToPos(0, 0);

				if (bInit)
				{
					Bitmap back = amobaList[activePlayerNo].getBackGround(displayWidth, displayHeight, getResources());
					backDrawable = new BitmapDrawable(back);
					backDrawable.setBounds(0, 0, displayWidth, displayHeight);
					bInit = false;
				}

				// the margin is 30 percent
				offMarginX = displayWidth / 3;
				offMarginY = displayHeight / 3;

				bitmapDrawable = new BitmapDrawable(bitmap);
				bitmapDrawable.setBounds(0, 0, (int) tableWidth, (int) tableHeight);

				setScore();
			}
		}
		catch (OutOfMemoryError e)
		{
			Log.e(TAG, "Out of memory error in new page!");
			System.exit(1);
		}
		catch (Exception ex)
		{
			Log.e(TAG, "" + ex);
		}

		return;
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public synchronized void onResume()
	{
		boolean change = false;

		SharedPreferences settings = getSharedPreferences("org.landroo.amoba_preferences", MODE_PRIVATE);

		int i = settings.getInt("cellSize", 40);
		if (this.cellSize != i) change = true;
		this.cellSize = i;
		i = Integer.parseInt(settings.getString("colors", "1"));
		if (this.colors != i) change = true;
		this.colors = i;

		float f = settings.getInt("tableSizeX", 200);
		if (this.tablesizeX != f / 100) change = true;
		this.tablesizeX = f / 100;
		f = settings.getInt("tableSizeY", 200);
		if (this.tablesizeY != f / 100) change = true;
		this.tablesizeY = f / 100;

		boolean b = settings.getBoolean("showscore", true);
		if (this.showScore != b) change = true;
		this.showScore = b;
		b = settings.getBoolean("zoom", true);
		if (this.zoomable != b) change = true;
		this.zoomable = b;

		String s = "";
		if (bFirst) s = settings.getString("table", "");
		String table = s;
		bFirst = false;
		s = settings.getString("player", "Player");
		s = s.replace(" ", "_");
		s = s.replace("?", "_");
		s = s.replace("=", "_");
		s = s.replace("&", "_");
		s = s.replace(";", "_");
		s = s.replace(":", "_");
		s = s.replace("/", "_");
		if (!this.playerName.equals(s))
		{
			this.logout();
			change = true;
		}
		playerName = s;
		s = settings.getString("server", "landroo.dyndns.org");
		if (!this.address.equals(s)) change = true;
		address = s;

		if (change) amobaList[activePlayerNo] = null;
		initApp(cellSize, tablesizeX, tablesizeY, table);

		if (change)
		{
			webClass = new WebClass(address, "amoba", playerName, handler, httpport);
			webClass.size = cellSize;
			webClass.width = (int) tablesizeX * displayWidth;
			webClass.height = (int) tablesizeY * displayHeight;
		}

		if (bitmap == null) drawPlayer(activePlayerNo, 0);
		
		if(checkWifi() && !playerName.equals("Player")) webClass.login();

		super.onResume();
	}

	@Override
	public synchronized void onPause()
	{
		saveState();
		if (webClass.loggedIn) logout();

		if(bitmap != null)
		{
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}

		super.onPause();
	}

	@Override
	public void onStop()
	{
		if (webClass.loggedIn) webClass.logout();

		if (http != null) http.stop();

		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		if (webClass.loggedIn) webClass.logout();

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_amoba, menu);

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem login = menu.findItem(R.id.menu_login);
		MenuItem logout = menu.findItem(R.id.menu_logout);
		if (webClass != null && webClass.loggedIn)
		{
			login.setVisible(false);
			logout.setVisible(true);
		}
		else
		{
			login.setVisible(true);
			logout.setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		// settings
		case R.id.menu_settings:
			Intent SettingsIntent = new Intent(this, SettingsScreen.class);
			startActivity(SettingsIntent);
			return true;
			// exit
		case R.id.menu_exit:
			saveState();
			logout();
			this.finish();
			//System.runFinalizersOnExit(true);
			//this.setResult(1);
			//int pid = android.os.Process.myPid();
			//android.os.Process.killProcess(pid);
			//System.exit(0);
			return true;
			// new table
		case R.id.menu_new:
			if (activePlayerNo == 0)
			{
				amobaList[0] = null;
				initApp(cellSize, tablesizeX, tablesizeY, "");
				amobaview.postInvalidate();
			}
			else if (amobaList[activePlayerNo].myTurn == 1)
			{
				webClass.newtable(amobaList[activePlayerNo].playerName);
				amobaList[activePlayerNo].initGame(amobaList[activePlayerNo].miWidth, amobaList[activePlayerNo].miHeight);
				amobaList[activePlayerNo].saveGame = "";
				drawPlayer(activePlayerNo, 2);
			}
			return true;
			// undo
		case R.id.menu_undo:
			if (activePlayerNo == 0)
			{
				amobaList[0].undo();
				amobaview.postInvalidate();
			}
			else
			{
				// TODO undo in online?
				Toast.makeText(AmobaActivity.this, sOfflineError, Toast.LENGTH_LONG).show();
			}
			return true;
			// login
		case R.id.menu_login:
			login();
			return true;

			// logout
		case R.id.menu_logout:
			logout();
			return true;

		case R.id.menu_list:
			userlist();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Touch event
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return ui.tapEvent(event);
	}

	// amoba view
	private class AmobaView extends View
	{
		private int row;

		public AmobaView(Context context)
		{
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			// draw play table
			try
			{
				if (backDrawable != null)
				{
					// canvas.save();
					// canvas.translate(xPos, yPos);
					backDrawable.draw(canvas);
					// canvas.translate(-xPos, -yPos);
					// canvas.restore();
				}

				if (bitmapDrawable != null)
				{
					canvas.translate(xPos, yPos);
					bitmapDrawable.draw(canvas);
					canvas.restore();
				}

				row = 0;
				if (showScore) for (row = 0; row < players; row++)
					canvas.drawText(score[row], 0, row * 20 + 20, paint);

				if (webClass.loggedIn)
				{
					row = 0;
					for (int j = 0; j < AmobaActivity.MAX_Parter; j++)
					{
						if (amobaList[j] != null)
						{
							if (amobaList[j].newEvent) canvas.drawBitmap(amobaList[j].button1, displayWidth - 96, row * buttonHeight + 10, paint);
							else canvas.drawBitmap(amobaList[j].button3, displayWidth - 96, row * buttonHeight + 10, paint);
							row++;
						}
					}
				}
			}
			catch (Exception ex)
			{
				Log.i(TAG, ex.getMessage());
			}
		}
	}

	// process amoba
	private void processAmoba(float localX, float localY)
	{
		int x = (int) localX;
		int y = (int) localY;
		int rx = x - amobaList[activePlayerNo].xOff;
		int ry = y - amobaList[activePlayerNo].yOff;

		rx = (rx - (rx % cellSize)) / cellSize;
		ry = (ry - (ry % cellSize)) / cellSize;

		// Log.i(TAG, "rx: " + rx + " ry: " + ry);

		boolean bOK = false;
		if (amobaList[activePlayerNo].myTurn == 1)
		{
			bOK = amobaList[activePlayerNo].setField(rx, ry, 1);
			if (bOK)
			{
				int iRes = amobaList[activePlayerNo].endGame();
				// player win
				if (iRes > 0)
				{
					String sMessage = iRes == 1 ? getResources().getString(R.string.you_win) : getResources().getString(
							R.string.you_lose);
					showAlert(sMessage, false);
					amobaList[activePlayerNo].iWin[0]++;

					// if play with droid strat a new and scroll to first step
					if (activePlayerNo == 0 && activePlayerNo == 0)
					{
						dialogMode = 1;
						String sNew = getResources().getString(R.string.new_game);
						showDlg(sNew);
					}
				}
				else
				{
					if (activePlayerNo == 0)
					{
						// AI
						int[] iAI = amobaList[activePlayerNo].amobaAI(2);
						amobaList[activePlayerNo].setField(iAI[0], iAI[1], 2);

						iRes = amobaList[activePlayerNo].endGame();
						// AI wins
						if (iRes > 0)
						{
							String sMessage = iRes == 1 ? getResources().getString(R.string.you_win) : getResources()
									.getString(R.string.you_lose);
							showAlert(sMessage, false);
							amobaList[activePlayerNo].iWin[1]++;
						}
					}
				}

				// if play with partner send your step
				if (activePlayerNo > 0)
				{
					amobaList[activePlayerNo].newEvent = false;
					String ip = "";
					for (WebClass.Partner partner : webClass.userList)
					{
						if (partner.name.equals(amobaList[activePlayerNo].playerName))
						{
							ip = partner.ip;
							break;
						}
					}
					webClass.step(amobaList[activePlayerNo].playerName, ip, rx, ry);
					amobaList[activePlayerNo].myTurn = 2;
				}

				setScore();
			}
		}

		return;
	}

	private void newGame()
	{
		int[] iAI = amobaList[activePlayerNo].stPos(0, 0, amobaList[activePlayerNo].miRectMaxX,
				amobaList[activePlayerNo].miRectMaxY, 0);
		if (iAI != null)
		{
			boolean bOk = amobaList[activePlayerNo].setField(iAI[0], iAI[1], 2);
			if (bOk) scrollToPos(iAI[0], iAI[1]);
		}
		else Log.i(TAG, "New table?");
	}

	// show a message
	private void showAlert(String sAlert, boolean bAlert)
	{
		if (bAlert)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(sAlert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		else
		{
			Toast.makeText(this, sAlert, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDown(float x, float y)
	{
		sX = x;
		sY = y;

		swipeVelocity = 0;

		amobaview.postInvalidate();
	}

	@Override
	public void onUp(float x, float y)
	{
		checkOff();

		amobaview.postInvalidate();
	}

	@Override
	public void onTap(float x, float y)
	{
		float bx = (x - xPos) * (origWidth / tableWidth);
		float by = (y - yPos) * (origHeight / tableHeight);
		// Log.i(TAG, "" + bx + " " + by);

		boolean bButton = false;
		for (int i = 0; i < AmobaActivity.MAX_Parter; i++)
		{
			if (amobaList[i] != null && x > displayWidth - 90 && x < displayWidth 
			&& y > i * buttonHeight + 10 && y < i * buttonHeight + buttonHeight + 10)
			{
				drawPlayer(i, 0);
				bButton = true;

				break;
			}
		}

		if (bButton == false) processAmoba(bx, by);

		return;
	}

	@Override
	public void onHold(float x, float y)
	{
	}

	@Override
	public void onMove(float x, float y)
	{
		mX = x;
		mY = y;

		float dx = mX - sX;
		float dy = mY - sY;

		if ((tableWidth >= displayWidth) && (xPos + dx < displayWidth - (tableWidth + offMarginX) || xPos + dx > offMarginX))
			dx = 0;
		if ((tableHeight >= displayHeight)
				&& (yPos + dy < displayHeight - (tableHeight + offMarginY) || yPos + dy > offMarginY)) dy = 0;
		if ((tableWidth < displayWidth) && (xPos + dx > displayWidth - tableWidth || xPos + dx < 0)) dx = 0;
		if ((tableHeight < displayHeight) && (yPos + dy > displayHeight - tableHeight || yPos + dy < 0)) dy = 0;

		xPos += dx;
		yPos += dy;

		sX = mX;
		sY = mY;

		amobaview.postInvalidate();
	}

	@Override
	public void onSwipe(int direction, float velocity, float x1, float y1, float x2, float y2)
	{
		swipeDistX = x2 - x1;
		swipeDistY = y2 - y1;
		swipeSpeed = 1;
		swipeVelocity = velocity;

		amobaview.postInvalidate();
	}

	@Override
	public void onDoubleTap(float x, float y)
	{
		tableWidth = origWidth;
		tableHeight = origHeight;

		xPos = (displayWidth - tableWidth) / 2;
		yPos = (displayHeight - tableHeight) / 2;

		bitmapDrawable.setBounds(0, 0, (int) tableWidth, (int) tableHeight);

		scrollToPos(amobaList[activePlayerNo].miLast1[0], amobaList[activePlayerNo].miLast1[1]);

		amobaview.postInvalidate();
	}

	@Override
	public void onZoom(int mode, float x, float y, float distance, float xdiff, float ydiff)
	{
		if (!zoomable) return;

		int dist = (int) distance * 8;
		switch (mode)
		{
		case 1:
			zoomSize = dist;
			break;
		case 2:
			int diff = (int) (dist - zoomSize);
			double sizeOrig = Math.sqrt(tableWidth * tableWidth + tableHeight * tableHeight);
			double sizeDiff = 100 / (sizeOrig / (sizeOrig + diff));
			int newSizeX = (int) (tableWidth * (sizeDiff / 100));
			int newSizeY = (int) (tableHeight * (sizeDiff / 100));

			// zoom between min and max value
			if (newSizeX > origWidth / 4 && newSizeX < origWidth * 10)
			{
				bitmapDrawable.setBounds(0, 0, newSizeX, newSizeY);
				zoomSize = dist;

				float diffX = newSizeX - tableWidth;
				float diffY = newSizeY - tableHeight;
				float xPer = 100 / (tableWidth / (Math.abs(xPos) + mX)) / 100;
				float yPer = 100 / (tableHeight / (Math.abs(yPos) + mY)) / 100;

				xPos -= diffX * xPer;
				yPos -= diffY * yPer;

				tableWidth = newSizeX;
				tableHeight = newSizeY;

				if (tableWidth > displayWidth || tableHeight > displayHeight)
				{
					if (xPos > 0) xPos = 0;
					if (yPos > 0) yPos = 0;

					if (xPos + tableWidth < displayWidth) xPos = displayWidth - tableWidth;
					if (yPos + tableHeight < displayHeight) yPos = displayHeight - tableHeight;
				}
				else
				{
					if (xPos <= 0) xPos = 0;
					if (yPos <= 0) yPos = 0;

					if (xPos + tableWidth > displayWidth) xPos = displayWidth - tableWidth;
					if (yPos + tableHeight > displayHeight) yPos = displayHeight - tableHeight;
				}

				// Log.i(TAG, "" + xPos + " " + yPos);
			}
			break;
		case 3:
			zoomSize = 0;
			break;
		}

		amobaview.postInvalidate();
	}

	@Override
	public void onRotate(int mode, float x, float y, float angle)
	{
	}

	class SwipeTask extends TimerTask
	{
		public void run()
		{
			if (swipeVelocity > 0)
			{
				float dist = FloatMath.sqrt(swipeDistY * swipeDistY + swipeDistX * swipeDistX);
				float x = xPos - (float) ((swipeDistX / dist) * (swipeVelocity / 10));
				float y = yPos - (float) ((swipeDistY / dist) * (swipeVelocity / 10));

				if ((tableWidth >= displayWidth) && (x < displayWidth - (tableWidth + offMarginX) || x > offMarginX)
						|| ((tableWidth < displayWidth) && (x > displayWidth - tableWidth || x < 0)))
				{
					swipeDistX *= -1;
					swipeSpeed = swipeVelocity;
					// swipeSpeed += .5;
				}

				if ((tableHeight >= displayHeight) && (y < displayHeight - (tableHeight + offMarginY) || y > offMarginY)
						|| ((tableHeight < displayHeight) && (y > displayHeight - tableHeight || y < 0)))
				{
					swipeDistY *= -1;
					swipeSpeed = swipeVelocity;
					// swipeSpeed += .5;
				}

				xPos -= (float) ((swipeDistX / dist) * (swipeVelocity / 10));
				yPos -= (float) ((swipeDistY / dist) * (swipeVelocity / 10));

				swipeVelocity -= swipeSpeed;
				swipeSpeed += .0001;

				xPos -= (float) ((swipeDistX / dist) * (swipeVelocity / 10));
				yPos -= (float) ((swipeDistY / dist) * (swipeVelocity / 10));

				swipeVelocity -= swipeSpeed;
				swipeSpeed += .0001;

				amobaview.postInvalidate();

				if (swipeVelocity <= 0) checkOff();
			}

			if (backSpeedX != 0)
			{
				if ((backSpeedX < 0 && xPos <= 0.1f) || (backSpeedX > 0 && xPos + 0.1f >= displayWidth - tableWidth)) backSpeedX = 0;
				else if (backSpeedX < 0) xPos -= xPos / 20;
				else xPos += (displayWidth - (tableWidth + xPos)) / 20;

				amobaview.postInvalidate();
			}

			if (backSpeedY != 0)
			{
				if ((backSpeedY < 0 && yPos <= 0.1f) || (backSpeedY > 0 && yPos + 0.1f >= displayHeight - tableHeight)) backSpeedY = 0;
				else if (backSpeedY < 0) yPos -= yPos / 20;
				else yPos += (displayHeight - (tableHeight + yPos)) / 20;

				amobaview.postInvalidate();
			}
		}
	}

	private void checkOff()
	{
		if (tableWidth >= displayWidth)
		{
			if (xPos > 0 && xPos <= offMarginX) backSpeedX = -1;
			else if (xPos < tableWidth - offMarginX && xPos <= tableWidth) backSpeedX = 1;
		}
		if (tableHeight >= displayHeight)
		{
			if (yPos > 0 && yPos <= offMarginY) backSpeedY = -1;
			else if (yPos < tableHeight - offMarginY && yPos <= tableHeight) backSpeedY = 1;
		}
	}

	private SensorEventListener senzorListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			float[] values = event.values;
			// float x = values[0];
			float y = values[1];
			float z = values[2];

			// int angxy = Math.round(getViewAngDist(0, 0, x, y, true));
			// int angxz = Math.round(getViewAngDist(0, 0, x, z, true));
			int angyz = Math.round(getViewAngDist(0, 0, y, z, true));

			currOri = angDir(angyz);
			if (lastOri == -1) lastOri = currOri;
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1)
		{
		}
	};

	private float getViewAngDist(float x1, float y1, float x2, float y2, boolean bMode)
	{
		float nDelX = x2 - x1;
		float nDelY = y2 - y1;
		float nDe = 0;

		if (bMode)
		{
			if (nDelX != 0)
			{
				nDe = 2 * (float) Math.PI;
				nDe = nDe + (float) Math.atan(nDelY / nDelX);
				if (nDelX <= 0)
				{
					nDe = (float) Math.PI;
					nDe = nDe + (float) Math.atan(nDelY / nDelX);
				}
				else if (nDelY >= 0)
				{
					nDe = 0;
					nDe = nDe + (float) Math.atan(nDelY / nDelX);
				}
			}
			else
			{
				if (nDelY == 0) nDe = 0;
				else
				{
					if (nDelY < 0) nDe = (float) Math.PI;
					nDe = nDe + (float) Math.PI / 2;
				}
			}

			return nDe / (float) Math.PI * 180;
		}
		else return (float) Math.sqrt(nDelY * nDelY + nDelX * nDelX);
	}

	private int angDir(int ang)
	{
		int iRet = 0;
		if (ang > 0 && ang < 45) iRet = 0;
		else if (ang > 45 && ang < 135) iRet = 90;
		else if (ang > 135 && ang < 225) iRet = 180;
		else if (ang > 225 && ang < 305) iRet = 270;
		else iRet = 0;

		return iRet;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		Display display = getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();

		xPos = 0;
		yPos = 0;

		return;
	}

	/**
	 * draw score list
	 */
	private void setScore()
	{
		score[0] = playerName + ": " + amobaList[activePlayerNo].iWin[0];
		if (activePlayerNo == 0) for (int i = 2; i <= players; i++) score[i - 1] = "Droid: " + amobaList[activePlayerNo].iWin[i - 1];
		else score[1] = amobaList[activePlayerNo].playerName + ": " + amobaList[activePlayerNo].iWin[1];

		return;
	}

	/**
	 * save last Droid playtable
	 */
	private void saveState()
	{
		SharedPreferences settings = getSharedPreferences("org.landroo.amoba_preferences", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		String state = amobaList[0].getAllFields();
		editor.putString("table", state);
		editor.commit();

		return;
	}

	/**
	 * login to the game server
	 */
	private void login()
	{
		if (http == null)
		{
			try
			{
				http = new HttpServer(httpport, handler);
			}
			catch (Exception e)
			{
				Log.i(TAG, e.getMessage());
			}
		}

		if (playerName.equals("Player")) Toast.makeText(this, sNameError, Toast.LENGTH_LONG).show();
		else if (webClass.loggedIn) Toast.makeText(this, sAlredyError, Toast.LENGTH_LONG).show();
		else webClass.login();

		return;
	}

	/**
	 * get the player list
	 */
	private void userlist()
	{
		if (webClass.loggedIn)
		{
			ArrayList<String> activeUsers = new ArrayList<String>();
			for (int i = 1; i < this.amobaList.length; i++)
				if (this.amobaList[i] != null) activeUsers.add(this.amobaList[i].playerName);
			webClass.activeUsers = activeUsers;
			webClass.showUserListDialog(this);
		}
		else Toast.makeText(this, sFirstError, Toast.LENGTH_LONG).show();

		return;
	}

	/**
	 * process messages from the web
	 * 
	 * @param reply
	 */
	private void processUpdate(String reply)
	{
		String[] arr = reply.split(":", -1);

		try
		{
			synchronized (this)
			{
				// steps: roli;5;10;miki;18;33;
				if (!arr[0].equals("")) procStep(arr[0]);

				// messages: feri;Hali;laci;szia
				if (!arr[1].equals("")) procMessage(arr[1]);

				// invites: laci;1280;720;40;
				if (!arr[2].equals("")) procInvite(arr[2]);

				// added: miki;
				if (!arr[3].equals("")) procAddUser(arr[3]);

				// removed: tibi;
				if (!arr[4].equals("")) procRemoveUser(arr[4]);

				// newtable: tibi;
				if (!arr[5].equals("")) procNewTable(arr[5]);

				// newtable: tibi;
				if (!arr[6].equals("")) procUndo(arr[6]);
			}
		}
		catch (Exception ex)
		{
			Log.i(TAG, reply);
		}
		return;
	}

	/**
	 * proces the step of the partner
	 * 
	 * @param param
	 */
	private void procStep(String param)
	{
		String[] params = param.split(";");
		String name;
		int x;
		int y;
		boolean bNew;
		try
		{
			for (int i = 0; i < params.length; i += 3)
			{
				name = params[i];
				x = Integer.parseInt(params[i + 1]);
				y = Integer.parseInt(params[i + 2]);
				bNew = true;
				for (int j = 1; j < AmobaActivity.MAX_Parter; j++)
				{
					// if the name is ok then set the step
					if (this.amobaList[j] != null && this.amobaList[j].playerName.equals(name))
					{
						this.amobaList[j].setField(x, y, 2);
						this.amobaList[j].getAllFields();
						this.amobaList[j].newEvent = true;

						int iRes = amobaList[activePlayerNo].endGame();
						if (iRes > 0)
						{
							String sMessage = iRes == 1 ? getResources().getString(R.string.you_win) : getResources()
									.getString(R.string.you_lose);
							showAlert(sMessage, false);
							amobaList[activePlayerNo].iWin[1]++;
						}

						setScore();

						bNew = false;

						scrollToPos(amobaList[activePlayerNo].miLast1[0], amobaList[activePlayerNo].miLast1[1]);

						if (j == activePlayerNo) amobaList[activePlayerNo].myTurn = 1;
						amobaview.postInvalidate();
					}
				}
				// first answer
				if (bNew)
				{
					for (int j = 1; j < AmobaActivity.MAX_Parter; j++)
					{
						if (this.amobaList[j] == null)
						{
							amobaList[j] = new AmobaClass(cellSize);
							amobaList[j].setColors(colors);
							amobaList[j].setName(name);
							amobaList[j].initGame((int) tableWidth, (int) tableHeight);
							amobaList[j].newEvent = true;
							amobaList[j].setTableField(x, y, 2);
							amobaList[j].getAllFields();

							if (j == activePlayerNo) amobaList[j].myTurn = 1;
							amobaview.postInvalidate();

							break;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	/**
	 * 
	 * @param param
	 */
	private void procMessage(String param)
	{
		String[] params = param.split(";");
		String msg;
		try
		{
			for (int i = 0; i < params.length; i += 2)
			{
				if (!params[i].equals(""))
				{
					msg = params[i] + ": " + params[i + 1];
					Toast.makeText(AmobaActivity.this, msg, Toast.LENGTH_LONG).show();
				}
			}
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	/**
	 * 
	 * @param param
	 */
	private void procInvite(String param)
	{
		String[] params = param.split(";");
		String name;
		int width;
		int height;
		int size;
		boolean bOK;
		try
		{
			for (int i = 0; i < params.length; i += 4)
			{
				name = params[i];
				width = Integer.parseInt(params[i + 1]);
				height = Integer.parseInt(params[i + 2]);
				size = Integer.parseInt(params[i + 3]);
				bOK = true;
				for (int j = 0; j < AmobaActivity.MAX_Parter; j++)
				{
					if (this.amobaList[j] != null && this.amobaList[j].playerName.equals(name))
					{
						// alredy partner
						bOK = false;
						break;
					}
				}
				if (bOK)
				{
					for (int j = 0; j < AmobaActivity.MAX_Parter; j++)
					{
						if (this.amobaList[j] == null)
						{
							String msg = name + " " + getResources().getString(R.string.accepted);
							Toast.makeText(AmobaActivity.this, msg, Toast.LENGTH_LONG).show();
							
							amobaList[j] = new AmobaClass(size);
							amobaList[j].setColors(colors);
							amobaList[j].setName(name);
							amobaList[j].initGame(width, height);
							amobaList[j].newEvent = true;

							amobaview.postInvalidate();

							break;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	/**
	 * 
	 * @param param
	 */
	private void procAddUser(String param)
	{
		String[] params = param.split(";");
		String[] pairs;
		try
		{
			for (int i = 0; i < params.length; i++)
			{
				pairs = params[i].split("&", -1);
				WebClass.Partner newpartner = webClass.new Partner(pairs[0], pairs[1]);
				if (webClass.userList.size() == 0) webClass.userList.add(newpartner);
				else
				{
					boolean bIs = false;
					for (WebClass.Partner partner : webClass.userList)
					{
						if (!partner.name.equals(newpartner.name))
						{
							bIs = true;
							break;
						}
					}
					if (bIs == false) webClass.userList.add(newpartner);
				}
			}
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	/**
	 * 
	 * @param param
	 */
	private void procRemoveUser(String param)
	{
		String[] params = param.split(";");
		// remove from user list
		try
		{
			for (WebClass.Partner partner : webClass.userList)
				for (int i = 0; i < params.length; i++)
					if (partner.name.equals(params[i])) webClass.userList.remove(partner);

			// remove from players
			for (int j = 1; j < AmobaActivity.MAX_Parter; j++)
			{
				for (int i = 0; i < params.length; i++)
				{
					if (this.amobaList[j] != null && this.amobaList[j].playerName.equals(params[i]))
					{
						if (activePlayerNo == j) drawPlayer(0, 0);
						this.amobaList[j] = null;
					}
				}
			}

			amobaview.postInvalidate();
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	/**
	 * process new play table request
	 * 
	 * @param param
	 */
	private void procNewTable(String param)
	{
		String[] params = param.split(";");
		try
		{
			for (int j = 1; j < AmobaActivity.MAX_Parter; j++)
			{
				for (int i = 0; i < params.length; i++)
				{
					if (this.amobaList[j] != null && this.amobaList[j].playerName.equals(params[i]))
					{
						amobaList[j].initGame(amobaList[j].miWidth, amobaList[j].miHeight);
						amobaList[j].saveGame = "";
						amobaList[j].newEvent = true;

						if (activePlayerNo == j) drawPlayer(j, 1);
					}
				}
			}

			amobaview.postInvalidate();
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	private void procUndo(String param)
	{
		String[] params = param.split(";");
		try
		{
			for (int j = 1; j < AmobaActivity.MAX_Parter; j++)
			{
				for (int i = 0; i < params.length; i++)
				{
					if (this.amobaList[j] != null && this.amobaList[j].playerName.equals(params[i]))
					{
						// TODO undo in online
					}
				}
			}
		}
		catch (Exception ex)
		{
			Log.i(TAG, param);
		}
	}

	/**
	 * delete all player if I logged out
	 */
	private void logout()
	{
		if (http != null) http.stop();
		if (webClass != null && webClass.loggedIn)
		{
			drawPlayer(0, 0);

			webClass.logout();
			for (int j = 1; j < AmobaActivity.MAX_Parter; j++)
				this.amobaList[j] = null;
			activePlayerNo = 0;

			amobaview.postInvalidate();

			System.gc();
		}

		return;
	}

	/**
	 * draw the play table with used cells
	 * 
	 * @param iPly
	 */
	private void drawPlayer(int iPly, int turn)
	{
		if (amobaList[iPly] != null)
		{
			if (bitmap != null)
			{
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
			// save the last state
			amobaList[activePlayerNo].getAllFields();

			// create a new bitmap
			activePlayerNo = iPly;

			tableWidth = amobaList[activePlayerNo].miWidth;
			tableHeight = amobaList[activePlayerNo].miHeight;
			origWidth = tableWidth;
			origHeight = tableHeight;

			cellSize = amobaList[activePlayerNo].miRectSize;

			bitmap = amobaList[activePlayerNo].drawGame();
			if (!amobaList[activePlayerNo].saveGame.equals(""))
			{
				amobaList[activePlayerNo].setAllFields(amobaList[activePlayerNo].saveGame, true);
				amobaList[activePlayerNo].drawLast();
			}
			bitmapDrawable = new BitmapDrawable(bitmap);
			bitmapDrawable.setBounds(0, 0, (int) tableWidth, (int) tableHeight);

			setScore();

			xPos = (displayWidth - tableWidth) / 2;
			yPos = (displayHeight - tableHeight) / 2;

			if (turn != 0) amobaList[activePlayerNo].myTurn = turn;
			else if (activePlayerNo == 0) amobaList[activePlayerNo].myTurn = 1;
			else if (amobaList[activePlayerNo].newEvent) amobaList[activePlayerNo].myTurn = 1;
			else amobaList[activePlayerNo].myTurn = 2;

			scrollToPos(amobaList[activePlayerNo].miLast1[0], amobaList[activePlayerNo].miLast1[1]);

			amobaview.postInvalidate();
		}

		return;
	}

	@Override
	public void onFingerChange()
	{
	}

	private void scrollToPos(int tx, int ty)
	{
		if (tx == 0 && ty == 0)
		{
			// go to middle of table
			xPos = (displayWidth - tableWidth) / 2;
			yPos = (displayHeight - tableHeight) / 2;
		}
		else
		{
			float x = tx * cellSize;
			float y = ty * cellSize;

			if (x > tableWidth / 4 * 3) xPos = displayWidth - tableWidth;
			else if (x >= tableWidth / 4 && x <= tableWidth / 4 * 3) xPos = displayWidth - x - displayWidth / 2;
			else xPos = 0;

			if (y > tableHeight / 4 * 3) yPos = displayHeight - tableHeight;
			else if (y >= tableHeight / 4 && y <= tableHeight / 4 * 3) yPos = displayHeight - y - displayHeight / 2;
			else yPos = 0;

			// Log.i(TAG, "" + tx + " " + ty + " " + xPos + " " + yPos);
		}

		amobaview.postInvalidate();
	}

	private void showDlg(String sText)
	{
		String sYes = getResources().getString(R.string.yes);
		String sNo = getResources().getString(R.string.no);

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(sText);
		builder.setNegativeButton(sYes, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (dialogMode == 1) newGame();
				dialogMode = 0;
			}
		});
		builder.setPositiveButton(sNo, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialogMode = 0;
			}
		});
		builder.show();
	}

	private boolean checkWifi()
	{
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) return true;
		
		return false;
	}

}
