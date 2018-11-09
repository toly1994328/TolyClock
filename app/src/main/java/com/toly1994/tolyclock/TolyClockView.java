package com.toly1994.tolyclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Random;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/7 0007:22:13<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：自定义表
 */
public class TolyClockView extends View {

    private Picture mPictureGrid;//网格Canvas元件
    private Point mCoo = new Point(500, 800);//坐标系原点
    private Picture mPictureCoo;//坐标系Canvas元件
    private Path mMainPath;
    private Paint mMainPaint;

    public TolyClockView(Context context) {
        this(context, null);
    }

    public TolyClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        //初始化画笔
        mMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setStrokeCap(Paint.Cap.ROUND);

        mMainPath = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawPicture(mPictureGrid);
//        canvas.drawPicture(mPictureCoo);

        canvas.save();//新建图层1
        canvas.translate(mCoo.x, mCoo.y);

        drawBreakCircle(canvas);
        drawDot(canvas);
        drawText(canvas);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        drawH(canvas, hour / 12.f * 360 - 90 + min / 60.f * 30 + sec / 3600.f * 30);
        drawM(canvas, min / 60.f * 360 - 90 + sec / 60.f);
        drawS(canvas, sec / 60.f * 360 - 90);
        canvas.restore();

    }

    private void drawDot(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                canvas.save();
                canvas.rotate(30 * i);
                mMainPaint.setStrokeWidth(8);
                mMainPaint.setColor(randomRGB());
                canvas.drawLine(250, 0, 300, 0, mMainPaint);

                mMainPaint.setStrokeWidth(10);
                mMainPaint.setColor(Color.BLACK);
                canvas.drawPoint(250, 0, mMainPaint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.rotate(6 * i);
                mMainPaint.setStrokeWidth(4);
                mMainPaint.setColor(Color.BLUE);
                canvas.drawLine(280, 0, 300, 0, mMainPaint);
                canvas.restore();
            }
        }
    }

    /**
     * 绘制破碎的圆
     *
     * @param canvas
     */
    private void drawBreakCircle(Canvas canvas) {
        mMainPaint.setStrokeWidth(8);
        mMainPaint.setColor(Color.parseColor("#D5D5D5"));

        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.rotate(90 * i);
            canvas.drawArc(
                    -350, -350, 350, 350,
                    10, 70, false, mMainPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制分针
     *
     * @param canvas
     * @param deg
     */
    private void drawM(Canvas canvas, float deg) {

        canvas.save();
        canvas.rotate(deg);
        mMainPaint.setColor(Color.parseColor("#87B953"));
        mMainPaint.setStrokeWidth(8);
        canvas.drawLine(0, 0, 200, 0, mMainPaint);

        mMainPaint.setColor(Color.GRAY);
        mMainPaint.setStrokeWidth(30);
        canvas.drawPoint(0, 0, mMainPaint);
        canvas.restore();
    }

    /**
     * 添加文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        mMainPaint.setTextSize(60);
        mMainPaint.setStrokeWidth(5);
        mMainPaint.setStyle(Paint.Style.FILL);
        mMainPaint.setTextAlign(Paint.Align.CENTER);
        mMainPaint.setColor(Color.BLUE);
        canvas.drawText("Ⅲ", 350, 30, mMainPaint);
        canvas.drawText("Ⅵ", 0, 350 + 30, mMainPaint);
        canvas.drawText("Ⅸ", -350, 30, mMainPaint);
        canvas.drawText("Ⅻ", 0, -350 + 30, mMainPaint);
        //使用外置字体放在assets目录下
        Typeface myFont = Typeface.createFromAsset(getContext().getAssets(), "CHOPS.TTF");
        mMainPaint.setTypeface(myFont);
        mMainPaint.setTextSize(70);
        canvas.drawText("Toly", 0, -150, mMainPaint);
    }

    /**
     * 绘制秒针
     *
     * @param canvas
     * @param deg
     */
    private void drawS(Canvas canvas, float deg) {
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setColor(Color.parseColor("#6B6B6B"));
        mMainPaint.setStrokeWidth(8);
        mMainPaint.setStrokeCap(Paint.Cap.SQUARE);

        canvas.save();
        canvas.rotate(deg);

        canvas.save();
        canvas.rotate(45);
        //使用path绘制：在init里初始化一下就行了
        mMainPath.addArc(-25, -25, 25, 25, 0, 240);
        canvas.drawPath(mMainPath, mMainPaint);
        canvas.restore();

        mMainPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(-25, 0, -50, 0, mMainPaint);

        mMainPaint.setStrokeWidth(2);
        mMainPaint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, 320, 0, mMainPaint);

        mMainPaint.setStrokeWidth(15);
        mMainPaint.setColor(Color.parseColor("#8FC552"));
        canvas.drawPoint(0, 0, mMainPaint);
        canvas.restore();
    }

    /**
     * 绘制时针
     *
     * @param canvas
     * @param deg
     */
    private void drawH(Canvas canvas, float deg) {

        canvas.save();
        canvas.rotate(deg);
        mMainPaint.setColor(Color.parseColor("#8FC552"));
        mMainPaint.setStrokeCap(Paint.Cap.ROUND);

        mMainPaint.setStrokeWidth(8);
        canvas.drawLine(0, 0, 150, 0, mMainPaint);
        canvas.restore();
    }

    /**
     * 返回随机颜色
     *
     * @return 随机颜色
     */
    public static int randomRGB() {
        Random random = new Random();
        int r = 30 + random.nextInt(200);
        int g = 30 + random.nextInt(200);
        int b = 30 + random.nextInt(200);
        return Color.rgb( r, g, b);
    }
}
