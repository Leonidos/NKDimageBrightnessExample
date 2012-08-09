package com.pandacoder.ndkexample.NdkImageProcessor;

import com.pandacoder.ndkexample.Utils.ImageUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class ImageProcessor {

	private static native void AdjustImageBrightnessNDK(Bitmap originalImage, Bitmap adjustedImage, int brightnessLevel);
	static {
		System.loadLibrary("ImageProcessor");
	}
	
	/**
	 * Изменяет яркость originalImage, согласно параметру brightnessLevel.
	 * Сохраняет результат в adjustedImage. Оба изображения должны быть созданы 
	 * до вызова этой функции и иметь одинаковый размер и ARGB_8888 формат.
	 * 
	 * @param originalImage исходное изображение
	 * @param adjustedImage готовое изображение
	 * @param brightnessLevel уровень яркости -255...+255
	 * 
	 * @throws NullPointerException if originalImage is null
	 * @throws IllegalArgumentException if brightnessLevel is illegal
	 * @throws ImageProcessorException if other image process error occurred
	 */
	public static void AdjustImageBrightness(Bitmap originalImage, Bitmap adjustedImage, int brightnessLevel) {
		
		CheckBitmapParams(originalImage, adjustedImage);
		CheckBrightnessValue(brightnessLevel);
		
		AdjustImageBrightnessNDK(originalImage, adjustedImage, brightnessLevel);
	}
	
	private static void CheckBitmapParams(Bitmap originalImage, Bitmap adjustedImage) {
		
		if (ImageUtils.SameImageParams(originalImage, adjustedImage) == false) {
			throw new ImageProcessorException("Bimap.sameAs says that originalImage and adhustedImage not the same");		
		}
		
		if (originalImage.getConfig() != Config.ARGB_8888) {
			throw new ImageProcessorException("Wrong originalImage config. Should be ARGB_8888");
		}
	}
	
	private static void CheckBrightnessValue(int brightnessLevel) {
		if (brightnessLevel > 255 || brightnessLevel < -255) {
			throw new IllegalArgumentException("Wrong brightness level " + brightnessLevel + ". Should be in [-255...255] range");
		}
	}
}
