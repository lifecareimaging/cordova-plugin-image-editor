//
//  ImageEditor.swift
//  Lifecare Camera
//
//  Created by Marko Pulkkinen on 7.10.2021.
//

import Foundation
import UIKit
import ZLImageEditor

@objc(ImageEditor)
class ImageEditor: CDVPlugin {
    
    var resultImageEditModel: ZLEditImageModel?
    
    @objc func editImage(_ command: CDVInvokedUrlCommand) {
        let path = command.arguments[0] as! String
        let url:NSURL = NSURL(string: path)!
        
        let imageData = NSData(contentsOf: url as URL)
        guard let image = UIImage(data: imageData! as Data) else { return }
        
        do {
            try self.editSelectedImage(image, editModel: self.resultImageEditModel, command: command) { (resultImage: UIImage?) throws -> Void in
                if (resultImage != nil) {
                    if let resultImageData = UIImageJPEGRepresentation(resultImage!, 0.7) {
                        _ = FileManager()
                        
                        let documentsPath = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0] as NSString
                        let fileName = url.lastPathComponent
                        let fileExtension = (fileName! as NSString).pathExtension
                        let pathPrefix = (fileName! as NSString).deletingPathExtension

                        let outputPath = "\(documentsPath)/\(pathPrefix)_1.\(fileExtension)"

                        let outputFileURL = URL(fileURLWithPath: outputPath)
                        
                        try resultImageData.write(to: outputFileURL)
                        self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK, messageAs: outputFileURL.absoluteString), callbackId: command.callbackId)
                    }
                } else {
                    // Image editing was cancelled
                    self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK), callbackId: command.callbackId)
                }
            }
        } catch {
            self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "General error: \(error.localizedDescription)"), callbackId: command.callbackId);
        }
    }
    
    func editSelectedImage(_ image: UIImage, editModel: ZLEditImageModel?, command: CDVInvokedUrlCommand, completion:@escaping (_ resImage: UIImage?) throws -> ()) throws {
        ZLImageEditorConfiguration.default().editImageTools([.draw, .clip, .imageSticker, .textSticker])

        ZLEditImageViewController.showEditImageVC(parentVC: self.viewController, image: image, editModel: editModel) { [weak self] (resImage, editModel) in
            if (image.isEqual(resImage)) {
                do {
                    try completion(nil)
                } catch {
                    self?.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Error canceling image editing: \(error.localizedDescription)"), callbackId: command.callbackId);
                }
            } else {
                do {
                    try completion(resImage)
                } catch {
                    self?.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Error saving edited image: \(error.localizedDescription)"), callbackId: command.callbackId);
                }
            }
        }
    }
}
