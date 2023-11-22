// Amoba package
package org.landroo.amoba;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.Log;

public class AmobaClass
{
	private static final String TAG = "AmobaClass";

	private boolean debug = false; // debug

	private Bitmap bitmap;
	private Canvas canvas = null; // the canvas

	public int miRectSize = 0; // square size
	public int miRectMaxX = 0; // paper width
	public int miRectMaxY = 0; // paper height

	private int[][] maTable = null; // playground fields
	private int[][] maValue = null; // fields values

	private int[][] maPattern = null; // pattern lines
	private int[] maPatternVal = null; // next values

	public int xOff = 0; // offset x
	public int yOff = 0; // offset y
	private int miCanvasW = 0; // canvas width
	private int miCanvasH = 0; // canvas height
	public int miWidth = 0; // width
	public int miHeight = 0; // height

	private int[] miEndLine = null; // draw line
	public int[] miLast1 = null; // last step
	public int[] miLast2 = null;

	private Paint paint;

	public int miBackColor = Color.TRANSPARENT;
	public int miLastColor = 0xFFFFFF00;
	public int miBorderColor = 0xFFFFFF;
	public int miGridColor = 0xFFFFFFFF;

	public int miXColor = 0xFF00FF00;
	public int miOColor = 0xFFFF0000;
	public int miRColor = 0xFF0000FF;
	public int miTColor = 0xFF00FFFF;

	public int miLieWidth = 6;

	public String playerName;
	public Bitmap button1;
	public Bitmap button2;
	public Bitmap button3;
	public boolean newEvent;
	public String saveGame = "";
	public int[] iWin = new int[4];
	public int myTurn = 1;

	private BorderLine border;

	// constructor
	public AmobaClass(int iRectSize)
	{
		miRectSize = iRectSize;
		maPattern = new int[12][9];
		maPatternVal = new int[12];
		for (int i = 0; i < 12; i++) maPatternVal[i] = 0;
		miEndLine = new int[4];
		miLast1 = new int[3];
		miLast2 = new int[3];

		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(miLieWidth);

		border = new BorderLine(miRectSize, miLieWidth, miBorderColor);

		for (int i = 0; i < 4; i++) iWin[i] = 0;
	}

	// set field
	public void setTableField(int i, int j, int iPlayer)
	{
		maTable[i][j] = iPlayer;

		miLast2[0] = miLast1[0];
		miLast2[1] = miLast1[1];
		miLast2[2] = miLast1[2];

		miLast1[0] = i;
		miLast1[1] = j;
		miLast1[2] = iPlayer;

		return;
	}

