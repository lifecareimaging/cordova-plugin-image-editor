package com.lifecare.cordova.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class ImageEditor extends CordovaPlugin {
    private CallbackContext callbackContext;
    private Exception lastException = null;
    private String imagePath;

    static class ImageEditorError {
        private static final int UNEXPECTED_ERROR = 0;
    }

    private static final int IMAGE_URL = 1001;

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        this.callbackContext = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();

        try {
            if (action.equals("editImage")) {
                try {
                    imagePath = args.getString(0);

                    if (imagePath == null || imagePath.isEmpty()) {
                        throw new Exception("Image path is invalid");
                    }
                    
                    openNewActivity(context);
                    return true;
                } catch (Exception e) {
                    lastException = e;
                    callbackContext.error(ImageEditorError.UNEXPECTED_ERROR);
                    return false;
                }
            } 
            
            if (action.equals("getLastError")) {
                callbackContext.success(lastException.getMessage());
                return true;
            }
            return false;
        } catch (Exception e) {
            lastException = e;
            callbackContext.error(ImageEditorError.UNEXPECTED_ERROR);
            return false;
        }
    }

    private void openNewActivity(Context context) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        Uri uri = Uri.parse(imagePath);
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cordova.startActivityForResult((CordovaPlugin) this, Intent.createChooser(intent, null), IMAGE_URL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_URL) {
            if (resultCode == Activity.RESULT_OK) {
                callbackContext.success(data.getDataString());
                return;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                callbackContext.success();
                return;
            }

            callbackContext.error("Media error: " + resultCode);
        }
    }
}