package com.lifecare.cordova.editor;

import com.lifecare.cordova.editor.ImageEditorConstants.ExceptionMessage;
import com.lifecare.cordova.editor.ImageEditorConstants.MediaStoreExtra;
import com.lifecare.cordova.editor.ImageEditorConstants.FileType;
import com.lifecare.cordova.editor.ImageEditorConstants.ImageEditorError;
import com.lifecare.cordova.editor.ImageEditorConstants.ImageEditorAction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import org.apache.cordova.BuildHelper;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

public class ImageEditor extends CordovaPlugin {

    private CallbackContext callbackContext;
    private Exception lastException = null;

    private String applicationId;

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext)
            throws JSONException {

        this.applicationId = (String) BuildHelper.getBuildConfigValue(cordova.getActivity(), ImageEditorConstants.APPLICATION_ID_KEY);
        this.callbackContext = callbackContext;

        try {
            if (action.equals(ImageEditorAction.EDIT_IMAGE)) {
                String imagePath = args.getString(0);

                if (imagePath == null || imagePath.isEmpty()) {
                    throw new Exception(ExceptionMessage.INVALID_IMAGE_PATH);
                }

                openImageEditor(imagePath);
                return true;
            }

            if (action.equals(ImageEditorAction.GET_LAST_ERROR)) {
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

    private void openImageEditor(String imagePath) {
        Intent intent = new Intent(Intent.ACTION_EDIT);

        Uri imageUri = getUriWithFileProvider(imagePath);

        intent.setDataAndType(imageUri, FileType.IMAGE);
        intent.setPackage(ImageEditorConstants.GOOGLE_PHOTOS_PACKAGE_NAME);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.putExtra(MediaStoreExtra.OUTPUT, imageUri);
        intent.putExtra(MediaStoreExtra.RETURN_DATA, true);

        cordova.startActivityForResult((CordovaPlugin) this, intent, ImageEditorConstants.IMAGE_URL_CODE);
    }

    private Uri getUriWithFileProvider(String path) {
        File imagefile = new File(path.replaceFirst(ImageEditorConstants.FILE_PREFIX, ""));

        Uri fileUri = FileProvider.getUriForFile(cordova.getActivity(),
                applicationId + ImageEditorConstants.PROVIDER_EXTENSION,
                imagefile);

        return fileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageEditorConstants.IMAGE_URL_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                callbackContext.success(data.getDataString());
                return;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                callbackContext.success();
                return;
            }

            callbackContext.error(ExceptionMessage.MEDIA_ERROR + resultCode);
        }
    }
}