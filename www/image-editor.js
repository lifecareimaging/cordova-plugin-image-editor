var ImageEditor = {
    editImage: function (path, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'ImageEditor', 'editImage', [path]);
    },
    getLastError: function (callback) {
        cordova.exec(callback, null, 'ImageEditor', 'getLastError', []);
    }
};

module.exports = ImageEditor;