package com.lifecare.cordova.editor;

public class ImageEditorConstants {
    public static final int IMAGE_URL_CODE = 1001;
    public static final String PROVIDER_EXTENSION = ".editor.provider";
    public static final String FILE_PREFIX = "file://";
    public static final String APPLICATION_ID_KEY = "APPLICATION_ID";
    public static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";

    static class ImageEditorAction {
        public static final String EDIT_IMAGE = "editImage";
        public static final String GET_LAST_ERROR = "Image path is invalid";
    }

    static class ImageEditorError {
        public static final int UNEXPECTED_ERROR = 0;
    }

    static class ExceptionMessage {
        public static final String INVALID_IMAGE_PATH = "Image path is invalid";
        public static final String MEDIA_ERROR = "Media error: ";
    }

    static class MediaStoreExtra {
        public static final String OUTPUT = "output";
        public static final String RETURN_DATA = "return-data";
    }

    static class FileType {
        public static final String IMAGE = "image/*";
    }
}
