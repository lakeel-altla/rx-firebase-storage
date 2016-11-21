package com.lakeel.altla.vision.builder.presentation.view.renderer;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rajawali.pool.Pool.Holder;
import com.lakeel.altla.rajawali.pool.QuaternionPool;
import com.lakeel.altla.rajawali.pool.Vector3Pool;
import com.lakeel.altla.tango.rajawali.TangoCameraRenderer;
import com.lakeel.altla.vision.builder.presentation.graphics.BitmapPlaneFactory;
import com.lakeel.altla.vision.builder.presentation.graphics.XyzAxesBuilder;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.ObjectEditMode;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;

public final class MainRenderer extends TangoCameraRenderer implements OnObjectPickedListener {

    private static final Log LOG = LogFactory.getLog(MainRenderer.class);

    private static final float OBJECT_POSITION_ADJUSTMENT = 2f;

    private static final float TRANSLATE_OBJECT_DISTANCE_SCALE = 0.005f;

    private static final float ROTATE_OBJECT_ANGLE_SCALE = 1f;

    private static final float SCALE_OBJECT_SIZE_SCALE = 0.5f;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Queue<Bitmap> planeBitmapQueue = new LinkedList<>();

    private final BitmapPlaneFactory bitmapPlaneFactory = new BitmapPlaneFactory();

    private final Object modeLock = new Object();

    private ObjectColorPicker picker;

    private Line3D axes;

    private Object3D pickedObject;

    private OnPickedObjectChangedListener onPickedObjectChangedListener;

    private ObjectEditMode objectEditMode = ObjectEditMode.NONE;

    private Axis translateObjectAxis = Axis.X;

    private Axis rotateObjectAxis = Axis.Y;

    private float translateObjectDistance;

    private float rotateObjectAngle;

    private float scaleObjectSize;

    public MainRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initSceneOverride() {
        picker = new ObjectColorPicker(this);
        picker.setOnObjectPickedListener(this);

        // Build axes model indicating a pose of a picked object.
        axes = new XyzAxesBuilder().setThickness(5)
                                   .setLength(0.25f)
                                   .build();
        // Shift the axes model to a little camera beside.
        axes.setPosition(0, 0, -0.001f);
        axes.setVisible(false);
        getCurrentScene().addChild(axes);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        synchronized (planeBitmapQueue) {
            while (true) {
                // NOTE:
                //
                // Adding primitives into a scene in a non-OpenGL thread fail,
                // because its constructors access OpenGL vertex buffers etc.
                // So we queue a data indicating a primitive in a non-OpenGL,
                // Renderer#onRender invoked in OpenGL thread instantiates an actual primitive.
                //
                Bitmap bitmap = planeBitmapQueue.poll();
                if (bitmap == null) {
                    break;
                }

                Plane plane = bitmapPlaneFactory.create(bitmap);
                position(plane, getCurrentCamera(), getCurrentCameraForward());

                getCurrentScene().addChild(plane);
                picker.registerObject(plane);
            }
        }

        if (pickedObject != null) {
            if (objectEditMode == ObjectEditMode.TRANSLATE && translateObjectDistance != 0) {

                // Scale.
                float scaledDistance = translateObjectDistance * TRANSLATE_OBJECT_DISTANCE_SCALE;

                // Ensure the selected axis in this thread.
                Axis axis;
                synchronized (modeLock) {
                    axis = translateObjectAxis;
                }

                // Translate.
                translate(pickedObject, axis, scaledDistance);
                translate(axes, axis, scaledDistance);

                // Clear.
                translateObjectDistance = 0;
            } else if (objectEditMode == ObjectEditMode.ROTATE && rotateObjectAngle != 0) {

                // Scale.
                float scaledAngle = rotateObjectAngle * ROTATE_OBJECT_ANGLE_SCALE;

                // Ensure the selected axis in this thread.
                Axis axis;
                synchronized (modeLock) {
                    axis = rotateObjectAxis;
                }

                // Rotate.
                rotate(pickedObject, axis, scaledAngle);
                rotate(axes, axis, scaledAngle);

                // Clear.
                rotateObjectAngle = 0;
            } else if (objectEditMode == ObjectEditMode.SCALE && scaleObjectSize != 0) {

                // Scale the raw value.
                float scaledSize = scaleObjectSize * SCALE_OBJECT_SIZE_SCALE;

                // Scale the object.
                scale(pickedObject, scaledSize);

                // Clear.
                scaleObjectSize = 0;
            }
        }

        super.onRender(ellapsedRealtime, deltaTime);
    }

    private void position(Object3D object, Camera camera, Vector3 cameraForward) {
        try (Holder<Vector3> positionHolder = Vector3Pool.get();
             Holder<Vector3> translationHolder = Vector3Pool.get();
             Holder<Vector3> cameraBackwardHolder = Vector3Pool.get();
             Holder<Quaternion> orientationHolder = QuaternionPool.get()) {

            Vector3 position = positionHolder.get();
            Vector3 translation = translationHolder.get();
            Vector3 cameraBackward = cameraBackwardHolder.get();
            Quaternion orientation = orientationHolder.get();

            position.setAll(camera.getPosition());

            translation.setAll(cameraForward);
            translation.multiply(OBJECT_POSITION_ADJUSTMENT);

            position.add(translation);

            object.setPosition(position);

            cameraBackward.setAll(cameraForward);
            cameraBackward.inverse();

            orientation.lookAt(cameraBackward, Vector3.Y);

            object.setOrientation(orientation);
        }
    }

