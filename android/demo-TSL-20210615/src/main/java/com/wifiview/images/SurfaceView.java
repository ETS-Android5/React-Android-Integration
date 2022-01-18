package com.wifiview.images;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.ImageView;

/**
 * Created by Admin on 2018/11/21.
 */

public class SurfaceView extends android.view.SurfaceView implements SurfaceHolder.Callback {



    private static final float MIN_ZOOM_SCALE = 1.0f;
    private static final float FLOAT_TYPE = 1.0f;
    private float mCurrentScale = 1.0f;

    private Rect mRectSrc = null;
    private Rect mRectDes = null;
    private ImageView imageView;


    private int mCenterX, mCenterY;
    int mSurfaceHeight, mSurfaceWidth, mImageHeight, mImageWidth;

    private PointF mStartPoint = new PointF();
    private float mStartDistance = 0f;

    private SurfaceHolder mSurHolder = null;
    private Bitmap mBitmap;
    private Canvas m_canvas = null;
    private float mDegree = 0;
    private Paint m_paint = null;

    private Context mContext;
    private boolean mDrawFlag = true;

    public SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
        mSurHolder.setFormat(PixelFormat.TRANSPARENT);// 设置背景透明
        mRectSrc = new Rect(0, 0, this.getWidth(), this.getHeight());
        mRectDes = new Rect(0, 0, this.getWidth(), this.getHeight());
        mSurHolder.addCallback(this);
        m_paint = new Paint();
        m_paint.setColor(Color.BLUE);
        m_paint.setAntiAlias(true);
        setFocusable(true);
        mContext = context;
    }

    private void init() {

        mCurrentScale = 1.3778453f;
        mCenterX = mImageWidth / 2;
        mCenterY = mImageHeight / 2;
        calcRect();

    }
    private void calcRect() {
        int w, h;
        float imageRatio, surfaceRatio;
        imageRatio = FLOAT_TYPE * mImageWidth / mImageHeight;
        surfaceRatio = FLOAT_TYPE * mSurfaceWidth / mSurfaceHeight;
        Configuration mConfiguration = this.getResources().getConfiguration();
        int ori = mConfiguration.orientation;
        if (imageRatio < surfaceRatio) {
            h = mSurfaceHeight;
            w = (int) (h * imageRatio);
        } else {
            w = mSurfaceWidth;
            h = (int) (w / imageRatio);
        }

        if (mCurrentScale > MIN_ZOOM_SCALE) {
            w = Math.min(mSurfaceWidth, (int) (w * mCurrentScale));
            h = Math.min(mSurfaceHeight, (int) (h * mCurrentScale));
        } else {
            mCurrentScale = MIN_ZOOM_SCALE;

        }

        mRectDes.left = (mSurfaceWidth - w) / 2;
        mRectDes.top = (mSurfaceHeight - w) / 2;
        mRectDes.right = mRectDes.left + w;
        mRectDes.bottom = mRectDes.top + w;


        float curImageRatio = FLOAT_TYPE * w / h;
        int h2, w2;
        if (curImageRatio > imageRatio) {

            h2 = (int) (mImageHeight / mCurrentScale);
            w2 = (int) (h2 * curImageRatio);
        } else {
            w2 = (int) (mImageWidth / mCurrentScale);
            h2 = (int) (w2 / curImageRatio);
        }

        mRectSrc.left = mCenterX - w2 / 2;
        mRectSrc.top = mCenterY - h2 / 2;
        mRectSrc.right = mRectSrc.left + w2;
        mRectSrc.bottom = mRectSrc.top + h2;
    }
    public void SetBitmap(Bitmap b) {

        if (b == null) {
            return;
        }
        mBitmap = b;
        if (mImageHeight != mBitmap.getHeight()
                || mImageWidth != mBitmap.getWidth()) {
            mImageHeight = mBitmap.getHeight();
            mImageWidth = mBitmap.getWidth();
            init();
        }
        showBitmap();
    }
    private void showBitmap() {
        synchronized (this) {
            try {
                if (mBitmap != null && mDrawFlag) {
                    if (mSurHolder.getSurface().isValid()) {
                        m_canvas = mSurHolder.lockCanvas();
                        if (m_canvas != null) {
                            Path path = new Path();
                            Configuration mConfiguration = this.getResources().getConfiguration();//获取设置的配置信息
                            int ori = mConfiguration.orientation;// 获取屏幕方向
                            if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {// 横屏
                                int x = this.getWidth() / 2;
                                int y = this.getHeight() / 2;
                                path.addCircle(x, y, y, Path.Direction.CCW);

                            } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {// 竖屏
                                int x = this.getWidth() / 2;
                                int y = this.getHeight() / 2;
                                path.addCircle(x, y, y, Path.Direction.CCW);

                            }
                            calcRect();
                            if(Build.VERSION.SDK_INT >= 26){
                                m_canvas.clipPath(path);
                            }else {
                                m_canvas.clipPath(path, Region.Op.REPLACE);
                            }
                            m_canvas.drawColor(Color.BLACK);
                            m_canvas.rotate(mDegree, mRectDes.centerX(), mRectDes.centerY());
                            m_canvas.drawBitmap(mBitmap, mRectSrc, mRectDes, null);
                            if (mSurHolder != null&&mSurHolder.getSurface().isValid()) {

                            }
                        }
                    }
                    if (m_canvas!=null){
                        mSurHolder.unlockCanvasAndPost(m_canvas);
                    }
                }

            }catch (IllegalStateException  e){
                e.printStackTrace();
                mDrawFlag = false;
            }

        }
    }


    public void setRotate(float mDegree) {
        this.mDegree = mDegree;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mDrawFlag = true;
    }

    // 初始化
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        mRectDes.set(0, 0, width, height);

        mSurfaceHeight = height;
        mSurfaceWidth = width;

        init();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            mDrawFlag = false;
        }
    }

}
