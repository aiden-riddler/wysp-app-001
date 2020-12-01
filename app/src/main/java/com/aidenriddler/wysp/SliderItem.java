package com.aidenriddler.wysp;

import android.net.Uri;

public class SliderItem {
    private Uri imageUri;

    public SliderItem() {
    }

    public SliderItem(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
