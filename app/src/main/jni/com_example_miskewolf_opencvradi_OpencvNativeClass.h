/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include "opencv2/opencv.hpp"
#include <android/log.h>
#include <string>
#include "opencv2/core/core.hpp"
#include "opencv2/contrib/contrib.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <vector>
#include <numeric>
#include <Eigen/Dense> // http://eigen.tuxfamily.org
#include <opencv2/core/eigen.hpp>
#include <iostream>
#include <FaceRecognitionLib/Eigenfaces.h>
#include <FaceRecognitionLib/Tools.h>
#include <stdio.h>
/* Header for class com_example_miskewolf_opencvradi_OpencvNativeClass */
using namespace std;
using namespace cv;
using namespace Eigen;
#ifdef NDEBUG
#define LOGD(...) ((void)0)
#define LOGI(...) ((void)0)
#define LOGE(...) ((void)0)
#define LOG_ASSERT(condition, ...) ((void)0)
#else
#define LOG_TAG "FaceRecognitionAppActivity/Native"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define LOG_ASSERT(condition, ...) if (!(condition)) __android_log_assert(#condition, LOG_TAG, __VA_ARGS__)
#endif
#ifndef _Included_com_example_miskewolf_opencvradi_OpencvNativeClass
#define _Included_com_example_miskewolf_opencvradi_OpencvNativeClass
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_miskewolf_opencvradi_OpencvNativeClass
 * Method:    faceDetection
 * Signature: (J)V
 */
 void detect(Mat& frame);
Mat pixelize(Mat src,int width,int height);
Mat FindDiff(Mat img1,Mat img2);
Mat SepImages1(Mat result);
int gcd(int a, int b);
Mat transparent(Mat image);
void StrangeMaske(Mat& frame,Mat& photos,jfloat alfa,jfloat beta);
Mat FillImage(Mat ImageYouWantToFill);
Mat SepImages(Mat result,int num);
bool truth(Mat img1,Mat img2);
Mat SepImages(Mat result,int num);
JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_faceDetection
  (JNIEnv *, jclass, jlong);
  JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_RemoveBackground
    (JNIEnv *, jclass, jlong);
  JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_StrangeMask
  (JNIEnv *, jclass, jlong,jlong,jfloat,jfloat);
  JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_detectHuman
    (JNIEnv * env, jclass, jlongArray addrRgba);
      JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_DeleteDiff
        (JNIEnv * env, jclass, jlongArray addrRgba);
              JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_DeleteDiffInverse
                (JNIEnv * env, jclass, jlongArray addrRgba);
        JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_mergeAll
        (JNIEnv * env, jclass, jlongArray addrRgba);
         JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_FaceRecognition
          (JNIEnv * env, jclass, jlong);
  JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_TransparentDiff
  (JNIEnv * env, jclass, jlongArray addrRgba);
   JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_TrainFaces(JNIEnv, jobject, jlong addrImages, jlong addrClasses);
   JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_MeasureDist(JNIEnv *env, jobject, jlong addrImage, jfloatArray minDist, jintArray minDistIndex, jfloatArray faceDist, jboolean useEigenfaces);
JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_Grayscale
    (JNIEnv * env, jclass, jlongArray addrRgba);

#ifdef __cplusplus
}
#endif
#endif