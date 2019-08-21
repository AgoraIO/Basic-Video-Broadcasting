package io.agora.openlive.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class FileUtil {
    private static final String LOG_FOLDER_NAME = "log";
    private static final String LOG_FILE_NAME = "agora-rtc.log";

    /**
     * Initialize the log folder
     * @param context Context to find the accessible file folder
     * @return the absolute path of the log file
     */
    public static String initializeLogFile(Context context) {
        File folder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), LOG_FOLDER_NAME);
        } else {
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator +
                    context.getPackageName() + File.separator +
                    LOG_FOLDER_NAME;
            folder = new File(path);
            if (!folder.exists() && !folder.mkdir()) folder = null;
        }

        if (folder != null && !folder.exists() && !folder.mkdir()) return "";
        else return new File(folder, LOG_FILE_NAME).getAbsolutePath();
    }
}
