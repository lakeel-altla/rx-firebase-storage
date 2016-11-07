package com.lakeel.altla.tango.rajawali;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoPoseData;

import com.projecttango.tangosupport.TangoSupport;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.ASceneFrameCallback;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.opengles.GL10;

public abstract class TangoCameraRenderer extends Renderer {

    private static final String TAG = TangoCameraRenderer.class.getSimpleName();

    private static final String TEXTURE_NAME = "tangoCamera";

    private static final int INVALID_CAMERA_ID = -1;

    private static final int INVALID_TEXTURE_ID = 0;

    private final AtomicBoolean mIsFrameAvailableTangoThread = new AtomicBoolean(false);

    private final CameraTransformer mCameraTransformer = new CameraTransformer();

    private final Vector3 mCurrentCameraForward = new Vector3();

    private StreamingTexture mTexture;

    private Tango mTango;

    private int mCameraId = INVALID_CAMERA_ID;

    private TangoCameraIntrinsics mIntrinsics;

    private boolean mSceneCameraConfigured;

    private int mConnectedTextureId = INVALID_TEXTURE_ID;

    private double mUpdateTextureTimestamp;

    private double mUpdateCameraPoseTimestamp;

    public TangoCameraRenderer(Context context) {
        super(context);
    }

    @Override
    protected final void initScene() {
        // Fullscreen quad.
        ScreenQuad screenQuad = new ScreenQuad();

        Material material = new Material();
        material.setColorInfluence(0);

        mTexture = new StreamingTexture(TEXTURE_NAME, (StreamingTexture.ISurfaceListener) null);
        try {
            material.addTexture(mTexture);
            screenQuad.setMaterial(material);
            getCurrentScene().addChild(screenQuad);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Can not add a texture: name = " + TEXTURE_NAME, e);
        }

        initSceneOverride();
    }

    protected abstract void initSceneOverride();

    /**
     * We need to override this method to mark the camera for re-configuration (set proper
     * projection matrix) since it will be reset by Rajawali on surface changes.
     */
    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mSceneCameraConfigured = false;
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                 int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    public void connectToTangoCamera(Tango tango) {
        mTango = tango;
        mCameraId = TangoCameraIntrinsics.TANGO_CAMERA_COLOR;
        mIntrinsics = mTango.getCameraIntrinsics(mCameraId);
        getCurrentScene().registerFrameCallback(new SceneFrameCallback());
    }

    public void disconnectFromTangoCamera() {
        if (mTango != null && mCameraId != INVALID_CAMERA_ID) {
            synchronized (this) {
                getCurrentScene().clearFrameCallbacks();
                mTango.disconnectCamera(mCameraId);
                mIsFrameAvailableTangoThread.set(false);
                mCameraId = INVALID_CAMERA_ID;
                mConnectedTextureId = INVALID_TEXTURE_ID;
            }
        }
    }

    public void onFrameAvailable() {
        mIsFrameAvailableTangoThread.set(true);
    }

    protected TangoSupport.TangoMatrixTransformData getCameraMatrixTransformAtTime(double timestamp) {
        // Get the color camera's pose at the specified time when the camera frame is updated.
        // Tango examples for Java use TangoSupport#getPoseAtTime, but we use TangoSupport#getMatrixTransformAtTime
        // according to Tango examples for C.
        // I think that TangoSupport#getPoseAtTime may return a wrong rotation values.
        return TangoSupport.getMatrixTransformAtTime(timestamp,
                                                     TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                                                     TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR,
                                                     TangoSupport.TANGO_SUPPORT_ENGINE_OPENGL,
                                                     TangoSupport.TANGO_SUPPORT_ENGINE_OPENGL);
    }

    /**
     * Updates the pose of the current camera with the specified transform matrix.
     *
     * @param cameraTransform The matrix to transform the current camera.
     */
    protected void updateCameraPose(TangoSupport.TangoMatrixTransformData cameraTransform) {
        mCameraTransformer.transform(getCurrentCamera(), cameraTransform.matrix, mCurrentCameraForward);
        mUpdateCameraPoseTimestamp = cameraTransform.timestamp;
    }

    protected final Vector3 getCurrentCameraForward() {
        return mCurrentCameraForward;
    }

    public synchronized double getUpdateTextureTimestamp() {
        return mUpdateTextureTimestamp;
    }

