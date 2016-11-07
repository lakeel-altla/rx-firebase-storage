package com.lakeel.altla.tango;

import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;

import com.projecttango.tangosupport.TangoPointCloudManager;
import com.projecttango.tangosupport.TangoSupport;

import android.opengl.Matrix;
import android.util.Log;

/**
 * The utility class for the point cloud.
 */
public final class PointCloud implements OnPointCloudAvailableListener {

    private static final String TAG = PointCloud.class.getSimpleName();

    private final TangoPointCloudManager mPointCloudManager = new TangoPointCloudManager();

    @Override
    public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
        mPointCloudManager.updatePointCloud(pointCloud);
    }

    public Plane findPlane(double timestamp, float u, float v) {
        TangoPointCloudData pointCloud = mPointCloudManager.getLatestPointCloud();
        if (pointCloud == null) {
            return null;
        }

        // Calculate the tranform between the color camera at the time you want the plane
        // and the depth camera at the time the point cloud was acquired.
        TangoPoseData colorTdepthPose = TangoSupport.calculateRelativePose(
                timestamp,
                TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR,
                pointCloud.timestamp,
                TangoPoseData.COORDINATE_FRAME_CAMERA_DEPTH);

        // Detect the plane at the latest point cloud.
        TangoSupport.IntersectionPointPlaneModelPair intersectionPointPlaneModelPair =
                TangoSupport.fitPlaneModelNearPoint(pointCloud, colorTdepthPose, u, v);

        // Get the transform from the depth camera to the OpenGL world at the latest point cloud.
        TangoSupport.TangoMatrixTransformData transform = TangoSupport.getMatrixTransformAtTime(
                pointCloud.timestamp,
                TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                TangoPoseData.COORDINATE_FRAME_CAMERA_DEPTH,
                TangoSupport.TANGO_SUPPORT_ENGINE_OPENGL,
                TangoSupport.TANGO_SUPPORT_ENGINE_TANGO);

        if (transform.statusCode == TangoPoseData.POSE_VALID) {
            return calculateOpenGlPlane(intersectionPointPlaneModelPair, transform.matrix);
        } else {
            Log.v(TAG, "Can not get depth camera transform at time " + pointCloud.timestamp);
            return null;
        }
    }

    private Plane calculateOpenGlPlane(TangoSupport.IntersectionPointPlaneModelPair intersection,
                                       float[] openGlTdepth) {
        // NOTE: Homogeneous Coodinate
        //
        // http://www.opengl-tutorial.org/jp/beginners-tutorials/tutorial-3-matrices/
        //
        // if a vector indicates a position: w = 1.
        // if a vector indicates a direction: w = 0.
        //

        // NOTE: Column-major
        //
        // http://puarts.com/?pid=1229
        //
        // OpenGL uses column-major: Matrix4x4 * Vector4.
        // DirectX uses row-major: Vector4 * Matrix4x4
        //

        // The position of the plane at the tango depth coodinate.
        float[] depthPoint = {
                (float) intersection.intersectionPoint[0],
                (float) intersection.intersectionPoint[1],
                (float) intersection.intersectionPoint[2],
                1
        };

        // The normal vector of the plane at the tango depth coodinate.
        // Values from plane[0] to plane[2] are N and plane[3] is D of the plane.
        float[] depthNormal = {
                (float) intersection.planeModel[0],
                (float) intersection.planeModel[1],
                (float) intersection.planeModel[2],
                0
        };

        // The position of the plane at the OpenGL coodinate.
        float[] openGlPoint = new float[4];
        Matrix.multiplyMV(openGlPoint, 0, openGlTdepth, 0, depthPoint, 0);

        // The normal vector of the plane at the OpenGL coodinate.
        float[] openGlNormal = new float[4];
        Matrix.multiplyMV(openGlNormal, 0, openGlTdepth, 0, depthNormal, 0);

        return new Plane(new float[] { openGlPoint[0], openGlPoint[1], openGlPoint[2] },
                         new float[] { openGlNormal[0], openGlNormal[1], openGlNormal[2] },
                         (float) intersection.planeModel[3]);
    }

    public class Plane {

        public final float[] center;

        public final float[] normal;

        public final float distance;

        private Plane(float[] center, float[] normal, float distance) {
            this.center = center;
            this.normal = normal;
            this.distance = distance;
        }
    }
}
