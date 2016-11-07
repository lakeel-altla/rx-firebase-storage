package com.lakeel.altla.tango.rajawali;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.cameras.Camera2D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.postprocessing.passes.EffectPass;

/**
 * The fullscreen quad class used to render the camera preview.
 *
 * We think that texture coodinates in ScreenQuad class of Rajawali are wrong,
 * and the rendering code for color picking is wrong too,
 * so redefine it.
 */
final class ScreenQuad extends Object3D {

    private int mSegmentsW;

    private int mSegmentsH;

    private int mNumTextureTiles;

    private boolean mCreateTextureCoords;

    private boolean mCreateVertexColorBuffer;

    private Camera2D mCamera;

    private Matrix4 mVPMatrix;

    private EffectPass mEffectPass;

    public ScreenQuad() {
        this(1, 1, true, false, 1, true);
    }

    /**
     * Creates a ScreenQuad.
     *
     * @param segmentsW                The number of vertical segments
     * @param segmentsH                The number of horizontal segments
     * @param createTextureCoordinates A boolean that indicates whether the texture coordinates should be calculated or
     *                                 not.
     * @param createVertexColorBuffer  A boolean that indicates whether a vertex color buffer should be created or not.
     * @param numTextureTiles          The number of texture tiles. If more than 1 the texture will be repeat by n
     *                                 times.
     * @param createVBOs               A boolean that indicates whether the VBOs should be created immediately.
     */
    private ScreenQuad(int segmentsW, int segmentsH, boolean createTextureCoordinates,
                       boolean createVertexColorBuffer, int numTextureTiles, boolean createVBOs) {
        super();
        mSegmentsW = segmentsW;
        mSegmentsH = segmentsH;
        mCreateTextureCoords = createTextureCoordinates;
        mCreateVertexColorBuffer = createVertexColorBuffer;
        mNumTextureTiles = numTextureTiles;
        init(createVBOs);
    }

    private void init(boolean createVBOs) {
        int i, j;
        int numVertices = (mSegmentsW + 1) * (mSegmentsH + 1);
        float[] vertices = new float[numVertices * 3];
        float[] textureCoords = null;
        if (mCreateTextureCoords) {
            textureCoords = new float[numVertices * 2];
        }
        float[] normals = new float[numVertices * 3];
        float[] colors = null;
        if (mCreateVertexColorBuffer) {
            colors = new float[numVertices * 4];
        }
        int[] indices = new int[mSegmentsW * mSegmentsH * 6];
        int vertexCount = 0;
        int texCoordCount = 0;

        mCamera = new Camera2D();
        mCamera.setProjectionMatrix(0, 0);
        mVPMatrix = new Matrix4();

        for (i = 0; i <= mSegmentsW; i++) {
            for (j = 0; j <= mSegmentsH; j++) {
                float v1 = ((float) i / (float) mSegmentsW - 0.5f);
                float v2 = ((float) j / (float) mSegmentsH - 0.5f);
                vertices[vertexCount] = v1;
                vertices[vertexCount + 1] = v2;
                vertices[vertexCount + 2] = 0;

                if (mCreateTextureCoords) {
                    // [FIX]
                    // Fixed texture coodinations in original rajawali codes.
                    float u = (float) i / (float) mSegmentsW;
                    textureCoords[texCoordCount++] = u * mNumTextureTiles;
                    float v = (float) j / (float) mSegmentsH;
                    textureCoords[texCoordCount++] = (1.0f - v) * mNumTextureTiles;
                }

                normals[vertexCount] = 0;
                normals[vertexCount + 1] = 0;
                normals[vertexCount + 2] = 1;

                vertexCount += 3;
            }
        }

        int colspan = mSegmentsH + 1;
        int indexCount = 0;

        for (int col = 0; col < mSegmentsW; col++) {
            for (int row = 0; row < mSegmentsH; row++) {
                int ul = col * colspan + row;
                int ll = ul + 1;
                int ur = (col + 1) * colspan + row;
                int lr = ur + 1;

                indices[indexCount++] = ur;
                indices[indexCount++] = lr;
                indices[indexCount++] = ul;

                indices[indexCount++] = lr;
                indices[indexCount++] = ll;
                indices[indexCount++] = ul;
            }
        }

        if (mCreateVertexColorBuffer) {
            int numColors = numVertices * 4;
            for (j = 0; j < numColors; j += 4) {
                colors[j] = 1.0f;
                colors[j + 1] = 1.0f;
                colors[j + 2] = 1.0f;
                colors[j + 3] = 1.0f;
            }
        }

        setData(vertices, normals, textureCoords, colors, indices, createVBOs);

        mEnableDepthTest = false;
        mEnableDepthMask = false;
    }

    public void render(Camera camera, final Matrix4 vpMatrix, final Matrix4 projMatrix,
                       final Matrix4 vMatrix, final Matrix4 parentMatrix, Material sceneMaterial) {
        final Matrix4 pMatrix = mCamera.getProjectionMatrix();
        final Matrix4 viewMatrix = mCamera.getViewMatrix();
        mVPMatrix.setAll(pMatrix).multiply(viewMatrix);
        super.render(mCamera, mVPMatrix, projMatrix, viewMatrix, null, sceneMaterial);
    }

    @Override
    public void renderColorPicking(Camera camera, Material pickingMaterial) {
        // Do not render anything.

        // NOTE:
        //
        // In Rajawali, the rendering code for color picking is wrong.
        // It always overrides a whole render target with a fullscreen quad,
        // because it ignores mEnableDepthTest and mEnableDepthMask of Object3D.
        // Now, we want to use this screen quad as a fullscreen quad,
        // so override this method that does not render anything.
    }

    @Override
    protected void setShaderParams(Camera camera) {
        super.setShaderParams(camera);
        if (mEffectPass != null) {
            mEffectPass.setShaderParams();
        }
    }
}
