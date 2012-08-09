package com.pandacoder.ndkexample.Utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;

public class ImageUtils {
		
	/**
	 * Преобразовывает Drawable в Bitmap
	 * спасибо: http://stackoverflow.com/a/10600736
	 * @param drawable
	 * @return
	 */
	public static Bitmap DrawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	/**
	 * Сравнивает параметры изображений. Если изображения одинакового размера и формата
	 * пикселей, изображения считаются одинаковыми. В противном случае - различными. Если
	 * оба или одно из изображений равно null, то изображения считаются различными.
	 * 
	 * @param first первой изображение
	 * @param second второе изображение
	 * @return true - если однаковы, false - если разные
	 */
	public static boolean SameImageParams(Bitmap first, Bitmap second) {
		
		if (first == null || second == null) {
			return false;		
		} else if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
			return false;			
		} else if (first.getConfig() != second.getConfig()) {
			return false;
		} else {
			return true;
		}
	}
}