	public boolean setField(int i, int j, int iPlayer)
	{
		if (i < 0 || i >= miRectMaxX ||j < 0 || j >= miRectMaxY) return false;

		if (debug) this.drawDebug();

		boolean bOK = false;
		if (maTable[i][j] == 0)
		{
			if (miLast1[2] != 0)
			{
				clearBack(miLast1[0] * miRectSize + xOff, miLast1[1] * miRectSize + yOff, false, this.miBackColor, false);

				switch (miLast1[2])
				{
				case 1:
					drawX(miLast1[0] * miRectSize + xOff, miLast1[1] * miRectSize + yOff);
					break;
				case 2:
					drawO(miLast1[0] * miRectSize + xOff, miLast1[1] * miRectSize + yOff);
					break;
				case 3:
					drawR(miLast1[0] * miRectSize + xOff, miLast1[1] * miRectSize + yOff);
					break;
				case 4:
					drawT(miLast1[0] * miRectSize + xOff, miLast1[1] * miRectSize + yOff);
					break;
				}
			}

			clearBack(i * miRectSize + xOff, j * miRectSize + yOff, false, this.miLastColor, true);
			maTable[i][j] = iPlayer;
			switch (iPlayer)
			{
			case 1:
				drawX(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			case 2:
				drawO(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			case 3:
				drawR(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			case 4:
				drawT(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			}
			bOK = true;

			miLast2[0] = miLast1[0];
			miLast2[1] = miLast1[1];
			miLast2[2] = miLast1[2];

			miLast1[0] = i;
			miLast1[1] = j;
			miLast1[2] = iPlayer;
		}

		return bOK;
	}

	public void drawLast()
	{
		int i = miLast1[0];
		int j = miLast1[1];
		int iPlayer = miLast1[2];

		if (iPlayer != 0)
		{
			clearBack(i * miRectSize + xOff, j * miRectSize + yOff, false, this.miLastColor, true);

			switch (iPlayer)
			{
			case 1:
				drawX(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			case 2:
				drawO(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			case 3:
				drawR(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			case 4:
				drawT(i * miRectSize + xOff, j * miRectSize + yOff);
				break;
			}
		}
	}

	// draw X
	private void drawX(int x, int y)
	{
		int rx = 0;
		int ry = 0;

		if ((x + miRectSize - xOff) <= miCanvasW && (y + miRectSize - yOff) <= miCanvasH)
		{
			rx = x;
			ry = y;

			paint.setStrokeWidth(miLieWidth);
			paint.setColor(this.miXColor);
			canvas.drawLine(rx + 2, ry + 2, rx + miRectSize - 1, ry + miRectSize - 1, paint);
			canvas.drawLine(rx + 2, ry + miRectSize - 1, rx + miRectSize - 1, ry + 2, paint);
		}

		return;
	}

	// draw O
	private void drawO(int x, int y)
	{
		int rx = 0;
		int ry = 0;

		if ((x + miRectSize - xOff) <= miCanvasW && (y + miRectSize - yOff) <= miCanvasH)
		{
			rx = x + (miRectSize / 2);
			ry = y + (miRectSize / 2);

			paint.setStrokeWidth(miLieWidth);
			paint.setColor(this.miOColor);
			canvas.drawCircle(rx, ry, miRectSize / 2 - 4, paint);
		}

		return;
	}

	// draw rect
	private void drawR(int x, int y)
	{
		if ((x + miRectSize - xOff) <= miCanvasW && (y + miRectSize - yOff) <= miCanvasH)
		{
			Path path = new Path();
			path.moveTo(x + miRectSize / 2, y);
			path.lineTo(x + miRectSize, y + miRectSize / 2);
			path.lineTo(x + miRectSize / 2, y + miRectSize);
			path.lineTo(x, y + miRectSize / 2);
			path.lineTo(x + miRectSize / 2, y);

			paint.setStrokeWidth(miLieWidth);
			paint.setColor(this.miRColor);
			canvas.drawPath(path, paint);
		}

		return;
	}

	// draw +
	private void drawT(int x, int y)
	{
		if ((x + miRectSize - xOff) <= miCanvasW && (y + miRectSize - yOff) <= miCanvasH)
		{
			paint.setStrokeWidth(miLieWidth);
			paint.setColor(this.miTColor);

			int rx = x * miRectSize + xOff;
			int ry = y * miRectSize + yOff;

			canvas.drawLine(rx + (miRectSize / 2) - 1, ry + 1, rx + (miRectSize / 2) - 1, ry + miRectSize - 1, paint);
			canvas.drawLine(rx + 1, ry + (miRectSize / 2) - 1, rx + miRectSize - 1, ry + (miRectSize / 2) - 1, paint);
		}

		return;
	}

	// show end line
	private boolean drawLine(int x, int y, int i, int c)
	{
		int rx = x * miRectSize + (miRectSize / 2);
		int ry = y * miRectSize + (miRectSize / 2);

		paint.setStrokeWidth(miLieWidth * 2);
		if (c == 1) paint.setColor(0xFF00FF00);
		else paint.setColor(0xFFFF0000);

		switch (i)
		{
		case 0: // right
			canvas.drawLine(rx + xOff, ry + yOff, rx + miRectSize * 4 + xOff, ry + yOff, paint);
			break;
		case 1: // left
			canvas.drawLine(rx + xOff, ry + yOff, rx - miRectSize * 4 + xOff, ry + yOff, paint);
			break;
		case 2: // up
			canvas.drawLine(rx + xOff, ry + yOff, rx + xOff, ry - miRectSize * 4 + yOff, paint);
			break;
		case 3: // down
			canvas.drawLine(rx + xOff, ry + yOff, rx + xOff, ry + miRectSize * 4 + yOff, paint);
			break;
		case 4: // right up
			canvas.drawLine(rx + xOff, ry + yOff, rx + miRectSize * 4 + xOff, ry - miRectSize * 4 + yOff, paint);
			break;
		case 5: // right down
			canvas.drawLine(rx + xOff, ry + yOff, rx + miRectSize * 4 + xOff, ry + miRectSize * 4 + yOff, paint);
			break;
		case 6: // left up
			canvas.drawLine(rx + xOff, ry + yOff, rx - miRectSize * 4 + xOff, ry + miRectSize * 4 + yOff, paint);
			break;
		case 7: // left down
			canvas.drawLine(rx + xOff, ry + yOff, rx - miRectSize * 4 + xOff, ry - miRectSize * 4 + yOff, paint);
			break;
		}

		miLast1[2] = 0;
		miLast2[2] = 0;

		return true;
	}

	// draw grid
	public void initGame(int width, int height)
	{
		miWidth = width;
		miHeight = height;

		int w = miWidth;
		int h = miHeight;

		w = w - (w % miRectSize);
		h = h - (h % miRectSize);

		miCanvasW = w;
		miCanvasH = h;

		xOff = (miWidth % miRectSize) / 2;
		yOff = (miHeight % miRectSize) / 2;

		miRectMaxX = w / miRectSize;
		miRectMaxY = h / miRectSize;

		maTable = new int[miRectMaxX][miRectMaxY];
		maValue = new int[miRectMaxX][miRectMaxY];

		for (int i = 0; i < miLast1.length; i++)
			miLast1[i] = 0;
		for (int i = 0; i < miLast2.length; i++)
			miLast2[i] = 0;
	}

	public Bitmap drawGame()
	{
		int w = miWidth;
		int h = miHeight;

		w = w - (w % miRectSize);
		h = h - (h % miRectSize);

		miCanvasW = w;
		miCanvasH = h;

		xOff = (miWidth % miRectSize) / 2;
		yOff = (miHeight % miRectSize) / 2;

		miRectMaxX = w / miRectSize;
		miRectMaxY = h / miRectSize;

		maTable = new int[miRectMaxX][miRectMaxY];
		maValue = new int[miRectMaxX][miRectMaxY];

		if(bitmap != null)
		{
			bitmap.recycle();
			bitmap = null;
		}
		try
		{
			bitmap = Bitmap.createBitmap(miWidth, miHeight, Bitmap.Config.ARGB_4444);
			//bitmap.eraseColor(Color.TRANSPARENT);
			bitmap.eraseColor(this.miBackColor);
			canvas = new Canvas(bitmap);

			paint.setColor(miGridColor);
			paint.setStrokeWidth(miLieWidth / 2);
			for (int x = 0; x <= w; x += miRectSize)
				canvas.drawLine(x + xOff, yOff, x + xOff, h + yOff, paint);
			for (int y = 0; y <= h; y += miRectSize)
				canvas.drawLine(xOff, y + yOff, w + xOff, y + yOff, paint);
		}
		catch (OutOfMemoryError e)
		{
			Log.e(TAG, "Out of memory error in AmobaClass!");
		}
		catch (Exception ex)
		{
			Log.e(TAG, ex.getMessage());
		}

		return bitmap;
	}

	// draw players
	public void drawDebug()
	{
		if (maTable != null) for (int i = 0; i < miRectMaxX; i++)
			for (int j = 0; j < miRectMaxY; j++)
				if (debug && maValue[i][j] != 0) clearBack(i * miRectSize + xOff, j * miRectSize + yOff, true, this.miBackColor, false);
	}

	// show cell values for debug
	private void clearBack(int i, int j, boolean bNum, int color, boolean bSet)
	{
		if (canvas != null && !bitmap.isRecycled())
		{
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(color);
			if(bSet)canvas.drawRect(i + 2, j + 2, i + miRectSize - 2, j + miRectSize - 2, paint);
			else
			{
				for(int x = i + 2; x <= i + miRectSize - 2; x++)
				    for(int y = j + 2; y <=j + miRectSize - 2; y++)
				    	bitmap.setPixel(x, y, miBackColor);
			            //bitmap.setPixel(x, y, Color.TRANSPARENT);
			}
			if (bNum)
			{
				paint.setColor(0xFFFFFFFF);
				canvas.drawText(maValue[i / miRectSize][j / miRectSize] + "", i + miRectSize / 3, j + miRectSize / 2, paint);
			}
			paint.setStyle(Paint.Style.STROKE);
		}
	}

	// undo last step
	public void undo()
	{
		if (miLast1[2] > 0 && miLast2[2] > 0)
		{
			clearBack(miLast1[0] * miRectSize + xOff, miLast1[1] * miRectSize + yOff, false, this.miBackColor, false);
			clearBack(miLast2[0] * miRectSize + xOff, miLast2[1] * miRectSize + yOff, false, this.miBackColor, false);

			maTable[miLast1[0]][miLast1[1]] = 0;
			maTable[miLast2[0]][miLast2[1]] = 0;
			
			miLast1[2] = 0;
			miLast2[2] = 0;
		}
	}

	// check game end
	public int endGame()
	{
		int iRes = 0;
		for (int i = 0; i < miRectMaxX; i++)
		{
			for (int j = 0; j < miRectMaxY; j++)
			{
				if (maTable[i][j] == 1 || maTable[i][j] == 2) iRes = checkCell(i, j, 0);
				if (iRes != 0)
				{
					getAllFields();
					drawBorder();
					drawLine(miEndLine[0], miEndLine[1], miEndLine[2], miEndLine[3]);

					return iRes;
				}
			}
		}

		return iRes;
	}

	// check the state of a cell
	private int checkCell(int x, int y, int st)
	{
		int i, j;

		for (i = 0; i < 12; i++)
		{
			maPatternVal[i] = 0;
			for (j = 0; j < 9; j++)
				maPattern[i][j] = 0;
		}

		cereatePattern(x, y, 0, st, 1, 0); // right
		cereatePattern(x, y, 1, st, -1, 0); // left
		cereatePattern(x, y, 2, st, 0, -1); // up
		cereatePattern(x, y, 3, st, 0, 1); // down
		cereatePattern(x, y, 4, st, 1, -1); // right up
		cereatePattern(x, y, 5, st, 1, 1); // right down
		cereatePattern(x, y, 6, st, -1, -1); // left up
		cereatePattern(x, y, 7, st, -1, 1); // left down

		j = 0;
		for (i = 4 - st; i >= 0; i--)
		{
			maPattern[8][j] = maPattern[1][i];
			maPattern[9][j] = maPattern[2][i];
			maPattern[10][j] = maPattern[6][i];
			maPattern[11][j] = maPattern[7][i];
			j++;
		}
		addPattern(j + 1, 8, 0); // left right
		addPattern(j + 1, 9, 3); // up down
		addPattern(j + 1, 10, 5); // left-up right-down
		addPattern(j + 1, 11, 4); // left-down right-up

		int iMul = 0;
		int iRet = 0;
		for (i = 0; i < 8; i++)
		{
			iMul = mulPattern(maPattern[i], 0);
			if (iMul == 511111) iRet = setEndLine(x, y, i, 1);
			if (iMul == 522222) iRet = setEndLine(x, y, i, 2);
			if (iMul == 533333) iRet = setEndLine(x, y, i, 3);
			if (iMul == 544444) iRet = setEndLine(x, y, i, 4);
		}

		return iRet;
	}

	private int setEndLine(int x, int y, int i, int ply)
	{
		miEndLine[0] = x;
		miEndLine[1] = y;
		miEndLine[2] = i;
		miEndLine[3] = ply;

		return ply;
	}

	// add new element to the end of a pattern
	private void addPattern(int st, int dest, int src)
	{
		for (int i = 0; i < 9 - st; i++)
			maPattern[dest][i + st] = maPattern[src][i];
	}

	// create pattern
	private void cereatePattern(int x, int y, int cnt, int st, int rx, int ry)
	{
		int j = 0;
		for (int i = st; i < 5; i++)
		{
			// if inside
			if (x + (i * rx) >= 0 && x + (i * rx) < miRectMaxX && y + (i * ry) >= 0 && y + (i * ry) < miRectMaxY)
			{
				maPattern[cnt][j++] = maTable[x + (i * rx)][y + (i * ry)];
				// if the cell is used increase the it's value
				if (maTable[x + (i * rx)][y + (i * ry)] > 0) maPatternVal[cnt] += 5 - i;
			}
		}

		return;
	}

	// amoba AI
	public int[] amobaAI(int iPlayer)
	{
		int[] iRes = new int[2];

		int x = 0;
		int y = 0;

		for (x = 0; x < miRectMaxX; x++)
		{
			for (y = 0; y < miRectMaxY; y++)
			{
				if (maTable[x][y] == 0)
				{
					// fill pattern
					checkCell(x, y, 1);

					// process pattern
					maValue[x][y] = processPattern(iPlayer);
				}
			}
		}

		int c = 0;
		for (x = 0; x < miRectMaxX; x++)
		{
			for (y = 0; y < miRectMaxY; y++)
			{
				if (maValue[x][y] >= c && maTable[x][y] == 0)
				{
					if (maValue[x][y] > c)
					{
						iRes[0] = x;
						iRes[1] = y;
						c = maValue[x][y];
					}
					else if (random(0, 9, 1) > 4)
					{
						iRes[0] = x;
						iRes[1] = y;
					}
				}
			}
		}

		return iRes;
	}

	// process pattern
	private int processPattern(int iPlayer)
	{
		int[] iPattern = new int[9];
		int iVal = 0;
		int iMul = 0;
		int i;

		// normal pattern
		for (i = 0; i < 8; i++)
		{
			iPattern = maPattern[i];
			iVal = maPatternVal[i];
			iMul = mulPattern(iPattern);
			if (iMul != 50000)
			{
				// increase field value +
				if (iMul == 50111 || iMul == 50222) iVal += 1; // 0111 0222
				if (iMul == 51110 || iMul == 52220) iVal += 7; // 1110 2220
				if (iMul == 51100 || iMul == 52200) iVal += 3; // 1100 2200
				if (iMul == 51000 || iMul == 52000) iVal += 1; // 1000 2000
				if (iMul == 51111 || iMul == 52222) iVal += 10; // 1111 2222

				if (iMul == 51100 && iPattern[0] == iPlayer) iVal += 1; // 1100
				if (iMul == 51110 && iPattern[0] == iPlayer) iVal += 1; // 1110
				if (iMul == 51111 && iPattern[0] == iPlayer) iVal += 9; // 1111

				if (iMul == 52200 && iPattern[0] == iPlayer) iVal += 1; // 2200
				if (iMul == 52220 && iPattern[0] == iPlayer) iVal += 1; // 2220
				if (iMul == 52222 && iPattern[0] == iPlayer) iVal += 9; // 2222

				if (iMul == 51111 && iPattern[0] != iPlayer) iVal += 10; // 1111
				if (iMul == 52222 && iPattern[0] != iPlayer) iVal += 10; // 2222

				if (iMul == 51112 && iPattern[0] == iPlayer) iVal += 7; // 1112
				if (iMul == 52221 && iPattern[0] == iPlayer) iVal += 7; // 2221

				// decrease field value -
				if (iPattern[0] != iPlayer || iPattern[1] != iPlayer) iVal -= 3;

				maPatternVal[i] = iVal;
			}
		}

		// wide pattern
		int[] iDesPatt = new int[5];
		for (i = 8; i < 12; i++)
		{
			iPattern = maPattern[i];
			for (int j = 0; j < 5; j++)
			{
				copyPattern(iPattern, iDesPatt, j, 0, 5);

				iVal = widePattern(iDesPatt, 1);
				if (iVal > 1) maPatternVal[i] += iVal * 2;

				iVal = widePattern(iDesPatt, 2);
				if (iVal > 1) maPatternVal[i] += iVal * 2;
			}

			iMul = mulPattern(iPattern, 1);
			if (iMul == 511101 || iMul == 522202) iVal = 11; // 11101 22202

			iMul = mulPattern(iPattern, 2);
			if (iMul == 511011 || iMul == 522022) iVal = 11; // 11011 22022

			iMul = mulPattern(iPattern, 3);
			if (iMul == 510111 || iMul == 520222) iVal = 11; // 10111 20222

			maPatternVal[i] += iVal;
		}

		// A legangyobb értékkel térek vissza
		int iNo1 = 0;
		for (i = 0; i < 8; i++)
			if (iNo1 < maPatternVal[i]) iNo1 = maPatternVal[i];

		int iNo2 = 0;
		for (i = 8; i < 12; i++)
			if (iNo2 < maPatternVal[i]) iNo2 = maPatternVal[i];

		return iNo1 + iNo2;
	}

	//
	private int widePattern(int[] iDesPatt, int iPly)
	{
		int iVal = 0;
		for (int k = 0; k < 5; k++)
		{
			if (iDesPatt[k] == iPly || iDesPatt[k] == 0)
			{
				if (iDesPatt[k] == iPly) iVal++;
			}
			else
			{
				iVal = 0;
				break;
			}
		}

		return iVal;
	}

	// copy patterns
	private void copyPattern(int[] iSrc, int[] iDes, int st1, int st2, int num)
	{
		for (int i = 0; i < num; i++)
			iDes[st2++] = iSrc[st1++];
	}

	// multiply pattern
	private int mulPattern(int[] iPattern)
	{
		int iRet = 50000;
		int iMul = 100;
		for (int i = 0; i < 4; i++)
		{
			iRet += iMul * 10 * iPattern[i];
			iMul /= 10;
		}
		iRet += iPattern[3];

		return iRet;
	}

	// multiply pattern
	private int mulPattern(int[] iPattern, int iBeg)
	{
		int iRet = 500000;
		int iMul = 1000;
		for (int i = iBeg; i < iBeg + 5; i++)
		{
			iRet += iMul * 10 * iPattern[i];
			iMul /= 10;
		}
		iRet += iPattern[4];

		return iRet;
	}

	// cell number
	public int getRate(int x, int y)
	{
		if (x < miRectMaxX && y < miRectMaxY && maValue[x][y] != 0) return maValue[x][y];

		return -1;
	}

	// draw border around the games
	public void drawBorder()
	{
		for (int i = 0; i < miRectMaxX; i++)
			for (int j = 0; j < miRectMaxY; j++)
				if(maTable[i][j] > -5 && maTable[i][j] < 0) maTable[i][j] *= -1;
				else if(maTable[i][j] == -5 || maTable[i][j] == 5) maTable[i][j] = 0;

		for (int i = 0; i < miRectMaxX; i++)
			for (int j = 0; j < miRectMaxY; j++)
				setBorder(i, j);
		
		for (int i = 0; i < miRectMaxX; i++)
			for (int j = 0; j < miRectMaxY; j++)
				if(maTable[i][j] > 0 && maTable[i][j] < 5) maTable[i][j] *= -1;
	}

	// draw a border element
	private void setBorder(int x, int y)
	{
		int rx;
		int ry;
		int n = 0;
		int i = -1;

		if (maTable[x][y] == 0)
		{
			if (x > 0 && maTable[x - 1][y] > 0) n += 7000;
			if (x + 1 < miRectMaxX && maTable[x + 1][y] > 0) n += 700;

			if (y > 0 && maTable[x][y - 1] > 0) n += 70;
			if (y + 1 < miRectMaxY && maTable[x][y + 1] > 0) n += 7;

			if (n > 0)
			{
				if (n == 7777) i = 15;
				if (n == 7770) i = 2;
				if (n == 7707) i = 7;
				if (n == 7077) i = 6;
				if (n == 777) i = 3;
				if (n == 7700) i = 10;
				if (n == 7007) i = 4;
				if (n == 77) i = 11;
				if (n == 770) i = 1;
				if (n == 7000) i = 10;
				if (n == 700) i = 10;
				if (n == 70) i = 11;
				if (n == 7) i = 11;
				if (n == 7070) i = 0;
				if (n == 707) i = 5;
			}

			if (x > 0 && y > 0 && x + 1 < miRectMaxX && y + 1 < miRectMaxY)
			{
				// T
				if (maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y + 1] > 0
						&& maTable[x + 1][y + 1] > 0) i = 9;
				if (maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y - 1] > 0) i = 12;
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0) i = 8;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x - 1][y + 1] > 0) i = 13;

				// T
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y + 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x + 1][y] > 0) i = 13;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y + 1] > 0
						&& maTable[x + 1][y - 1] > 0 && maTable[x + 1][y] > 0) i = 13;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y - 1] > 0 && maTable[x + 1][y] > 0) i = 13;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x + 1][y] > 0) i = 13;

				// T
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y + 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x - 1][y] > 0) i = 8;
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y + 1] > 0
						&& maTable[x + 1][y - 1] > 0 && maTable[x - 1][y] > 0) i = 8;
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y - 1] > 0 && maTable[x - 1][y] > 0) i = 8;
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x - 1][y] > 0) i = 8;

				// T
				if (maTable[x - 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x - 1][y + 1] > 0 && maTable[x][y + 1] > 0) i = 12;
				if (maTable[x - 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x][y + 1] > 0) i = 12;
				if (maTable[x - 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x + 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x][y + 1] > 0) i = 12;
				if (maTable[x - 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x + 1][y - 1] > 0
						&& maTable[x - 1][y + 1] > 0 && maTable[x][y + 1] > 0) i = 12;

				// T
				if (maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x - 1][y + 1] > 0 && maTable[x][y - 1] > 0) i = 9;
				if (maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x - 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x][y - 1] > 0) i = 9;
				if (maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x + 1][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0 && maTable[x][y - 1] > 0) i = 9;
				if (maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x + 1][y - 1] > 0
						&& maTable[x - 1][y + 1] > 0 && maTable[x][y - 1] > 0) i = 9;

				// +
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0
						&& maTable[x + 1][y - 1] > 0 && maTable[x - 1][y + 1] > 0 && maTable[x + 1][y + 1] > 0) i = 14;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0
						&& maTable[x - 1][y - 1] > 0 && maTable[x - 1][y + 1] > 0 && maTable[x + 1][y + 1] > 0) i = 14;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0
						&& maTable[x - 1][y - 1] > 0 && maTable[x + 1][y - 1] > 0 && maTable[x + 1][y + 1] > 0) i = 14;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0
						&& maTable[x - 1][y - 1] > 0 && maTable[x + 1][y - 1] > 0 && maTable[x - 1][y + 1] > 0) i = 14;

				//
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] > 0
						&& maTable[x + 1][y - 1] > 0) i = 12;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] > 0
						&& maTable[x - 1][y - 1] > 0) i = 12;

				//
				if (maTable[x][y + 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y - 1] > 0
						&& maTable[x + 1][y + 1] > 0) i = 9;
				if (maTable[x][y + 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y - 1] > 0
						&& maTable[x - 1][y + 1] > 0) i = 9;

				//
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y] > 0
						&& maTable[x + 1][y - 1] > 0) i = 8;
				if (maTable[x][y - 1] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x - 1][y] > 0
						&& maTable[x + 1][y + 1] > 0) i = 8;

				//
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y] > 0
						&& maTable[x - 1][y - 1] > 0) i = 13;
				if (maTable[x][y - 1] <= 0 && maTable[x - 1][y] <= 0 && maTable[x][y + 1] <= 0 && maTable[x + 1][y] > 0
						&& maTable[x - 1][y + 1] > 0) i = 13;

				if (i == -1)
				{
					// 16 - 17 corner
					if (maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x][y + 1] <= 0
							&& maTable[x - 1][y + 1] > 0 && maTable[x + 1][y - 1] > 0) i = 17;
					if (maTable[x - 1][y] <= 0 && maTable[x + 1][y] <= 0 && maTable[x][y - 1] <= 0 && maTable[x][y + 1] <= 0
							&& maTable[x - 1][y - 1] > 0 && maTable[x + 1][y + 1] > 0) i = 16;
				}
			}

			if (i == -1)
			{
				if (x + 1 < miRectMaxX && y + 1 < miRectMaxY && maTable[x + 1][y + 1] > 0) i = 0;
				if (x + 1 < miRectMaxX && y > 0 && maTable[x + 1][y - 1] > 0) i = 4;
				if (x > 0 && y + 1 < miRectMaxY && maTable[x - 1][y + 1] > 0) i = 1;
				if (x > 0 && y > 0 && maTable[x - 1][y - 1] > 0) i = 5;
			}

			rx = x * miRectSize + xOff;
			ry = y * miRectSize + yOff;

			if (i != -1)
			{
				canvas.drawBitmap(border.tile(i), rx, ry, paint);
				//maTable[x][y] = -10 - i;
				maTable[x][y] = -5;
			}
		}

		return;
	}

	public void setColors(int i)
	{
		switch (i)
		{
		case 1:
			this.miBackColor = 0x66000000;
			this.miGridColor = 0xFFFFFFFF;
			this.miBorderColor = 0xFFFFFFFF;
			break;
		case 2:
			this.miBackColor = 0xCCFFFFFF;
			this.miGridColor = 0xFF00CCFF;
			this.miBorderColor = 0xFF000000;
			break;
		case 3:
			this.miBackColor = 0xCCC0C0C0;
			this.miGridColor = 0xFF3366FF;
			this.miBorderColor = 0xFF333333;
			break;
		}
		border = new BorderLine(miRectSize, miLieWidth, miBorderColor);
	}

	public String getAllFields()
	{
		String sFields = "";

		// write the table field
		for (int x = 0; x < this.miRectMaxX; x++)
			for (int y = 0; y < this.miRectMaxY; y++)
				sFields += this.maTable[x][y] + ";";

		sFields += miLast1[0] + ";" + miLast1[1] + ";" + miLast1[2];
		sFields += ";" + this.miRectMaxX + ";" + this.miRectMaxY;
		sFields += ";" + iWin[0] + ";" + iWin[1] + ";" + iWin[2] + ";" + iWin[3];

		saveGame = sFields;

		//Log.i(TAG, saveGame);

		return sFields;
	}

	public boolean setAllFields(String sFields, boolean bSaved)
	{
		String[] sArr = sFields.split(";");
		int iCnt = 0;

		try
		{
			int rx = Integer.parseInt(sArr[sArr.length - 6]);
			int ry = Integer.parseInt(sArr[sArr.length - 5]);

			if (this.miRectMaxX == rx && this.miRectMaxY == ry)
			{
				for (int x = 0; x < this.miRectMaxX; x++)
				{
					for (int y = 0; y < this.miRectMaxY; y++)
					{
						this.maTable[x][y] = Integer.parseInt(sArr[iCnt++]);

						if (maTable[x][y] == -1 || maTable[x][y] == 1) drawX(x * miRectSize + xOff, y * miRectSize + yOff);
						if (maTable[x][y] == -2 || maTable[x][y] == 2) drawO(x * miRectSize + xOff, y * miRectSize + yOff);
						if (maTable[x][y] == -3 || maTable[x][y] == 3) drawR(x * miRectSize + xOff, y * miRectSize + yOff);
						if (maTable[x][y] == -4 || maTable[x][y] == 4) drawT(x * miRectSize + xOff, y * miRectSize + yOff);
					}
				}
				
				miLast1[0] = Integer.parseInt(sArr[sArr.length - 9]);
				miLast1[1] = Integer.parseInt(sArr[sArr.length - 8]);
				miLast1[2] = Integer.parseInt(sArr[sArr.length - 7]);
				
				iWin[0] = Integer.parseInt(sArr[sArr.length - 4]);
				iWin[1] = Integer.parseInt(sArr[sArr.length - 3]);
				iWin[2] = Integer.parseInt(sArr[sArr.length - 2]);
				iWin[3] = Integer.parseInt(sArr[sArr.length - 1]);
				
				if(bSaved) drawSavedBorder();
				else drawBorder();

				return true;
			}
		}
		catch (Exception ex)
		{
			Log.e(TAG, ex.getMessage());
		}

		return false;
	}
	
	// Remove active steps, draw border, set active steps again
	public void drawSavedBorder()
	{
		int[][] aTmp = new int[miRectMaxX][miRectMaxY];
		// insert active steps onto temp table
		for (int x = 0; x < this.miRectMaxX; x++)
			for (int y = 0; y < this.miRectMaxY; y++)
				if(this.maTable[x][y] > 0)
				{
					aTmp[x][y] = this.maTable[x][y];
					this.maTable[x][y] = 0;
				}
		
		drawBorder();
		// draw back active stpes
		for (int x = 0; x < this.miRectMaxX; x++)
			for (int y = 0; y < this.miRectMaxY; y++)
				if(aTmp[x][y] > 0) this.maTable[x][y] =	aTmp[x][y];
	}

	private Bitmap creteButton(int w, int h, int color, boolean pressed, boolean border, String text)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		int borderColor = Color.WHITE;

		int[] colors = new int[3];
		if (pressed)
		{
			colors[0] = color;
			colors[1] = 0xFFFFFFFF;
			colors[2] = color;
		}
		else
		{
			colors[0] = 0xFFFFFFFF;
			colors[1] = color;
			colors[2] = 0xFFFFFFFF;
		}

		float[] pos = new float[3];
		pos[0] = 0f;
		pos[1] = 0.5f;
		pos[2] = 1f;

		LinearGradient gradient = new LinearGradient(0f, 0f, 0, (float) h, colors, pos, TileMode.CLAMP);

		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);

		RectF rect = new RectF(0, 0, w, h);
		if (border == false) canvas.drawRoundRect(rect, w / 5, h / 5, paint);

		//
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(borderColor);
		paint.setStrokeWidth(5);
		paint.setShader(null);

		canvas.drawRoundRect(rect, w / 5, h / 5, paint);

		// draw text
		paint.setTextSize(20);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(3, 0, 0, Color.BLACK);

		float f = paint.measureText(text);
		canvas.drawText(text, (w - f) / 2, 26, paint);

		return bitmap;
	}

	public void setName(String name)
	{
		playerName = name;

		int color = getColor(random(1, 8, 1));

		button1 = creteButton(120, 40, color, false, false, name);
		button2 = creteButton(120, 40, color, true, false, name);
		button3 = creteButton(120, 40, color, false, true, name);
	}

	private int getColor(int iColNum)
	{
		int uRetCol = 0;

		iColNum = iColNum % 10;

		switch (iColNum)
		{
		case 1: // Red
			uRetCol = Color.RED;
			break;
		case 2: // Green
			uRetCol = Color.GREEN;
			break;
		case 3: // Blue
			uRetCol = Color.BLUE;
			break;
		case 4: // Magenta
			uRetCol = Color.MAGENTA;
			break;
		case 5: // Yellow
			uRetCol = Color.YELLOW;
			break;
		case 6: // Cyan
			uRetCol = Color.CYAN;
			break;
		case 7: // White
			uRetCol = Color.WHITE;
			break;
		case 8: // Grey
			uRetCol = Color.GRAY;
			break;
		default:
			uRetCol = Color.BLACK;
		}

		return uRetCol;
	}

	public int random(int nMinimum, int nMaximum, int nRoundToInterval)
	{
		if (nMinimum > nMaximum)
		{
			int nTemp = nMinimum;
			nMinimum = nMaximum;
			nMaximum = nTemp;
		}

		int nDeltaRange = (nMaximum - nMinimum) + (1 * nRoundToInterval);
		double nRandomNumber = Math.random() * nDeltaRange;

		nRandomNumber += nMinimum;

		int nRet = (int) (Math.floor(nRandomNumber / nRoundToInterval) * nRoundToInterval);

		return nRet;
	}
	
	public int[] stPos(int sx, int sy,int w, int h, int cnt)
	{
		int[] iRes = null;
		
		// end of recursion
		if(cnt++ > 3) return iRes;

		iRes = checkEmpty(sx, sy);
		if(iRes == null) iRes = stPos(sx, sy, w / 2, h / 2, cnt);
		else return iRes;
		
		iRes = checkEmpty(sx + w / 2, sy);
		if(iRes == null) iRes = stPos(sx + w /2, sy, w / 2, h / 2, cnt);
		else return iRes;
		
		iRes = checkEmpty(sx, sy + h / 2);
		if(iRes == null) iRes = stPos(sx, sy + h / 2, w / 2, h / 2, cnt);
		else return iRes;
		
		iRes = checkEmpty(sx + w / 2, sy + h / 2);
		if(iRes == null) iRes = stPos(sx + w / 2, sy + h / 2, w / 2, h / 2, cnt);
		else return iRes;
		
		return iRes;
	}
	
	
	private int[] checkEmpty(int x, int y)
	{
		int[] iRes = null;
		
		if(x > 1 && x < miRectMaxX - 2 && y > 1 && y < miRectMaxY - 2)
		{
			if(maTable[x][y] == 0 
					&& maTable[x + 1][y] == 0 && maTable[x + 2][y] == 0 && maTable[x - 1][y] == 0 && maTable[x - 2][y] == 0
					&& maTable[x][y + 1] == 0 && maTable[x][y + 2] == 0 && maTable[x][y - 1] == 0 && maTable[x][y - 2] == 0)
			{
				iRes = new int[2];
				iRes[0] = x;
				iRes[1] = y;
			}
		}
		
		return iRes;
	}
	
	public Bitmap getBackGround(int w, int h, Resources res)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
		//bitmap.eraseColor(0xFFFF0000);
		Canvas canvas = new Canvas(bitmap);
		RectF rect = new RectF();

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		int color1 = 0xFF222222;
		int color2 = 0xFF882222;

		int[] colors = new int[2];
		colors[0] = color1;
		colors[1] = color2;

		float bw = w / 10;
		float bh = h / 40;
		float gap = w / 200;

		LinearGradient grad;

		for (int i = 0; i < 11; i++)
		{
			for (int j = 0; j < 40; j++)
			{
				if (random(0, 1, 1) == 1)
				{
					colors[0] = color1;
					colors[1] = color2 + random(0, 3, 1) * 0x1100;
				}
				else
				{
					colors[1] = color1;
					colors[0] = color2 + random(0, 3, 1) * 0x1100;
				}

				if (j % 2 == 0)
				{
					grad = new LinearGradient(i * bw, j * bh, i * bw + bw, j * bh + bh, colors, null,
							android.graphics.Shader.TileMode.REPEAT);
					rect.set(i * bw + gap, j * bh + gap, i * bw + bw - gap, j * bh + bh - gap);
				}
				else
				{
					grad = new LinearGradient(i * bw - bw / 2, j * bh, i * bw + bw - bw / 2, j * bh + bh, colors, null,
							android.graphics.Shader.TileMode.REPEAT);
					rect.set(i * bw + gap - bw / 2, j * bh + gap, i * bw + bw - gap - bw / 2, j * bh + bh - gap);
				}
				paint.setShader(grad);
				canvas.drawRect(rect, paint);
			}
		}
		
		paint.setShader(null);
		paint.setAlpha(64);
		
		float width = w / 2;
		float height = h / 2;
		
		float x, y, r, scaleWidth, scaleHeight;
		Bitmap img;

		scaleWidth = (float) width * (0.5f + ((float)random(0, 5, 1)) / 10) * 2;
		scaleHeight = (float) height * (0.5f + ((float)random(0, 5, 1)) / 10);
		img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.jewel1), (int)scaleWidth, (int)scaleHeight, false);
		x = random(0, (int)width - img.getWidth(), 1);
		y = random(0, (int)height - img.getHeight(), 1);
		canvas.drawBitmap(img, x, y, paint);
		
		scaleWidth = (float) width * (0.5f + ((float)random(0, 5, 1)) / 10);
		scaleHeight = (float) height * (0.5f + ((float)random(0, 5, 1)) / 10);
		img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.piper), (int)scaleWidth, (int)scaleHeight, false);
		x = random(0, (int)width - img.getWidth(), 1);
		y = random(0, (int)height - img.getHeight(), 1);
		canvas.drawBitmap(img, x + width, y + height, paint);
		
		scaleWidth = (float) width * (0.5f + ((float)random(0, 5, 1)) / 10);
		scaleHeight = (float) height * (0.5f + ((float)random(0, 5, 1)) / 10);
		img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.jewel2), (int)scaleWidth, (int)scaleHeight, false);
		x = random(0, (int)width - img.getWidth(), 1);
		y = random(0, (int)height - img.getHeight(), 1);
		r = random(0, 7, 1) * 45;
		canvas.drawBitmap(rotImage(img, r), x + width, y, paint);
		
		scaleWidth = (float) width * (0.5f + ((float)random(0, 5, 1)) / 10) * 2;
		scaleHeight = (float) height * (0.5f + ((float)random(0, 5, 1)) / 10) / 2;
		img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.colorizer), (int)scaleWidth, (int)scaleHeight, false);
		x = random(0, (int)width - img.getWidth(), 1);
		y = random(0, (int)height - img.getHeight(), 1);
		r = random(0, 7, 1) * 45;
		canvas.drawBitmap(rotImage(img, r), x, y + height, paint);

		return bitmap;
	}
	
	private Bitmap rotImage(Bitmap img, float rot)
	{
		int origWidth = img.getWidth();
		int origHeight = img.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(rot, img.getWidth() / 2, img.getHeight() / 2);
		Bitmap outImage = Bitmap.createBitmap(img, 0, 0, origWidth, origHeight, matrix, false);
		
		return outImage;
	}	
}
