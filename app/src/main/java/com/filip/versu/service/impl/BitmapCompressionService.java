package com.filip.versu.service.impl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;

import com.filip.versu.model.dto.PostPhotoDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapCompressionService {

    public static final String TAG = BitmapCompressionService.class.getSimpleName();

    public static final int NEW_POST_PHOTO_REQ_WIDTH = 1200;
    public static final int NEW_POST_PHOTO_REQ_HEIGHT = 1200;
    public static final int NEWSFEED_SIMPLE_POST_PHOTO_REQ_WIDTH = 1024;
    public static final int NEWSFEED_SIMPLE_POST_PHOTO_REQ_HEIGHT = 1024;

    public static class TwoDimensional {

        private int width;
        private int height;


        public TwoDimensional(int width, int height) {
            super();
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }


    }

    private static int calculateInSampleSize(BitmapFactory.Options options, TwoDimensional reqDimens) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int reqWidth = reqDimens.getWidth();
        int reqHeight = reqDimens.getHeight();

        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((height / (inSampleSize * 2)) > reqHeight
                    && (width / (inSampleSize * 2)) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static File compressBitmapToFile(PostPhotoDTO originalPhoto, TwoDimensional reqDimens) {
        String originalBitmapFilename = originalPhoto.path;
        Bitmap original = decodeBitmapFromPath(originalBitmapFilename, reqDimens);
        return compressBitmapToFile(original, originalBitmapFilename);
    }

    public static File compressBitmapToFile(Bitmap original, String originalBitmapFilename) {
        File file = new File(createResizedBitmapFilename(originalBitmapFilename));
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.i(TAG, e.getMessage());// this should never happen
        }
        original.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        try {
            outputStream.close();
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        }

        return file;
    }

    /**
     * Writes the compressed bitmap into outputStream. Used to write the
     * compressed bitmap to e.g. opened HTTPUrlConnection OutputStream. Does not
     * close the outputstream.
     *
     * @param originalPhoto
     * @param reqDimens
     * @param outputStream
     */
    public static void compressBitmapToOutputStream(PostPhotoDTO originalPhoto, TwoDimensional reqDimens,
                                                    OutputStream outputStream) {
        String originalBitmapFilename = originalPhoto.path;
        Bitmap original = decodeBitmapFromPath(originalBitmapFilename, reqDimens);
        original.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
    }

    private static String createResizedBitmapFilename(String originalFilename) {
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return originalFilename + "_2";
        } else {
            String a = originalFilename.substring(0, lastDotIndex);
            String format = originalFilename.substring(lastDotIndex + 1);
            return a + "_2." + format;
        }
    }


    public static PostPhotoDTO compressBitmap(PostPhotoDTO originalPhoto) {
        TwoDimensional twoDimensional = new TwoDimensional(BitmapCompressionService.NEW_POST_PHOTO_REQ_WIDTH,
                BitmapCompressionService.NEW_POST_PHOTO_REQ_HEIGHT);
        return compressBitmap(originalPhoto, twoDimensional);
    }

    /*
     * creates new file with compressed photo
     */
    public static PostPhotoDTO compressBitmap(PostPhotoDTO originalPhoto, TwoDimensional reqDimens) {
        File file = compressBitmapToFile(originalPhoto, reqDimens);

        originalPhoto.path = file.getPath();
        return originalPhoto;
    }

    public static Bitmap decodeBitmapFromPath(String path, TwoDimensional reqDimens) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        int inSampleSize = calculateInSampleSize(options, reqDimens);
        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        Bitmap bitmapCorrectOrientation = BitmapCompressionService.ExifUtil.rotateBitmap(path,
                bitmap);

        return bitmapCorrectOrientation;
    }

    /**
     * getResizedBitmap method is used to Resized the Image according to custom
     * width and height
     *
     * @param image
     * @param newHeight (new desired height)
     * @param newWidth  (new desired Width)
     * @return image (new resized image)
     */
    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation

        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleFactor, scaleFactor);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
        image.recycle();
        return resizedBitmap;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device SPECIFIC display metrics
     * @return A float value to represent px equivalent to dp depending on
     * device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device SPECIFIC pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device SPECIFIC display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static class ExifUtil {
        /**
         * @see http://sylvana.net/jpegcrop/exif_orientation.html
         */
        public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
            try {
                int orientation = getExifOrientation(src);

                if (orientation == 1) {
                    return bitmap;
                }

                Matrix matrix = new Matrix();
                switch (orientation) {
                    case 2:
                        matrix.setScale(-1, 1);
                        break;
                    case 3:
                        matrix.setRotate(180);
                        break;
                    case 4:
                        matrix.setRotate(180);
                        matrix.postScale(-1, 1);
                        break;
                    case 5:
                        matrix.setRotate(90);
                        matrix.postScale(-1, 1);
                        break;
                    case 6:
                        matrix.setRotate(90);
                        break;
                    case 7:
                        matrix.setRotate(-90);
                        matrix.postScale(-1, 1);
                        break;
                    case 8:
                        matrix.setRotate(-90);
                        break;
                    default:
                        return bitmap;
                }

                try {
                    Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    return oriented;
                } catch (OutOfMemoryError e) {
                    Log.i(TAG, e.getMessage());
                    return bitmap;
                }
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
            }

            return bitmap;
        }

        private static int getExifOrientation(String src) throws IOException {
            int orientation = 1;

            try {
                ExifInterface exif = new ExifInterface(src);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                return orientation;
            } catch (SecurityException e) {
                Log.i(TAG, e.getMessage());
            } catch (IllegalArgumentException e) {
                Log.i(TAG, e.getMessage());
            }

            return orientation;
        }
    }

}
