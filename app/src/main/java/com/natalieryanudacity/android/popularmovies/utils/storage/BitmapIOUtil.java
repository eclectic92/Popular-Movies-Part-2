package com.natalieryanudacity.android.popularmovies.utils.storage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by natalier258 on 6/23/17.
 *
 * utility class for storing and reading bitmaps from file system
 */

@SuppressWarnings({"UnusedReturnValue"})
public class BitmapIOUtil {

    private static final String DIRECTORY = "images";
    private static final int COMPRESSION_QUALITY = 100;
    private static final String TAG = BitmapIOUtil.class.getSimpleName();

    public static boolean saveBitmap(Bitmap bitmap, String filename, Context context){

        boolean success = false;
        File directory = getDirectory(context);
        File imagePath=new File(directory, filename);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imagePath, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream != null){
                    outputStream.close();
                    success = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Saved " + imagePath);
        return success;
    }

    public static Bitmap readBitmap (String filename, Context context) {

        Bitmap savedImage = null;

        String path = getDirectory(context).getAbsolutePath();
        try {
            File bitmapFile = new File(path, filename);
            savedImage = BitmapFactory.decodeStream(new FileInputStream(bitmapFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return savedImage;
    }

    public static boolean deleteBitmap(@Nullable String filename, Context context){
        boolean success = false;

        if(filename != null && !filename.isEmpty()){
            String directory = getDirectory(context).getAbsolutePath();
            //String filePath = directory + "//" + filename;
            File targetfile = new File(directory, filename);

            if(targetfile.exists()){
                success = targetfile.delete();
            }
        }

        if(success){
            Log.d(TAG, "Deleted file " + filename);
        }else{
            Log.d(TAG, "Failed to delete file " + filename);
        }

        return success;
    }

    private static File getDirectory(Context context){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        return cw.getDir(DIRECTORY, Context.MODE_PRIVATE);
    }
}
