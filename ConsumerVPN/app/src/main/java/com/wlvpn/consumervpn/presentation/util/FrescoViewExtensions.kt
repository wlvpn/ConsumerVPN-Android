package com.wlvpn.consumervpn.presentation.util

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * Is a prototype function that always tries to load the image from cache first
 * @param uri the uniform resource identify containing the image url address
 */
@Suppress("DEPRECATION")
fun SimpleDraweeView.setImageCacheStrategy(uri: Uri) {

    if (Fresco.getImagePipeline().isInBitmapMemoryCache(uri)) {
        // Forces cache strategy first
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.DISK_CACHE)
            .build()

        val draweeController = Fresco.newDraweeControllerBuilder()
            .setOldController(this.controller)
            .setImageRequest(imageRequest)
            .build()

        this.controller = draweeController
    } else {
        this.setImageURI(uri)
    }
}