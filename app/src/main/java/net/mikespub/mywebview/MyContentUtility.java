package net.mikespub.mywebview;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;

/**
 * Content Utility Methods
 */
class MyContentUtility {
    private static final String TAG = "Content";

    /**
     * Show detail of a content uri via content resolver
     *
     * @param activity  current Activity context
     * @param uri       content uri to show
     */
    /*
    Content: 0 name: _id type: 1 value: 47
    Content: 1 name: entity type: 0 value: null
    Content: 2 name: _data type: 3 value: /storage/emulated/0/Android/data/net.mikespub.mywebview/files/assets.zip
    Content: 3 name: mimetype type: 3 value: application/zip
    Content: 4 name: visibility type: 1 value: 0
    Content: 5 name: destination type: 1 value: 4
    Content: 6 name: control type: 0 value: null
    Content: 7 name: status type: 1 value: 200
    Content: 8 name: lastmod type: 1 value: 540540424
    Content: 9 name: notificationpackage type: 3 value: net.mikespub.mywebview
    Content: 10 name: notificationclass type: 0 value: null
    Content: 11 name: total_bytes type: 1 value: 6313
    Content: 12 name: current_bytes type: 1 value: 6313
    Content: 13 name: title type: 3 value: assets.zip
    Content: 14 name: description type: 3 value:
    Content: 15 name: uri type: 3 value: https://github.com/mikespub/android-webview/raw/master/app/release/updates/assets.zip
    Content: 16 name: is_visible_in_downloads_ui type: 1 value: 1
    Content: 17 name: hint type: 3 value: file:///storage/emulated/0/Android/data/net.mikespub.mywebview/files/assets.zip
    Content: 18 name: mediaprovider_uri type: 0 value: null
    Content: 19 name: deleted type: 1 value: 0
    Content: 20 name: _display_name type: 3 value: assets.zip
    Content: 21 name: _size type: 1 value: 6313
     */
    static void showContent(AppCompatActivity activity, Uri uri) {
        Log.d(TAG, "URI: " + uri);
        Cursor cursor = activity.getContentResolver().query(uri,null,null,null,null);
        if (cursor.moveToFirst()) {
            showCursor(cursor);
        }
        cursor.close();
    }

    /**
     * Show columns for current cursor
     *
     * @param cursor    current cursor
     */
    static void showCursor(Cursor cursor) {
        //String local_uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        //Log.d(TAG, "Local URI: " + local_uri);
        //String[] columns = cursor.getColumnNames();
        //Log.d(TAG, "Columns: " + Arrays.toString(columns));
        for (int i=0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_NULL:
                    Log.d(TAG, i + " name: " + cursor.getColumnName(i) + " type: " + cursor.getType(i) + " value: null");
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    Log.d(TAG, i + " name: " + cursor.getColumnName(i) + " type: " + cursor.getType(i) + " value: " + cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    Log.d(TAG, i + " name: " + cursor.getColumnName(i) + " type: " + cursor.getType(i) + " value: " + cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    Log.d(TAG, i + " name: " + cursor.getColumnName(i) + " type: " + cursor.getType(i) + " value: " + cursor.getString(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    Log.d(TAG, i + " name: " + cursor.getColumnName(i) + " type: " + cursor.getType(i) + " value: " + Arrays.toString(cursor.getBlob(i)));
                    break;
                default:
                    Log.d(TAG, i + " name: " + cursor.getColumnName(i) + " type: " + cursor.getType(i) + " value: ?");
                    break;
            }
        }
        Log.d(TAG, "Extras: " + cursor.getExtras().toString());
    }

    /**
     * Show app downloads via content provider
     *
     * @param activity  current Activity context
     */
    static void showMyDownloadFiles(AppCompatActivity activity) {
        //static android.provider.Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI;
        //ALL_DOWNLOADS_CONTENT_URI
        Uri uri = Uri.parse("content://downloads/my_downloads");
        //Uri uri = Uri.parse("content://downloads/public_downloads");
        //Uri uri = Uri.parse("content://downloads/all_downloads");
        //activity.grantUriPermission(activity.getPackageName(),uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d(TAG, "Uri: " + uri.toString());
        Cursor cursor = activity.getContentResolver().query(uri,null,null,null,null);
        // MediaStore.Downloads.EXTERNAL_CONTENT_URI
        while (cursor.moveToNext()) {
            showCursor(cursor);
        }
        cursor.close();
    }

    /**
     * Get external downloads dir for this app
     *
     * @param activity  current Activity context
     * @return          external downloads directory
     */
    static File getMyExternalDownloadsDir(AppCompatActivity activity) {
        return activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * Show external download files for this app
     *
     * @param activity  current Activity context
     */
    static void showMyExternalDownloadFiles(AppCompatActivity activity) {
        File downloadsDir = getMyExternalDownloadsDir(activity);
        Log.d(TAG, "Dir: " + downloadsDir.getAbsolutePath());
        for (File file: downloadsDir.listFiles()) {
            Log.d(TAG, "File: " + file.getAbsolutePath());
        }
    }

    /**
     * Get DownloadManager service
     *
     * @param activity  current Activity context
     * @return          download manager
     */
    static DownloadManager getDownloadManager(AppCompatActivity activity) {
        return (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /**
     * Get download status for a download id
     *
     * @param activity      current Activity context
     * @param downloadId    download id
     * @return              current status
     */
    static int getDownloadStatus(AppCompatActivity activity, int downloadId) {
        DownloadManager mDownloadManager = getDownloadManager(activity);
        // query download status
        Cursor cursor = mDownloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if(status == DownloadManager.STATUS_SUCCESSFUL){
                // download is successful
                //String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                //File file = new File(Uri.parse(uri).getPath());
                Uri uri = mDownloadManager.getUriForDownloadedFile(downloadId);
                if (uri != null) {
                    Log.d(TAG, "URI: " + uri);
                    //InputStream inputStream = activity.getContentResolver().openInputStream(uri);
                }
            }
            else {
                // download is assumed cancelled
            }
            return status;
        }
        else {
            // download is assumed cancelled
            return DownloadManager.STATUS_FAILED;
        }
    }

}