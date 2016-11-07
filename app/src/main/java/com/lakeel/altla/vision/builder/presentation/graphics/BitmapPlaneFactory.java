package com.lakeel.altla.vision.builder.presentation.graphics;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.primitives.Plane;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public final class BitmapPlaneFactory {

    private static final Log LOG = LogFactory.getLog(BitmapPlaneFactory.class);

    private static final String TEXTURE_NAME = "texture";

    private static final String NAME_PREFIX = "BitmapPlane_";

    private int mObjectCounter;

    public Plane create(@NonNull Bitmap bitmap) {
        // NOTE:
        // The argument 'textureName' is used as a variable in a fragment shader.
        Texture texture = new Texture(TEXTURE_NAME, bitmap);

        Material material = new Material();
        material.setColorInfluence(0);
        try {
            material.addTexture(texture);
        } catch (ATexture.TextureException e) {
            LOG.e("Can not add a texture.", e);
        }

        // Define 512 pixels as unit 1 in the world space.
        float w = bitmap.getWidth() / 512f;
        float h = bitmap.getHeight() / 512f;

        Plane plane = new Plane(w, h, 1, 1);
        plane.setMaterial(material);
        // Enable transparent images.
        plane.setTransparent(true);
        // Enable the back face rendering to understand how models rotate.
        plane.setDoubleSided(true);

        mObjectCounter++;
        plane.setName(NAME_PREFIX + mObjectCounter);

        return plane;
    }
}