    private void translate(Object3D object, Axis axis, float distance) {
        try (Holder<Vector3> translationHolder = Vector3Pool.get();
             Holder<Quaternion> rotationHolder = QuaternionPool.get();
             Holder<Vector3> positionHolder = Vector3Pool.get()) {

            Vector3 translation = translationHolder.get();
            Quaternion rotation = rotationHolder.get();
            Vector3 position = positionHolder.get();

            switch (axis) {
                case X:
                    translation.setAll(Vector3.X);
                    break;
                case Y:
                    translation.setAll(Vector3.Y);
                    break;
                case Z:
                default:
                    translation.setAll(Vector3.Z);
                    break;
            }

            rotation.setAll(object.getOrientation());
            // Conjugate because rotateBy is wrong...
            rotation.conjugate();

            translation.rotateBy(rotation);
            translation.normalize();
            translation.multiply(distance);

            position.setAll(object.getPosition());
            position.add(translation);

            object.setPosition(position);
        }
    }

    private void rotate(Object3D object, Axis axis, float angle) {
        try (Holder<Vector3> baseAxisHolder = Vector3Pool.get();
             Holder<Vector3> modelAxisHolder = Vector3Pool.get();
             Holder<Quaternion> rotationHolder = QuaternionPool.get()) {

            Vector3 baseAxis = baseAxisHolder.get();
            Vector3 modelAxis = modelAxisHolder.get();
            Quaternion rotation = rotationHolder.get();

            switch (axis) {
                case X:
                    baseAxis.setAll(Vector3.X);
                    break;
                case Y:
                    baseAxis.setAll(Vector3.Y);
                    break;
                case Z:
                default:
                    baseAxis.setAll(Vector3.Z);
                    break;
            }

            modelAxis.setAll(baseAxis);
            rotation.setAll(object.getOrientation());
            // Conjugate because rotateBy is wrong...
            rotation.conjugate();
            modelAxis.rotateBy(rotation);

            object.rotate(modelAxis, angle);
        }
    }

    private void scale(Object3D object, float size) {
        final float MIN_RATIO = 0.1f;

        try (Holder<Vector3> scaleHolder = Vector3Pool.get()) {
            Vector3 scale = scaleHolder.get();

            float ratio;
            if (0 < size) {
                ratio = 1 + size * 0.01f;
            } else {
                ratio = Math.max(1 - Math.abs(size) * 0.01f, MIN_RATIO);
            }

            scale.setAll(object.getScale());
            scale.multiply(ratio);

            object.setScale(scale);
        }
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        LOG.d("onObjectPicked: %s", object.getName());

        if (pickedObject == object) {
            // Unpick.
            changePickedObject(null);
        } else {
            // Pick.
            changePickedObject(object);
        }
    }

    @Override
    public void onNoObjectPicked() {
        LOG.d("onNoObjectPicked");

        // Unpick
        changePickedObject(null);
    }

    private void changePickedObject(Object3D object) {
        String oldName = null;
        if (pickedObject != null) {
            // Unpick
            oldName = pickedObject.getName();
        }

        String newName = null;
        if (object != null) {
            newName = object.getName();
        }

        pickedObject = object;

        if (pickedObject != null) {
            // The axes model uses the same pose with the picked object.
            axes.setPosition(pickedObject.getPosition().clone());
            axes.setOrientation(pickedObject.getOrientation().clone());
            axes.setVisible(true);
        } else {
            axes.setVisible(false);
        }

        raiseOnPickedObjectChanged(oldName, newName);
    }

    // Non-threa-safe.
    public void setOnPickedObjectChangedListener(OnPickedObjectChangedListener onPickedObjectChangedListener) {
        this.onPickedObjectChangedListener = onPickedObjectChangedListener;
    }

    private void raiseOnPickedObjectChanged(String oldName, String newName) {
        if (onPickedObjectChangedListener != null) {
            mainHandler.post(() -> onPickedObjectChangedListener.onPickedObjectChanged(oldName, newName));
        }
    }

    public void addPlaneBitmap(@NonNull Bitmap bitmap) {
        synchronized (planeBitmapQueue) {
            planeBitmapQueue.offer(bitmap);
        }
    }

    public void tryPickObject(float x, float y) {
        LOG.d("tryPickObject: x = %f, y = %f", x, y);
        picker.getObjectAt(x, y);
    }

    public void setObjectEditMode(ObjectEditMode mode) {
        LOG.d("setObjectEditMode: mode = %s", mode);
        objectEditMode = mode;
    }

    public void setTranslateObjectAxis(Axis axis) {
        LOG.d("setTranslateObjectAxis: axis = %s", axis);
        synchronized (modeLock) {
            translateObjectAxis = axis;
        }
    }

    public void setTranslateObjectDistance(float distance) {
        LOG.d("setTranslateObjectDistance: distance = %f", distance);
        translateObjectDistance = distance;
    }

    public void setRotateObjectAxis(Axis axis) {
        LOG.d("setRotateObjectAxis: axis = %s", axis);
        synchronized (modeLock) {
            rotateObjectAxis = axis;
        }
    }

    public void setRotateObjectAngle(float angle) {
        LOG.d("setRotateObjectAngle: angle = %f", angle);
        rotateObjectAngle = angle;
    }

    public void setScaleObjectSize(float size) {
        LOG.d("setScaleObjectSize: size = %f", size);
        scaleObjectSize = size;
    }

    public interface OnPickedObjectChangedListener {

        void onPickedObjectChanged(String oldName, String newName);
    }
}
