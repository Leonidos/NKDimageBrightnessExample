package com.pandacoder.ndkexample.Activities;

import com.pandacoder.ndkexample.R;
import com.pandacoder.ndkexample.NdkImageProcessor.ImageProcessor;
import com.pandacoder.ndkexample.Utils.ImageUtils;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	
	private ImageView imageView;
	private Bitmap originalImage, workImage;
	private SeekBar brightnessLevelBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imageView =  (ImageView)findViewById(R.id.imageView);
        brightnessLevelBar = (SeekBar)findViewById(R.id.brightnessLevelBar);
        brightnessLevelBar.setOnSeekBarChangeListener(brightnessBarListener);
    }
	
	private OnSeekBarChangeListener brightnessBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) { }

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (originalImage == null) {
				cacheOriginalImage();
				initWorkImage();
			}
			
			int brightnessValue = getBrightnessBarValue(); 				
			ImageProcessor.AdjustImageBrightness(originalImage, workImage, brightnessValue);
			imageView.setImageBitmap(workImage);			
		}		
	};
	
	/**
	 * Считывает показание контрастности, заданное бегунком
	 * @return
	 */
	private int getBrightnessBarValue() {
		return brightnessLevelBar.getProgress() - brightnessLevelBar.getMax()/2;
	}
	
	/**
	 * Создает рабочее изображение, скопировав исходное.
	 */
	private void initWorkImage() {
		workImage = originalImage.copy(Config.ARGB_8888, true);
	}
	
	/**
	 * Кеширует исходное изображение доставая его из imageView, проверяет
	 * его формат. При необходимости преобразовывает его в ARGB_8888.
	 */
    private void cacheOriginalImage() {
		originalImage = ImageUtils.DrawableToBitmap(imageView.getDrawable());
		if (originalImage.getConfig() != Config.ARGB_8888) {
			originalImage = originalImage.copy(Config.ARGB_8888, false);
		}
	}
}

