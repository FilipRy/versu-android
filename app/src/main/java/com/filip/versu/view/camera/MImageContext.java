

package com.filip.versu.view.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;

import com.android.mms.exif.ExifInterface;
import com.android.mms.exif.ExifTag;
import com.commonsware.cwac.cam2.CameraEngine.DeepImpactEvent;
import com.commonsware.cwac.cam2.ImageContext;

import de.greenrobot.event.EventBus;

import java.io.IOException;

public class MImageContext {
    private static final double LOG_2 = Math.log(2.0D);

    private Context ctxt;
    private byte[] jpegOriginal;
    private Bitmap bmp;
    private Bitmap thumbnail;
    private ExifInterface exif;


    MImageContext(ImageContext imageContext) {
        this.ctxt = imageContext.getContext();
        this.jpegOriginal = imageContext.getJpeg();
    }


    public Context getContext() {
        return this.ctxt;
    }

    public void setJpeg(byte[] jpeg) {
        this.jpegOriginal = jpeg;
        this.bmp = null;
        this.thumbnail = null;
    }

    public ExifInterface getExifInterface() throws IOException {
        if (this.exif == null) {
            this.exif = new ExifInterface();
            this.exif.readExif(this.jpegOriginal);
        }

        return this.exif;
    }

    public int getOrientation() throws IOException {
        ExifTag tag = this.getExifInterface().getTag(ExifInterface.TAG_ORIENTATION);
        return tag == null ? -1 : tag.getValueAsInt(-1);
    }


    public static final int MAX_RESULT_BITMAP_BYTES = 15000000;

    public Bitmap buildResultThumbnail(boolean normalizeOrientation) {
        return this.createBitmap(null, MAX_RESULT_BITMAP_BYTES, normalizeOrientation);
    }

    private Bitmap createBitmap(Bitmap inBitmap, int limit, boolean normalizeOrientation) {
        double ratio = (double) this.jpegOriginal.length * 10.0D / (double) limit;
        int inSampleSize;
        if (ratio > 1.0D) {
            inSampleSize = 1 << (int) Math.ceil(Math.log(ratio) / LOG_2);
        } else {
            inSampleSize = 1;
        }

        return this.createBitmap(inSampleSize, inBitmap, limit, normalizeOrientation);
    }

    private Bitmap createBitmap(int inSampleSize, Bitmap inBitmap, int limit, boolean normalizeOrientation) {
        Options opts = new Options();
        opts.inSampleSize = inSampleSize;
        opts.inBitmap = inBitmap;

        Bitmap result;
        try {
            result = BitmapFactory.decodeByteArray(this.jpegOriginal, 0, this.jpegOriginal.length, opts);
            if (limit > 0 && result.getByteCount() > limit) {
                return this.createBitmap(inSampleSize + 1, inBitmap, limit, normalizeOrientation);
            }
        } catch (OutOfMemoryError var9) {
            return this.createBitmap(inSampleSize + 1, inBitmap, limit, normalizeOrientation);
        }

        try {
            if (normalizeOrientation) {
                int e = this.getOrientation();
                if (this.needsNormalization(e)) {
                    result = rotateViaMatrix(result, e);
                }
            }
        } catch (IOException var8) {
            EventBus.getDefault().post(new DeepImpactEvent(var8));
        }

        return result;
    }


    private boolean needsNormalization(int orientation) {
        return orientation == 8 || orientation == 3 || orientation == 6;
    }

    private static Bitmap rotateViaMatrix(Bitmap original, int orientation) {
        Matrix matrix = new Matrix();
        matrix.setRotate((float) degreesForRotation(orientation));
        return Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
    }

    private static int degreesForRotation(int orientation) {
        short result;
        switch (orientation) {
            case 3:
                result = 180;
                break;
            case 8:
                result = 270;
                break;
            default:
                result = 90;
        }

        return result;
    }
}
