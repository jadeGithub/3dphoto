package com.example.jade.three;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/***
 * 3D展示用的测试view
 * created by chenjuanxia on 2019／1／2
 */


public class ThreeSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean isDrawing;
    private float downX = 0;
    private float moveX = 0;
    private String KEY_IMG = "img_";
    private int cunrrentPositon = 0;
    private int lastPostion = 0;
    private int imageCount = 10;
    private LruCache<String, Bitmap> mImageCache;
    private Paint imagePaint;
    private Matrix matric;
    private Bitmap drawBitmap;
    private Path mPath;
    private int x = 0, y = 0;
    private int images[] = new int[]{R.drawable.img_0, R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
            R.drawable.img_5, R.drawable.img_6, R.drawable.img_7, R.drawable.img_8, R.drawable.img_9};

    public ThreeSurfaceView(Context context) {
        super(context);
        initView();
    }

    private void initView() {

        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        imagePaint = new Paint();
        imagePaint.setColor(Color.BLUE);
        imagePaint.setAntiAlias(true);
        imagePaint.setFilterBitmap(true);

        mPath = new Path();
        imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        imagePaint.setColor(Color.RED);
        imagePaint.setStyle(Paint.Style.STROKE);
        imagePaint.setStrokeWidth(10);
        imagePaint.setStrokeCap(Paint.Cap.ROUND);
        imagePaint.setStrokeJoin(Paint.Join.ROUND);

        matric = new Matrix();
        matric.postScale(1.5f, 1.5f);


    }

    private void loadLocalImages(int start) {
        if (start > 0 && start <= imageCount - 1) {
            if (getImageFromCache(KEY_IMG + start) == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images[start], getOption(images[start]));
                addImageCache(KEY_IMG + start, bitmap);
            }
        }
    }

    private void loadLocalImages(int start, int n) {
        if (start < 0 || start + n > imageCount - 1) {
            return;
        }

        for (int i = start; i < start + n; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images[i], getOption(images[i]));
            addImageCache(KEY_IMG + i, bitmap);
        }
        drawBitmap = Bitmap.createBitmap(getImageFromCache(KEY_IMG + 0), 0, 0, getImageFromCache(KEY_IMG + 0).getWidth(), getImageFromCache(KEY_IMG + 0).getHeight(), matric, true);
    }


    private BitmapFactory.Options getOption(int imageId) {
        BitmapFactory.Options imgOpt = new BitmapFactory.Options();
        imgOpt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), imageId, imgOpt);

        int requireHeight = 1600;
        int requireWidth = 1000;

        int height = (int) (imgOpt.outHeight * 2.5);
        int width = (int) (imgOpt.outWidth * 2.5);
        int sizeScale = 1;

        while (requireHeight < height || requireWidth < width) {
            sizeScale = sizeScale * 2;
            height = height / 2;
            width = width / 2;
        }

        imgOpt.inJustDecodeBounds = false;
        imgOpt.inSampleSize = sizeScale;
        return imgOpt;

    }

    public ThreeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ThreeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        loadLocalImages(0, 4);
        mPath.moveTo(0, 400);
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    @Override
    public void run() {
        while (isDrawing) {
            drawImage();
//            x += 1;
//            y = (int) (100 * Math.sin(x * 2 * Math.PI / 180) + 400);
//            mPath.lineTo(x, y);
            //通过线程休眠以控制刷新速度
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void drawImage() {
        try {
            mCanvas = mHolder.lockCanvas();
            //初始化画布并在画布上画一些东西
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawPath(mPath, imagePaint);

            //mCanvas.drawLine(90, 90, 500, 500, imagePaint);
            mCanvas.drawBitmap(drawBitmap, 100, 200, imagePaint);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //判断画布是否为空，从而避免黑屏情况
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    public void drawImageWithIndex(int progress) {
        Log.i("chan",mImageCache.toString());
        if (progress >= 0 && progress < imageCount && getImageFromCache(KEY_IMG + progress) != null) {
            loadLocalImages(progress - 1);
            loadLocalImages(progress);
            loadLocalImages(progress + 1);
            drawBitmap = Bitmap.createBitmap(getImageFromCache(KEY_IMG + progress), 0, 0, getImageFromCache(KEY_IMG + progress).getWidth(), getImageFromCache(KEY_IMG + progress).getHeight(), matric, true);

        }


    }

    // @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                downX = event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                moveX = (int) event.getX();
//                float disX = moveX - downX;
//                if (Math.abs(disX) > 50) {
//                    if (disX >= 0) {
//                        //向右滑动
//                        int dur_pos = (int) (disX / 100);
//                        if (dur_pos - cunrrentPositon > 0) {
//                            cunrrentPositon++;
//
//                            if (getImageFromCache(KEY_IMG + cunrrentPositon) != null) {
//                                drawBitmap = getImageFromCache(KEY_IMG + cunrrentPositon);
//                            } else {
//                                cunrrentPositon = 0;
//                            }
//                        }
//
//                    } else {
//                        //向左滑动
//
//                    }
//                }
//
//                break;
//        }
//        return true;
//    }

    private void addImageCache(String key, Bitmap image) {
        if (mImageCache != null && image != null && getImageFromCache((key)) == null) {
            mImageCache.put(key, image);
        }
    }

    //使用时注意判空
    private Bitmap getImageFromCache(String key) {
        if (mImageCache != null) {
            return mImageCache.get(key);
        }
        return null;
    }

    private void removeFromCache(String key) {
        if (mImageCache != null) {
            mImageCache.remove(key);
        }
    }

}
