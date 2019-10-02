#include "com_example_miskewolf_opencvradi_OpencvNativeClass.h"
Eigenfaces eigenfaces;
 int save=0;
 JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_mergeAll
     (JNIEnv * env, jclass, jlongArray addrRgba){
       vector<Mat> trainimgs;

             jsize a_len = env-> GetArrayLength(addrRgba);
             jlong *traindata = env-> GetLongArrayElements(addrRgba,0);
             for(int k=0;k<a_len;k++)
             {
                 Mat& newimage=*(Mat*)traindata[k];
                 trainimgs.push_back(newimage);
             }
             env->ReleaseLongArrayElements(addrRgba,traindata,0);
             //TODO PRVO ODUZMI SVE RAZLIKE SA BACKGROUND I NAPRAVI NEKI TEMPLATE :D
                try{
                   for(int y=1;y<trainimgs.size();y++){
                      Mat backgroundImage = (Mat) trainimgs[0];
                      Mat currentImage = (Mat) trainimgs[y];
                      Mat HSV_currentImage;
                      Mat HSVbackgroundImagebg;
                      Mat diffImage;
                      Mat diffImage2;
                      cvtColor(backgroundImage, HSVbackgroundImagebg, CV_BGR2HSV);
                      cv::cvtColor(currentImage, HSV_currentImage, CV_BGR2HSV);
                      absdiff(HSVbackgroundImagebg, HSV_currentImage, diffImage);
                       Mat mask = FillImage(diffImage);

                       Mat src;
                       bitwise_and(currentImage, currentImage, src, mask);
                       Mat fil;
                       try{
                        Size size1(src.cols/50,src.rows/50);
                        src.convertTo(src, -1, 1.1, 0);
                        resize(src, src, size1, 1, 1, INTER_NEAREST);
                        fil=FillImage(SepImages(src,1));
                         }catch(...){}__android_log_print(ANDROID_LOG_INFO, "sometag", "save %d",save);
                         if(save!=0){
                 for(int i=save-1;i>0;i--){

                 int tru=0;
                 int fols=0;
                 Mat ses1;
                 Mat ses2;
                 Mat ses3;
                 try{
                 Size size0(src.cols,src.rows);
                 Size size1(src.cols/10 , src.rows/10);
                 src.convertTo(src, -1, 1.1, 0);
                 resize(src, src, size1, 1, 1, INTER_NEAREST);
                 fil=FillImage(SepImages(src,i));
                 resize(src, src, size0, 1, 1, INTER_NEAREST);
                 bitwise_and(backgroundImage, backgroundImage, ses1,fil);
                 bitwise_and(currentImage, currentImage, ses2, fil);
                 for(int i=1;i<trainimgs.size();i++){
                 if(i!=y){
                 bitwise_and(trainimgs[i],trainimgs[i] , ses3, fil);
                 if(!truth(ses2,ses3)){
                            }else{
                                fols++;
                            }
                   }
                 }
                 if(tru>fols){
                    trainimgs[0]=trainimgs[0]-ses1+ses2;
                    __android_log_print(ANDROID_LOG_INFO, "sometag", "TRUE");
                  }else{
                        __android_log_print(ANDROID_LOG_INFO, "sometag", "FALSE");
                  }
                   }catch(...){__android_log_print(ANDROID_LOG_INFO, "sometag", "ne postoji %d",i);}
                   }
                  }else{
                  y=trainimgs.size();
                  }

           }
           }catch(...){}
          }
            JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_RemoveBackground
              (JNIEnv *, jclass, jlong addrRgba){
               Mat& frame = *(Mat*) addrRgba;
               frame=transparent(frame);

              }
              JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_Grayscale
                  (JNIEnv * env, jclass, jlongArray addrRgba){
                       Mat& photo= *(Mat*) addrRgba;
                       cvtColor(photo,photo,CV_BGR2GRAY);
                  }
JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_StrangeMask
  (JNIEnv *, jclass, jlong addrRgba,jlong addrRgba1 , jfloat alfa , jfloat beta){
    Mat& frame = *(Mat*) addrRgba;
        Mat& photo= *(Mat*) addrRgba1;
        StrangeMaske(frame,photo,alfa,beta);
  }
JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_faceDetection
  (JNIEnv *, jclass, jlong addrRgba){
    Mat& frame = *(Mat*) addrRgba;
    detect(frame);
  }
  JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_TransparentDiff
      (JNIEnv * env, jclass, jlongArray addrRgba){

        vector<Mat> trainimgs;

              jsize a_len = env-> GetArrayLength(addrRgba);
              jlong *traindata = env-> GetLongArrayElements(addrRgba,0);
              for(int k=0;k<a_len;k++)
              {
                  Mat& newimage=*(Mat*)traindata[k];
                  trainimgs.push_back(newimage);
              }
              env->ReleaseLongArrayElements(addrRgba,traindata,0);
              int s;
              for(s=1;s<trainimgs.size();s++){
                        try{
                        Mat checkdif=FindDiff(trainimgs[0],trainimgs[s]);
                        trainimgs[0]=trainimgs[0]-checkdif+checkdif;
                          }catch(...){__android_log_print(ANDROID_LOG_INFO, "sometag", "ne postoji %d",6);}
                          }

    }
    Mat FindDiff(Mat img1,Mat img2){
                Mat im=img2.clone();
                Mat diffImage;
                Mat HSV_currentImage;
                Mat HSVbackgroundImagebg;
                cvtColor(img1, HSVbackgroundImagebg, CV_BGR2HSV);
                cv::cvtColor(img2, HSV_currentImage, CV_BGR2HSV);
                absdiff(HSVbackgroundImagebg, HSV_currentImage, diffImage);
                Mat mask(diffImage.size(), CV_8UC1);
                mask = FillImage(diffImage);
                Mat src;
                bitwise_and(img2, img2, src, mask);
                Mat ses11;
                bitwise_and(img2, img2, ses11, FillImage(SepImages1(src)));
                return ses11;
            }


 JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_detectHuman
     (JNIEnv * env, jclass, jlongArray addrRgba){
       vector<Mat> trainimgs;

       jsize a_len = env-> GetArrayLength(addrRgba);
       jlong *traindata = env-> GetLongArrayElements(addrRgba,0);
       for(int k=0;k<a_len;k++)
       {
           Mat& newimage=*(Mat*)traindata[k];
           trainimgs.push_back(newimage);
       }
       env->ReleaseLongArrayElements(addrRgba,traindata,0);
       //TODO PRVO ODUZMI SVE RAZLIKE SA BACKGROUND I NAPRAVI NEKI TEMPLATE :D
             for(int y=1;y<trainimgs.size();y++){
                Mat backgroundImage = (Mat) trainimgs[0];
                Mat currentImage = (Mat) trainimgs[y];
                Mat HSV_currentImage;
                Mat HSVbackgroundImagebg;
                Mat diffImage;
                Mat diffImage2;
                cvtColor(backgroundImage, HSVbackgroundImagebg, CV_BGR2HSV);
                cv::cvtColor(currentImage, HSV_currentImage, CV_BGR2HSV);
                absdiff(HSVbackgroundImagebg, HSV_currentImage, diffImage);
                 Mat mask = FillImage(diffImage);

                 Mat src;
                 bitwise_and(currentImage, currentImage, src, mask);
                 Mat fil;
                 try{
                   fil=FillImage(SepImages(src,1));

                   }catch(...){}__android_log_print(ANDROID_LOG_INFO, "sometag", "save %d",save);
                   if(save!=0){

                             for(int i=save-1;i>0;i--){
                              __android_log_print(ANDROID_LOG_INFO, "sometag","proso je jebote");
                             int tru=0;
                             int fols=0;
                             Mat ses1;
                             Mat ses2;
                             Mat ses3;
                             try{

                             fil=FillImage(SepImages(src,i));
                             bitwise_and(backgroundImage, backgroundImage, ses1,fil);
                             bitwise_and(currentImage, currentImage, ses2, fil);
                             for(int i=1;i<trainimgs.size();i++){
                             if(i!=y){
                             bitwise_and(trainimgs[i],trainimgs[i] , ses3, fil);
                             if(truth(ses2,ses3)){
                                          tru++;
                                        }else{
                                            fols++;
                                        }
                               }
                             }
                             if(tru>=fols){
                             try{
                                trainimgs[0]=trainimgs[0]-ses1+ses2;
                                 __android_log_print(ANDROID_LOG_INFO, "TACNOST", "OVDE JE KAO GRESKA");

                                }catch(...){__android_log_print(ANDROID_LOG_INFO, "sometag", "CANNOT MERGE");}
                                __android_log_print(ANDROID_LOG_INFO, "TACNOST", "TRUE");
                              }else{
                                    __android_log_print(ANDROID_LOG_INFO, "TACNOST", "FALSE");
                              }
                               }catch(...){__android_log_print(ANDROID_LOG_INFO, "sometag", "ne postoji %d",i);}
                               }
                              }else{
                               trainimgs[0]=trainimgs[0];
                              }

    }
    }


    bool truth(Mat img1,Mat img2){
    Mat backgroundImage=img1;
    Mat third=img2;
    for(int j=0; j<backgroundImage.rows; ++j)
                                      for(int i=0; i<backgroundImage.cols; ++i)
                                      {
                                          Vec3b pix = backgroundImage.at<cv::Vec3b>(j,i);
                                          Vec3b pix1= third.at<cv::Vec3b>(j,i);


                                          if(pix1!=pix){
                                           __android_log_print(ANDROID_LOG_INFO, "sometag", "J je :%d I je: %d",j,i);
                                          return false;

                                          }

                                      }
                                      return true;
    }
    Mat SepImages1(Mat result){
        try{
    Mat src=result;
           // the first command-line parameter must be a filename of the binary
           // (black-n-white) image

            Mat srcGray;
           cvtColor(src,srcGray,CV_BGR2GRAY);
           vector<vector<Point> > contours;
           vector<Vec4i> hierarchy;
           findContours( srcGray, contours, hierarchy,

               CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );
             __android_log_print(ANDROID_LOG_INFO, "sometag", "contours = %d ", contours.size());
             Mat dst = Mat::zeros(src.rows, src.cols, CV_8UC3);
           // iterate through all the top-level contours,
           // draw each connected component with its own random color
           int idx = 0;
           for( ; idx >= 0; idx = hierarchy[idx][0] )
           {
               Scalar color( rand()&255, rand()&255, rand()&255 );
               drawContours(dst , contours, idx, color, CV_FILLED, 8, hierarchy );
           }

           return dst;
           } catch(...){
                Mat s;
                return s;
           }
        }
        int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }
    Mat SepImages(Mat result,int num){
    try{
        Mat src=result;
        Size s(src.cols,src.rows);
        int sy=gcd(abs(src.cols),abs(src.rows));
        Size s1(src.cols/sy*10,src.rows/sy*10);
        resize(src, src, s1, 1, 1, INTER_NEAREST);
       // the first command-line parameter must be a filename of the binary
       // (black-n-white) image
        Mat srcGray;
       cvtColor(src,srcGray,CV_BGR2GRAY);
       vector<vector<Point> > contours;
        vector<Mat> razlike;
       vector<Vec4i> hierarchy;
       findContours( srcGray, contours, hierarchy,

           CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );
         __android_log_print(ANDROID_LOG_INFO, "sometag", "contours = %d ", contours.size());

       // iterate through all the top-level contours,
       // draw each connected component with its own random color
       int idx = 0;
       for( ; idx >= 0; idx = hierarchy[idx][0] )
       {   Mat dst = Mat::zeros(src.rows, src.cols, CV_8UC3);
           Scalar color( rand()&255, rand()&255, rand()&255 );

           drawContours(dst , contours, idx, color, CV_FILLED, 8, hierarchy );
           razlike.push_back(dst);
       }
        save=razlike.size();
        __android_log_print(ANDROID_LOG_INFO, "sometag", "razlie %d",razlike.size());
         resize(razlike[num], razlike[num], s, 1, 1, INTER_NEAREST);
       return razlike[num];
       } catch(Exception e){
            Mat s(result.size(),result.type());
            __android_log_print(ANDROID_LOG_INFO, "sometag", "what %s",e.what());
            return s;
       }
    }
    Mat FillImage(Mat ImageYouWantToFill){
        Mat diffImage=ImageYouWantToFill;
        Mat mask(diffImage.size(), CV_8UC1);
                        float th = 30.0f;
                        float dist;

                        for(int j=0; j<diffImage.rows; ++j)
                            for(int i=0; i<diffImage.cols; ++i)
                            {
                                Vec3b pix = diffImage.at<cv::Vec3b>(j,i);
                                dist = (pix[0]*pix[0] + pix[1]*pix[1] + pix[2]*pix[2]);
                                            dist = sqrt(dist);
                                if(dist>th){
                                  mask.at<unsigned char>(j,i) = 255;

                                }


                            }
                            return mask;
    }
  void detect(Mat& frame){
                   String face_cascade_name = "/storage/emulated/0/Android/data/com.example.miskewolf.opencvradi/files/haarcascade_frontalface_alt.xml";

                   CascadeClassifier face_cascade;

                    if( !face_cascade.load( face_cascade_name ) ){  __android_log_print(ANDROID_LOG_INFO, "sometag", "ERROR LOADING" ); return ; };

                      std::vector<Rect> faces;
                        Mat frame_gray;

                        cvtColor( frame, frame_gray, CV_BGR2GRAY );
                        equalizeHist( frame_gray, frame_gray );

                        //-- Detect faces
                        face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );
                          Mat maskedImage=frame.clone();
                          __android_log_print(ANDROID_LOG_INFO, "sometag", "faces.size = %d ", faces.size());
                          Mat cop=frame.clone();
                        for( size_t i = 0; i < faces.size(); i++ )
                        {
                          Mat mask(maskedImage.size(),maskedImage.type());

                          Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
                            ellipse( mask, center, Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar( 255, 255, 255 ), -1, 8, 0 );
                            Mat mask_gray;
                            cvtColor( mask, mask_gray, CV_BGR2GRAY );
                            Mat f_gray;
                            cvtColor( cop, f_gray, CV_BGR2GRAY );
                            Mat x;
                             x=f_gray-(f_gray-mask_gray);
                            Mat y;
                            bitwise_and(frame, frame, y, x);
                             Mat z;
                            cvtColor(y, z, CV_BGR2HSV);

                        Mat ses=pixelize(y,faces[i].width*0.5,faces[i].height*0.5);
                        frame=frame-ses+ses;
      }
      }
      void StrangeMaske(Mat& frame,Mat& photos,float alfa, float beta){
        Mat B=photos.clone();
               String face_cascade_name = "/storage/emulated/0/Android/data/com.example.miskewolf.opencvradi/files/haarcascade_frontalface_alt.xml";

               CascadeClassifier face_cascade;

                if( !face_cascade.load( face_cascade_name ) ){  __android_log_print(ANDROID_LOG_INFO, "sometag", "ERROR LOADING" ); return ; };
                  std::vector<Rect> faces;
                    Mat frame_gray;

                    cvtColor( frame, frame_gray, CV_BGR2GRAY );
                    equalizeHist( frame_gray, frame_gray );

                    //-- Detect faces
                    face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0|CV_HAAR_SCALE_IMAGE, Size(30, 30) );
                      Mat maskedImage=frame.clone();
                      __android_log_print(ANDROID_LOG_INFO, "sometag", "faces.size = %d ", faces.size());
                      Mat cop=frame.clone();
                    for( size_t i = 0; i < faces.size(); i++ )
                    {
                      Mat mask(maskedImage.size(),maskedImage.type());

                      Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
                        ellipse( mask, center, Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar( 255, 255, 255 ), -1, 8, 0 );
                        Mat mask_gray;
                        cvtColor( mask, mask_gray, CV_BGR2GRAY );
                        Mat f_gray;
                        cvtColor( cop, f_gray, CV_BGR2GRAY );
                        Mat x;
                         x=f_gray-(f_gray-mask_gray);
                        Mat y;
                        bitwise_and(frame, frame, y, x);
                         Mat z;
                        cvtColor(y, z, CV_BGR2HSV);

                       Size s1(faces[i].width,faces[i].height);
                                 B.convertTo(B, -1, 1.1, 0);
                                 resize(B, B, s1, 1, 1, INTER_NEAREST);
                       // min_x, min_y should be valid in A and [width height] = size(B)
                       cv::Rect roi = cv::Rect(faces[i].x, faces[i].y, B.cols, B.rows);

                       // "out_image" is the output ; i.e. A with a part of it blended with B
                       cv::Mat out_image = frame.clone();

                       // Set the ROIs for the selected sections of A and out_image (the same at the moment)
                       cv::Mat A_roi= frame(roi);
                       cv::Mat out_image_roi = out_image(roi);

                       // Blend the ROI of A with B into the ROI of out_image
                       cv::addWeighted(A_roi,alfa,B,beta,0.0,out_image_roi);
                       cv::addWeighted(frame(roi),0,out_image_roi,1,0.0,frame(roi));
                       frame=frame;
      }

      }
      Mat transparent(Mat image){
        Mat src=image;
                       Mat dst;
                       Mat tmp,thr;
                       cvtColor(src,tmp,CV_BGR2GRAY);
                       threshold(tmp,thr,100,255,THRESH_BINARY);
                        vector< vector <Point> > contours; // Vector for storing contour
                            vector< Vec4i > hierarchy;
                            int largest_contour_index=0;
                            int largest_area=0;

                       Mat alpha=Mat::zeros(src.rows, src.cols, CV_8UC3);
                       findContours( tmp, contours, hierarchy,CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE ); // Find the contours in the image
                       __android_log_print(ANDROID_LOG_INFO, "sometag", "contours %d",contours.size());
                                   for( int i = 0; i< contours.size(); i++ ) // iterate through each contour.
                                     {
                                      double a=contourArea( contours[i],false);  //  Find the area of contour
                                      if(a>largest_area){
                                      largest_area=a;
                                      largest_contour_index=i;                //Store the index of largest contour
                                      }
                                     }
                           drawContours( alpha,contours, largest_contour_index, Scalar(255),CV_FILLED, 8, hierarchy );
                      Mat omg = FillImage(alpha);
                      Mat seks;
                      bitwise_and(image, image, seks, omg);
                      return seks;
      }
      Mat pixelize(Mat src,int width, int height){
          Size s(src.cols, src.rows);
          Size s1(width*50/100,height*50/100);
          src.convertTo(src, -1, 1.1, 0);

          resize(src, src, s1, 1, 1, INTER_NEAREST);
          resize(src, src, s, 1, 1, INTER_NEAREST);
          return src;
      }

JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_FaceRecognition
          (JNIEnv * env, jclass, jlong addrRgba){

         }
         JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_TrainFaces(JNIEnv, jobject, jlong addrImages, jlong addrClasses) {
             Mat *pImages = (Mat *) addrImages; // Each images is represented as a column vector
             Mat *pClasses = (Mat *) addrClasses; // Classes are represented as a vector

             LOG_ASSERT(pImages->type() == CV_8U, "Images must be an 8-bit matrix");
             MatrixXi images;
             cv2eigen(*pImages, images); // Copy from OpenCV Mat to Eigen matrix

             //Facebase *pFacebase;
             if (pClasses == NULL) { // If classes are NULL, then train Eigenfaces
                 eigenfaces.train(images); // Train Eigenfaces
                 LOGI("Eigenfacess numComponents: %d", eigenfaces.numComponents);
                 //pFacebase = &eigenfaces;
             } else {
                 LOG_ASSERT(pClasses->type() == CV_32S && pClasses->cols == 1, "Classes must be a signed 32-bit vector");
                 VectorXi classes;
                 cv2eigen(*pClasses, classes); // Copy from OpenCV Mat to Eigen vector
                 LOG_ASSERT(classes.minCoeff() == 1, "Minimum value in the list must be 1");


             }


         }
         JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_MeasureDist(JNIEnv *env, jobject, jlong addrImage, jfloatArray minDist, jintArray minDistIndex, jfloatArray faceDist, jboolean useEigenfaces) {
             Facebase *pFacebase;
             if (useEigenfaces) {
                 LOGI("Using Eigenfaces");
                 pFacebase = &eigenfaces;
             } else {

             }

             if (pFacebase->V.any()) { // Make sure that the eigenvector has been calculated
                 Mat *pImage = (Mat *) addrImage; // Image is represented as a column vector

                 VectorXi image;
                 cv2eigen(*pImage, image); // Convert from OpenCV Mat to Eigen matrix

                 LOGI("Project faces"); // LEARNED LATER ABOUT LOGI HAHAHAH
                 VectorXf W = pFacebase->project(image); // Project onto subspace
                 LOGI("Reconstructing faces");
                 VectorXf face = pFacebase->reconstructFace(W);

                 LOGI("Calculate normalized Euclidean distance");
                 jfloat dist_face = pFacebase->euclideanDistFace(image, face);
                 LOGI("Face distance: %f", dist_face);
                 env->SetFloatArrayRegion(faceDist, 0, 1, &dist_face);

                 VectorXf dist = pFacebase->euclideanDist(W);

                 vector<size_t> sortedIdx = sortIndexes(dist);
                 for (auto idx : sortedIdx)
                     LOGI("dist[%zu]: %f", idx, dist(idx));

                 int minIndex = (int) sortedIdx[0];
                 env->SetFloatArrayRegion(minDist, 0, 1, &dist(minIndex));
                 env->SetIntArrayRegion(minDistIndex, 0, 1, &minIndex);
             }else{
                __android_log_print(ANDROID_LOG_DEBUG, "ERORRR", "jbg");
             }
         }
          JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_DeleteDiffInverse
                         (JNIEnv * env, jclass, jlongArray addrRgba){
                          vector<Mat> trainimgs;

                               jsize a_len = env-> GetArrayLength(addrRgba);
                               jlong *traindata = env-> GetLongArrayElements(addrRgba,0);
                               for(int k=0;k<a_len;k++)
                               {
                                   Mat& newimage=*(Mat*)traindata[k];
                                   trainimgs.push_back(newimage);
                               }
                               env->ReleaseLongArrayElements(addrRgba,traindata,0);
                               //TODO PRVO ODUZMI SVE RAZLIKE SA BACKGROUND I NAPRAVI NEKI TEMPLATE :D
                                     for(int y=1;y<trainimgs.size();y++){
                                        Mat backgroundImage = (Mat) trainimgs[0];
                                        Mat currentImage = (Mat) trainimgs[y];
                                        Mat HSV_currentImage;
                                        Mat HSVbackgroundImagebg;
                                        Mat diffImage;
                                        Mat diffImage2;
                                        cvtColor(backgroundImage, HSVbackgroundImagebg, CV_BGR2HSV);
                                        cv::cvtColor(currentImage, HSV_currentImage, CV_BGR2HSV);
                                        absdiff(HSVbackgroundImagebg, HSV_currentImage, diffImage);
                                         Mat mask = FillImage(diffImage);

                                         Mat src;
                                         bitwise_and(currentImage, currentImage, src, mask);
                                          trainimgs[0]=trainimgs[0]-src;
                         }
                         }
                 JNIEXPORT void JNICALL Java_com_example_miskewolf_opencvradi_OpencvNativeClass_DeleteDiff
                 (JNIEnv * env, jclass, jlongArray addrRgba){
                  vector<Mat> trainimgs;

                       jsize a_len = env-> GetArrayLength(addrRgba);
                       jlong *traindata = env-> GetLongArrayElements(addrRgba,0);
                       for(int k=0;k<a_len;k++)
                       {
                           Mat& newimage=*(Mat*)traindata[k];
                           trainimgs.push_back(newimage);
                       }
                       env->ReleaseLongArrayElements(addrRgba,traindata,0);
                       //TODO PRVO ODUZMI SVE RAZLIKE SA BACKGROUND I NAPRAVI NEKI TEMPLATE :D
                             for(int y=1;y<trainimgs.size();y++){
                                Mat backgroundImage = (Mat) trainimgs[0];
                                Mat currentImage = (Mat) trainimgs[y];
                                Mat HSV_currentImage;
                                Mat HSVbackgroundImagebg;
                                Mat diffImage;
                                Mat diffImage2;
                                cvtColor(backgroundImage, HSVbackgroundImagebg, CV_BGR2HSV);
                                cv::cvtColor(currentImage, HSV_currentImage, CV_BGR2HSV);
                                absdiff(HSVbackgroundImagebg, HSV_currentImage, diffImage);
                                 Mat mask = FillImage(diffImage);

                                 Mat src;
                                 bitwise_and(currentImage, currentImage, src, mask);
                                  trainimgs[0]=trainimgs[0]-(trainimgs[0]-src);
                 }

                 }



