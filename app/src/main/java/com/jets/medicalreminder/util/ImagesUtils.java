package com.jets.medicalreminder.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;

public class ImagesUtils {

    private ImagesUtils() {
    }


    // Scale Image
    public static Bitmap scaleImage(Bitmap image, ImageView imageView) {
        return scaleImage(image, imageView.getWidth(), imageView.getHeight());
    }

    public static Bitmap scaleImage(Bitmap image, int scaleWidth, int scaleHeight) {
        double imageViewWidthHeightRatio = (double) scaleWidth / scaleHeight;
        double imageWidthHeightRatio = (double) image.getWidth() / image.getHeight();

        int width, height;

        if (imageWidthHeightRatio > imageViewWidthHeightRatio) {
            width = scaleWidth;
            double ratio = (double) image.getWidth() / width;
            height = (int) (image.getHeight() / ratio);
        } else {
            height = scaleHeight;
            double ratio = (double) image.getHeight() / height;
            width = (int) (image.getWidth() / ratio);
        }

        return Bitmap.createScaledBitmap(image, width, height, false);
    }


    // Get Scaled Image
    public static Bitmap getScaledImage(String path, int scaleFactor) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(path, options);
    }


    // Get Image Rotation
    public static int getImageRotation(String path) throws IOException {
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotation = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotation = 270;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotation = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotation = 90;
        }

        return rotation;
    }


    // Get Adjusted Image
    public static Bitmap getAdjustedImage(String path) throws IOException {
        return getAdjustedImage(path, 1);
    }

    public static Bitmap getAdjustedImage(String path, int scaleFactor) throws IOException {
        Bitmap image = getScaledImage(path, scaleFactor);

        int rotation = ImagesUtils.getImageRotation(path);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        }

        return image;
    }

}