    /**
     * Calculate the projection matrix from Tango camera intrinsics.
     *
     * @param intrinsics Tango camera intrinsics.
     * @param result     The matrix that holds the result.
     */
    private static void projection(TangoCameraIntrinsics intrinsics, Matrix4 result) {
        // Uses frustumM to create a projection matrix taking into account calibrated camera
        // intrinsic parameter.
        // Reference: http://ksimek.github.io/2013/06/03/calibrated_cameras_in_opengl/
        float near = 0.1f;
        float far = 100;

        float xScale = near / (float) intrinsics.fx;
        float yScale = near / (float) intrinsics.fy;
        float xOffset = (float) (intrinsics.cx - (intrinsics.width / 2.0)) * xScale;
        // Color camera's coordinates has y pointing downwards so we negate this term.
        float yOffset = (float) -(intrinsics.cy - (intrinsics.height / 2.0)) * yScale;

        Matrix.frustumM(result.getDoubleValues(), 0,
                        xScale * (float) -intrinsics.width / 2.0f - xOffset,
                        xScale * (float) intrinsics.width / 2.0f - xOffset,
                        yScale * (float) -intrinsics.height / 2.0f - yOffset,
                        yScale * (float) intrinsics.height / 2.0f - yOffset,
                        near, far);
    }

    private class SceneFrameCallback extends ASceneFrameCallback {

        private final Matrix4 mProjection = new Matrix4();

        @Override
        public void onPreFrame(long sceneTime, double deltaTime) {
            try {
                synchronized (TangoCameraRenderer.this) {
                    if (!mSceneCameraConfigured) {
                        projection(mIntrinsics, mProjection);
                        getCurrentCamera().setProjectionMatrix(mProjection);
                        mSceneCameraConfigured = true;
                    }

                    // NOTE by Tango Samples:
                    //
                    // NOTE: When the OpenGL context is recycled, Rajawali may re-generate the
                    // texture with a different ID.
                    int textureId = mTexture.getTextureId();
                    if (mConnectedTextureId != textureId) {
                        mTango.connectTextureId(mCameraId, textureId);
                        mConnectedTextureId = textureId;
                    }

                    if (mIsFrameAvailableTangoThread.compareAndSet(true, false)) {
                        mUpdateTextureTimestamp = mTango.updateTexture(mCameraId);
                    }

                    if (mUpdateTextureTimestamp > mUpdateCameraPoseTimestamp) {
                        TangoSupport.TangoMatrixTransformData transformData =
                                getCameraMatrixTransformAtTime(mUpdateTextureTimestamp);
                        if (transformData.statusCode == TangoPoseData.POSE_VALID) {
                            updateCameraPose(transformData);
                        } else {
                            Log.v(TAG, "Can't get a valid camera pose at time: " + mUpdateTextureTimestamp);
                        }
                    }
                }
            } catch (TangoErrorException e) {
                Log.e(TAG, "Tango error on the OpenGL thread", e);
            } catch (Throwable t) {
                Log.e(TAG, "Error on the OpenGL thread", t);
            }
        }

        @Override
        public void onPreDraw(long sceneTime, double deltaTime) {
        }

        @Override
        public void onPostFrame(long sceneTime, double deltaTime) {
        }

        @Override
        public boolean callPreFrame() {
            // Enable calling onPreFrame().
            return true;
        }
    }

    private static class CameraTransformer {

        final Matrix4 mTransform = new Matrix4();

        final Vector3 mPosition = new Vector3();

        final Quaternion mOrientation = new Quaternion();

        final Quaternion mConugateOrientation = new Quaternion();

        void transform(Camera camera, float[] matrix, Vector3 cameraForward) {
            // Convert arrays to a Matrix4 instance.
            mTransform.setAll(matrix);
            // Get the position from the matrix.
            mTransform.getTranslation(mPosition);
            // Get the orientation from the matrix.
            mOrientation.fromMatrix(mTransform);

            // Update the specified camera pose.
            camera.setPosition(mPosition);
            camera.setOrientation(mOrientation);

            // Update the forward vector of the current camera.
            //
            // NOTE:
            //
            // Rajawali and OpenGL are based on the right-handed one, but Quaternion#multiply(Vector3) used in
            // Vector3#rotateBy(Quaternion) may be the left-handed coodinate system, I think.
            // so we call Vector3#rotateBy with the conjugate of an orientation.
            cameraForward.setAll(0, 0, -1);

            mConugateOrientation.setAll(mOrientation);
            mConugateOrientation.conjugate();

            cameraForward.rotateBy(mConugateOrientation);
            cameraForward.normalize();
        }
    }
}
