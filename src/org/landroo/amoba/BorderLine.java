package org.landroo.amoba;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class BorderLine 
{
	private Bitmap bitmap;
	private Canvas canvas;
	private int size;
	private Paint paint = new Paint();
	private Rect dst;
	
	public BorderLine(int size, int width, int color)
	{
		this.size = size;
		dst = new Rect(0, 0, size, size);
		
		bitmap = Bitmap.createBitmap(size * 4, size * 4, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);
		
		canvas = new Canvas(bitmap);
		
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(width);
		paint.setColor(color);
		
		// circle
		canvas.drawCircle(size, size, size / 2, paint);

		// end lines
		canvas.drawLine(size * 2 + size / 2, size / 2, size * 2 + size / 2, size, paint);
		canvas.drawCircle(size * 2 + size / 2, size / 2, size / 10, paint);
		canvas.drawLine(size * 3, size / 2, size * 3 + size / 2, size / 2, paint);
		canvas.drawCircle(size * 3 + size / 2, size / 2, size / 10, paint);
		canvas.drawLine(size * 2 + size / 2, size + size / 2, size * 3, size + size / 2, paint);
		canvas.drawCircle(size * 2 + size / 2, size + size / 2, size / 10, paint);
		canvas.drawLine(size * 3 + size / 2, size + size / 2, size * 3 + size / 2, size, paint);
		canvas.drawCircle(size * 3 + size / 2, size + size / 2, size / 10, paint);
		
		// T lines
		canvas.drawLine(size / 2, size * 2, size / 2, size * 3 + size / 2, paint);
		canvas.drawLine(size / 2, size * 2 + size / 2, size * 2, size * 2 + size / 2, paint);
		canvas.drawLine(0, size * 3 + size / 2, size + size / 2, size * 3 + size / 2, paint);
		canvas.drawLine(size + size / 2, size * 2 + size / 2, size + size / 2, size * 4, paint);
		
		// lines
		canvas.drawLine(size * 2 + size / 2, size * 2, size * 2 + size / 2, size * 3, paint);
		canvas.drawLine(size * 3, size * 2 + size / 2, size * 4, size * 2 + size / 2, paint);
		canvas.drawLine(size * 2, size * 3 + size / 2, size * 3, size * 3 + size / 2, paint);
		canvas.drawLine(size * 2 + size / 2, size * 3, size * 2 + size / 2, size * 4, paint);
		canvas.drawCircle(size * 3 + size / 2, size * 3 + size / 2, size / 10, paint);
	}
	
	public Bitmap tile(int iNo)
	{
		int x = iNo % 4;
		int y = iNo / 4;
		
		Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		if(iNo < 16)
		{
			Rect rect = new Rect(x * size, y * size, x * size + size, y * size + size);
			canvas.drawBitmap(this.bitmap, rect, dst, paint);
		}
		else
		{
			if(iNo == 16)
			{
				Rect rect1 = new Rect(0, 0, size, size);
				canvas.drawBitmap(this.bitmap, rect1, dst, paint);
				Rect rect2 = new Rect(size, size, size + size, size + size);
				canvas.drawBitmap(this.bitmap, rect2, dst, paint);
			}

			if(iNo == 17)
			{
				Rect rect1 = new Rect(size, 0, size + size, size);
				canvas.drawBitmap(this.bitmap, rect1, dst, paint);
				Rect rect2 = new Rect(0, size, size, size + size);
				canvas.drawBitmap(this.bitmap, rect2, dst, paint);
			}
		}
		
		return bitmap;
	}	
		
}
