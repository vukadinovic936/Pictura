package com.example.miskewolf.opencvradi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.opencv.core.Mat;
// required knowlegde for JavaCameraView & OpenCV & usage of OpenCV gained here https://www.youtube.com/channel/UCvRTnofGf_Svg2Q-94KUm8g/videos
// resources from which I learned about Eigenfaces --> https://docs.opencv.org/2.4/modules/contrib/doc/facerec/facerec_tutorial.html , https://www.youtube.com/watch?v=B3XHEkLxjJE, https://www.youtube.com/watch?v=WfrHM24aFIE
/**
 * Created by MiskeWolf on 12/9/2017.
 */


public class OpencvNativeClass {
    private static final String TAG = Recognition.class.getSimpleName() + "/" + OpencvNativeClass.class.getSimpleName();

    static void loadNativeLibraries() {
        System.loadLibrary("MyFaceLibs");
    }

    static class TrainFacesTask extends AsyncTask<Void, Void, Boolean> {
        private final Mat images, classes;
        private final Callback callback;
        private Exception error;

        interface Callback {
            void onTrainFacesComplete(boolean result);
        }

        TrainFacesTask(Mat images, Callback callback) {
            this(images, null, callback);
        }

        TrainFacesTask(Mat images, Mat classes, Callback callback) {
            this.images = images;
            this.classes = classes;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (classes == null) {
                    TrainFaces(images.getNativeObjAddr(), 0);
                } else {
                    TrainFaces(images.getNativeObjAddr(), classes.getNativeObjAddr());
                }
                return true;
            } catch (Exception e) {
                error = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            callback.onTrainFacesComplete(result);
            if (result)
                Log.i(TAG, "Done training images");
            else
                Log.e(TAG, error.getMessage());
        }
    }

    static class MeasureDistTask extends AsyncTask<Mat, Void, Bundle> {
        static final String MIN_DIST_FLOAT = "minDist";
        static final String MIN_DIST_INDEX_INT = "minDistIndex";
        static final String DIST_FACE_FLOAT = "distFace";

        private final Callback callback;
        private final boolean useEigenfaces;
        private Exception error;

        interface Callback {
            void onMeasureDistComplete(Bundle bundle);
        }

        MeasureDistTask(boolean useEigenfaces, Callback callback) {
            this.useEigenfaces = useEigenfaces;
            this.callback = callback;
        }

        @Override
        protected Bundle doInBackground(Mat... mat) {
            float[] minDist = new float[] { -1 };
            int[] minDistIndex = new int[1];
            float[] faceDist = new float[1];
            try {
                MeasureDist(mat[0].getNativeObjAddr(), minDist, minDistIndex, faceDist, useEigenfaces);
                Log.d("ERORRR","AAAA");
            } catch (Exception e) {
                error = e;
                Log.d("ERORRR","EEEEE");
                return null;
            }
            Bundle bundle = new Bundle();
            bundle.putFloat(MIN_DIST_FLOAT, minDist[0]);
            bundle.putInt(MIN_DIST_INDEX_INT, minDistIndex[0]);
            bundle.putFloat(DIST_FACE_FLOAT, faceDist[0]);
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            callback.onMeasureDistComplete(bundle);
            if (bundle != null)
                Log.i(TAG, "Done measuring distance");
            else
                Log.e(TAG, error.getMessage());
        }
    }
    private static native void TrainFaces(long addrImages, long addrClasses);
    private static native void MeasureDist(long addrImage, float[] minDist, int[] minDistIndex, float[] faceDist, boolean useEigenfaces);
    public native static void faceDetection(long addrRgba);
    public native static void StrangeMask(long addrRgba,long addrRgba1,float alfa,float beta);
    public native static void detectHuman(long[] addrRgba);
    public native static void TransparentDiff(long[] addrRgba);
    public native static void mergeAll(long[] addrRgba);
    public native static void RemoveBackground(long addrRgba);
    public native static void FaceRecognition(long addrRgba);
    public native static void DeleteDiff(long[] addrRgba);
    public native static void DeleteDiffInverse(long[] addrRgba);
    public native static void Grayscale(long addrRgba);

}
