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

    private int segmentsW;

    private int segmentsH;

    private int numTextureTiles;

    private boolean createTextureCoords;

    private boolean createVertexColorBuffer;

    private Camera2D camera;

    private Matrix4 viewProjection;

    private EffectPass effectPass;

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
        this.segmentsW = segmentsW;
        this.segmentsH = segmentsH;
        createTextureCoords = createTextureCoordinates;
        this.createVertexColorBuffer = createVertexColorBuffer;
        this.numTextureTiles = numTextureTiles;
        init(createVBOs);
    }

    private void init(boolean createVBOs) {
        int i, j;
        int numVertices = (segmentsW + 1) * (segmentsH + 1);
        float[] vertices = new float[numVertices * 3];
        float[] textureCoords = null;
        if (createTextureCoords) {
            textureCoords = new float[numVertices * 2];
        }
        float[] normals = new float[numVertices * 3];
        float[] colors = null;
        if (createVertexColorBuffer) {
            colors = new float[numVertices * 4];
        }
        int[] indices = new int[segmentsW * segmentsH * 6];
        int vertexCount = 0;
        int texCoordCount = 0;

        camera = new Camera2D();
        camera.setProjectionMatrix(0, 0);
        viewProjection = new Matrix4();

        for (i = 0; i <= segmentsW; i++) {
            for (j = 0; j <= segmentsH; j++) {
                float v1 = ((float) i / (float) segmentsW - 0.5f);
                float v2 = ((float) j / (float) segmentsH - 0.5f);
                vertices[vertexCount] = v1;
                vertices[vertexCount + 1] = v2;
                vertices[vertexCount + 2] = 0;

                if (createTextureCoords) {
                    // [FIX]
                    // Fixed texture coodinations in original rajawali codes.
                    float u = (float) i / (float) segmentsW;
                    textureCoords[texCoordCount++] = u * numTextureTiles;
                    float v = (float) j / (float) segmentsH;
                    textureCoords[texCoordCount++] = (1.0f - v) * numTextureTiles;
                }

                normals[vertexCount] = 0;
                normals[vertexCount + 1] = 0;
                normals[vertexCount + 2] = 1;

                vertexCount += 3;
            }
        }

        int colspan = segmentsH + 1;
        int indexCount = 0;

        for (int col = 0; col < segmentsW; col++) {
            for (int row = 0; row < segmentsH; row++) {
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

        if (createVertexColorBuffer) {
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
        final Matrix4 pMatrix = this.camera.getProjectionMatrix();
        final Matrix4 viewMatrix = this.camera.getViewMatrix();
        viewProjection.setAll(pMatrix).multiply(viewMatrix);
        super.render(this.camera, viewProjection, projMatrix, viewMatrix, null, sceneMaterial);
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
        if (effectPass != null) {
            effectPass.setShaderParams();
        }
    }
}
