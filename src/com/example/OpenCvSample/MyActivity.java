package com.example.OpenCvSample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.util.Log;
import android.widget.ImageView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class MyActivity extends Activity {

    Mat target;
    FeatureDetector detector;
    DescriptorExtractor extractor;

    ImageView myImageView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    try {
                        target = Utils.loadResource(MyActivity.this, R.raw.f1, Highgui.CV_LOAD_IMAGE_COLOR);
                        detector = FeatureDetector.create(FeatureDetector.ORB);
                        extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

                        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
                        Mat descriptors = new Mat();

                        long time = System.currentTimeMillis();
                        detector.detect(target, keyPoint);
                        extractor.compute(target, keyPoint, descriptors);
                        Log.d("opencv", "计算关键点耗时（毫秒）: " + (System.currentTimeMillis() - time) +
                                ", 关键点总数: " + keyPoint.toArray().length);

                        for (KeyPoint k : keyPoint.toArray()) {
                            Core.circle(target, k.pt, 5, new Scalar(255, 0, 0));
                        }

                        Mat tmp = new Mat(target.cols(), target.rows(), CvType.CV_8U, new Scalar(4));
                        Bitmap image = Bitmap.createBitmap(target.cols(), target.rows(), Bitmap.Config.ARGB_8888);
                        Imgproc.cvtColor(target, tmp, Imgproc.COLOR_RGB2BGRA, 4);
                        Utils.matToBitmap(tmp, image);
                        myImageView.setImageBitmap(image);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myImageView = (ImageView) findViewById(R.id.myImageView);
    }
}
