LOCAL_PATH := $(call my-dir)

	include $(CLEAR_VARS)

#opencv
	OPENCVROOT:= D:\OPENCV\OpenCV-2.4.9-android-sdk
	OPENCV_CAMERA_MODULES:=on
	OPENCV_INSTALL_MODULES:=on
	OPENCV_LIB_TYPE:=SHARED
	include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk
	LOCAL_SRC_FILES = com_example_miskewolf_opencvradi_OpencvNativeClass.cpp
    LOCAL_SRC_FILES += FaceRecognitionLib/Eigenfaces.cpp
    LOCAL_SRC_FILES += FaceRecognitionLib/PCA.cpp
    LOCAL_SRC_FILES +=FaceRecognitionLib/LDA.cpp
    LOCAL_SRC_FILES +=FaceRecognitionLib/Facebase.cpp
	LOCAL_LDLIBS += -llog
	LOCAL_MODULE := MyFaceLibs


	include $(BUILD_SHARED_LIBRARY)