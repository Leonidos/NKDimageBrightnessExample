/*
 * ImageProcessor.c
 *
 *  Created on: Aug 8, 2012
 *
 */
#include <ImageProcessor.h>

#include <android/bitmap.h>

static void ProcessImagePixelsBrightness(AndroidBitmapInfo *imageInfo,
		const void* originalImagePixels, void* adjustedImagePixels,
		int32_t brightness);
static void ThrowErrorJavaException(JNIEnv * env, const char * errorMessage);

JNIEXPORT void JNICALL Java_com_pandacoder_ndkexample_NdkImageProcessor_ImageProcessor_AdjustImageBrightnessNDK(
		JNIEnv * env, jclass cls, jobject originalImage, jobject adjustedImage,
		jint brightnessLevel) {

	AndroidBitmapInfo imageInfo;
	void* originalImagePixels;
	void* adjustedImagePixels;
	int returnCode;

	// получаем информацию о исходном изображении
	if ((returnCode = AndroidBitmap_getInfo(env, originalImage, &imageInfo)) < 0) {
		ThrowErrorJavaException(env, "AndroidBitmap_getInf returned error!");
		return;
	}

	// получаем доступ к пикселям исходного изоражения
	if ((returnCode = AndroidBitmap_lockPixels(env, originalImage, &originalImagePixels)) < 0) {
		ThrowErrorJavaException(env, "AndroidBitmap_lockPixels() failed!");
		return;
	}

	// получаем доступ к пикселяем преобразованного изображения
	if ((returnCode = AndroidBitmap_lockPixels(env, adjustedImage, &adjustedImagePixels)) < 0) {
		ThrowErrorJavaException(env, "AndroidBitmap_lockPixels() failed!");
		return;
	}

	// производим расчет изначений цвета пикселей, с учетом требуемой яркости
	ProcessImagePixelsBrightness(&imageInfo, originalImagePixels, adjustedImagePixels, brightnessLevel);

	// после завершения работы с изображениями, нужно вызвать функцию
	// AndroidBitmap_unlockPixels для каждого изображения для которого
	// вызывалась функция AndroidBitmap_lockPixels
	AndroidBitmap_unlockPixels(env, adjustedImage);
	AndroidBitmap_unlockPixels(env, originalImage);
}

/*
 * Генерирует Java исключение класса ImageProcessorException с заданным сообщением. Может геренировать
 * ClassNotFoundException, если ImageProcessorException не найден.
 */
static void ThrowErrorJavaException(JNIEnv * env, const char * errorMessage) {
	jclass exceptionClass = (*env)->FindClass(env, "com/pandacoder/ndkexample/NdkImageProcessor/ImageProcessorException");
	if (exceptionClass != NULL) {
		(*env)->ThrowNew(env, exceptionClass, errorMessage);
	}
	(*env)->DeleteLocalRef(env, exceptionClass);
}

/*
 * Преобразовывает значение переданного цвета в допустимый
 * диапазон [0,255]
 *
 * параметры:
 * 	color - значение цвета
 *
 * return:
 * 	приведенное к допустимому диапазону значение
 */
static int32_t LimitColorRange(int32_t color) {
	if (color < 0)			return 0;
	else if (color > 255)	return 255;
	else					return color;
}

/*
 * Преобразовывает яркость пикселей из оригинального массива
 * согласно параметру яркости. Результат сохраняется в
 * дополнительном массиве. Оригинальный массив не изменяется.
 * массиве
 *
 * параметры:
 * 	imageInfo - параметры исходного изображения
 * 	originalImagePixels - исходный массив пикселей
 * 	adjustedImagePixels - массив пикселей для сохранения результата
 * 	brightness - значение яркости [-255...255]
 */
static void ProcessImagePixelsBrightness(AndroidBitmapInfo *imageInfo,
		const void* originalImagePixels, void* const adjustedImagePixels,
		int32_t brightness) {

	const uint32_t width = imageInfo->width, height = imageInfo->height, stride = imageInfo->stride;

	uint32_t* srcPixelsRow = (uint32_t*) originalImagePixels;
	uint32_t* destPixelsRow = (uint32_t*) adjustedImagePixels;
	uint32_t y, x;

	for (y = 0; y < height; y++) {
		for (x = 0; x < width; x++) {

			// считываем исходные цвет текущего пикселя
			uint32_t pixelRGBcolor = srcPixelsRow[x];

			// разбиваем цвет на RGB компоненты
			int32_t R = (pixelRGBcolor >> 16) & 0xFF;
			int32_t G = (pixelRGBcolor >> 8) & 0xFF;
			int32_t B = (pixelRGBcolor >> 0) & 0xFF;

			// изменяем яркость компонент, корректируем
			// диапазон значения компонент [0...255]
			R = LimitColorRange(brightness + R);
			G = LimitColorRange(brightness + G);
			B = LimitColorRange(brightness + B);

			// преобразованное значение цвета
			pixelRGBcolor = (R << 16) | (G << 8) | B;

			// сохраняем пассчитанное значение в рельтирующем
			// массиве
			destPixelsRow[x] = pixelRGBcolor;
		}

		// перемещаемся на новую строчку в изображении
		srcPixelsRow = (uint32_t*) ((uint8_t*) srcPixelsRow + stride);
		destPixelsRow = (uint32_t*) ((uint8_t*) destPixelsRow + stride);
	}
}

