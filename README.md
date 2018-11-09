#### 零、前言

>前几天介绍了一大堆Android的Canvas，Paint,Path的API，接下来将是灵活地使用他们  
今天带来的是一个手表的绘制，经过本篇的洗礼，相信你会对Canvas的`图层`概念有更深刻的理解  
至于表的美丑不是本文的重点，本文只有一个目的，就是理清Canvas的save和restore的意义

![表.gif](https://upload-images.jianshu.io/upload_images/9414344-e7a3f0628b0c5b46.gif?imageMogr2/auto-orient/strip)

---
#### 一、准备工作

##### 1.新建类继承View

```
public class TolyClockView extends View {

    public TolyClockView(Context context) {
        this(context, null);
    }

    public TolyClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        //TODO 初始化
    }
    
       @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //TODO 绘制
    }
```

##### 2.分析一下

>一般我们都会这样去自定义一个View,但很少人会有`图层`这个概念，毕竟咱都是敲代码的   
如下图，一开始是一个x,y轴在顶点的图层,如果你不用save(),那你始终都在这个图层，图层栈始终只有一个  

![开始绘制时.png](https://upload-images.jianshu.io/upload_images/9414344-7aaf25742a62ce32.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


---

##### 3.下面在这个界面上绘制本人专用坐标系:(已封装成工具，`附在文尾`)
>网格和坐标系属于辅助性的工具，绘制起来比较多,所以使用Picture录制，在init()里初始化  
Picture在onDraw里绘制高效些，区别就像`准备一车砖盖房子和造一块才砖盖一下房子`  

###### 

```
//成员变量
private Picture mPictureGrid;//网格Canvas元件
private Point mCoo = new Point(500, 800);//坐标系原点
private Picture mPictureCoo;//坐标系Canvas元件

//init()中
mPictureGrid = HelpDraw.getGrid(getContext());
mPictureCoo = HelpDraw.getCoo(getContext(), mCoo);
//初始化画笔
mMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
mMainPaint.setStyle(Paint.Style.STROKE);
mMainPaint.setStrokeCap(Paint.Cap.ROUND);

//onDraw里
canvas.drawPicture(mPictureGrid);
canvas.drawPicture(mPictureCoo);
```

>正如API字面上的意思，在canvas上将网格和坐标系两张`图片`绘制出来,如下图：


![绘制坐标系时.png](https://upload-images.jianshu.io/upload_images/9414344-e3bbe8aa14762ef1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



---
#### 二、绘制逻辑
>准备工作做好了，下面要到正题了

##### 1.onDraw里
```
canvas.save();//保存先前状态(相当于在另一个图层操作)
canvas.translate(mCoo.x, mCoo.y);//将画布定点平移到绘制的坐标系中心
canvas.restore();//合并到root图层
```

##### 2.看一下这两句翻译在图上是什么意思：  
>一旦canvas.save()，相当于新建了一个图层(黑色虚线所示),  
然后canvas.translate(mCoo.x, mCoo.y)将新建的图层向右和向下移动    
新建的图层的好处:只有栈顶的图层才能操作(如Canvas移动时，root图层并没有动，这正是我们想要的)


![save和translate.png](https://upload-images.jianshu.io/upload_images/9414344-9ece979946771e62.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.绘制外圈破碎的圆:drawBreakCircle(canvas)


```
 /**
 * 绘制破碎的圆
 * @param canvas
 */
private void drawBreakCircle(Canvas canvas) {
    for (int i = 0; i < 4; i++) {
        canvas.save();//保存先前状态(相当于在另一个图层操作)
        canvas.rotate(90 * i);
        mMainPaint.setStrokeWidth(8);
        mMainPaint.setColor(Color.parseColor("#D5D5D5"));
        //在-350, -350, 350, 350的矩形区域,从10°扫70°绘制圆弧
        canvas.drawArc(
                -350, -350, 350, 350,
                10, 70, false, mMainPaint);
        canvas.restore();//恢复先前状态(相当于将图层和前一图层合并)
    }
}     
```

>先看i=0时：  
由于save了，前面的图层被锁定，相当于在另一个图层操作

![绘制碎圆.png](https://upload-images.jianshu.io/upload_images/9414344-27062a46ce8684ef.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

> `canvas.restore()`调用后，   
图层2将它的结果给了图层1，`挥挥衣袖，不带走一片云彩`，出栈了

![绘制碎圆2.png](https://upload-images.jianshu.io/upload_images/9414344-7e0a6d7ad90b3a9b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>先看i=1时：  
由于save了，前面的图层被锁定，相当于在另一个图层操作  
这里canvas.rotate(90 * 1)相当于当前图层转了90°，如图：  
`注意`:我只将坐标轴的第一象限涂色,canvas图层是一个无限的面，canvas宽高只是限制显示，  
旋转、平移、缩放等的关键在于坐标轴的变换，旋转90°相当于坐标轴转了90°

![绘制碎圆3.png](https://upload-images.jianshu.io/upload_images/9414344-eb9b9f374967d5bf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


>`canvas.restore()`调用后，  
图层2将它的结果给了图层1，`挥挥衣袖，不带走一片云彩`，出栈了

![绘制碎圆4.png](https://upload-images.jianshu.io/upload_images/9414344-3ed6e8b5db9ba7da.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>经过这两个图层的演示，想必你应该明白图层的作用了吧。  
最后画完之后，图层全合并到root

![绘制碎圆5.png](https://upload-images.jianshu.io/upload_images/9414344-255beba5d2ea8a30.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---

##### 4.绘制小点
>画60个点(小线)，每逢5变长，也就是画直线，每次将画布旋转360/60=6°

```
    private void drawDot(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                canvas.save();
                canvas.rotate(30 * i);
                mMainPaint.setStrokeWidth(8);
                mMainPaint.setColor(ColUtils.randomRGB());
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
```


![点绘制.png](https://upload-images.jianshu.io/upload_images/9414344-bedb96bd75120295.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)





---

##### 5.绘制时针：

```
 /**
     * 绘制时针
     *
     * @param canvas
     */
    private void drawH(Canvas canvas) {

        canvas.save();
        canvas.rotate(40);
        mMainPaint.setColor(Color.parseColor("#8FC552"));
        mMainPaint.setStrokeCap(Paint.Cap.ROUND);
        mMainPaint.setStrokeWidth(8);
        canvas.drawLine(0, 0, 150, 0, mMainPaint);
        canvas.restore();
    }
```

##### 6.绘制分针：

```
    /**
     * 绘制分针
     * @param canvas
     * @param deg
     */
    private void drawM(Canvas canvas) {
        canvas.save();
        canvas.rotate(120);
        mMainPaint.setColor(Color.parseColor("#87B953"));
        mMainPaint.setStrokeWidth(8);
        canvas.drawLine(0, 0, 200, 0, mMainPaint);
        mMainPaint.setColor(Color.GRAY);
        mMainPaint.setStrokeWidth(30);
        canvas.drawPoint(0, 0, mMainPaint);
        canvas.restore();
    }
```

##### 7.绘制秒针

```
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

```

![时针.png](https://upload-images.jianshu.io/upload_images/9414344-3f5c7a612f5c58de.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


---

##### 8.添加文字

```
/**
 * 添加文字
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
```


![效果.png](https://upload-images.jianshu.io/upload_images/9414344-97017c6fc305c1ae.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>好了，静态效果实现了，现在让它动起来吧


---
#### 三、让表动起来

##### 1.显示当前时间：

>表的旋转角度由每个针绘制是的`canvas.rotate(XXX);`决定，  
那么动态改变旋转的角度不就行了吗!
看下面一道数学题:

```
11:12:45秒，时针、分针、秒针的指针各与中心水平线的夹角?
答：
秒针：45 / 12.f * 360 - 90
分针：12 / 60.f * 360 - 90 + 45 / 60.f * 1
时针：11 / 60.f * 360 - 90 + 12 / 60.f * 30 + 45 / 3600.f * 30
```

##### 2.动态更新角度：绘制指针的三个函数,加角度参数

```
Calendar calendar = Calendar.getInstance();
int hour = calendar.get(Calendar.HOUR_OF_DAY);
int min = calendar.get(Calendar.MINUTE);
int sec = calendar.get(Calendar.SECOND);

drawS(canvas, hour / 60.f * 360 - 90 + min / 60.f * 30 + sec / 3600.f * 30);
drawM(canvas, min / 60.f * 360 - 90 + sec / 60.f);
drawH(canvas, sec / 60.f * 360 - 90);
```

![时间.png](https://upload-images.jianshu.io/upload_images/9414344-e0e804a92470f403.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---

##### 3.现在每次进来，都会更新时间了,怎么自动更新呢?
>循环的黄金搭档：`Handler + Timer` 

```
public class ClockActivity extends AppCompatActivity {

    /**
     * 新建Handler
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mView.invalidate();//处理：刷新视图
        }
    };

    private View mView;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toly_clock);
        ButterKnife.bind(this);

        mView = findViewById(R.id.id_toly_clock);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);//发送消息
            }
        };
        //定时任务
        timer.schedule(timerTask, 0, 1000);
    }
}
```

![表.gif](https://upload-images.jianshu.io/upload_images/9414344-e7a3f0628b0c5b46.gif?imageMogr2/auto-orient/strip)

>ok，完结散花(分析图画的真要命...)

---

#### 后记：捷文规范
##### 1.本文成长记录及勘误表
项目源码 | 日期|备注
---|---|---
[V0.1--github](https://github.com/toly1994328/TolyClock)|2018-11-8|[Android原生绘图之一起画个表](https://www.jianshu.com/p/706f1294077b)

##### 2.更多关于我

笔名 | QQ|微信|爱好
---|---|---|---|
张风捷特烈 | 1981462002|zdl1994328|语言
 [我的github](https://github.com/toly1994328)|[我的简书](https://www.jianshu.com/u/e4e52c116681)|[我的CSDN](https://blog.csdn.net/qq_30447263)|[个人网站](http://www.toly1994.com)

##### 3.声明
>1----本文由张风捷特烈原创,转载请注明  
2----欢迎广大编程爱好者共同交流  
3----个人能力有限，如有不正之处欢迎大家批评指证，必定虚心改正   
4----看到这里，我在此感谢你的喜欢与支持

---

![icon_wx_200.png](https://upload-images.jianshu.io/upload_images/9414344-8a0c95a090041a0d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 附录：网格+坐标系绘制工具：

##### 1.HelpDraw

```
/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/5 0005:8:43<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：辅助画布
 */
public class HelpDraw {

    /**
     * 获取屏幕尺寸
     */
    public static Point getWinSize(Context context) {
        Point point = new Point();
        Utils.loadWinSize(context, point);
        return point;
    }

    /**
     * 绘制网格
     */
    public static Picture getGrid(Context context) {
        return getGrid(getWinSize(context));
    }

    /**
     * 绘制坐标系
     */
    public static Picture getCoo(Context context, Point coo) {
        return getCoo(coo, getWinSize(context));
    }


    /**
     * 绘制网格
     *
     * @param winSize 屏幕尺寸
     */
    private static Picture getGrid(Point winSize) {

        Picture picture = new Picture();
        Canvas recording = picture.beginRecording(winSize.x, winSize.y);
        //初始化网格画笔
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        //设置虚线效果new float[]{可见长度, 不可见长度},偏移值
        paint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
        recording.drawPath(HelpPath.gridPath(50, winSize), paint);
        return picture;

    }

    /**
     * 绘制坐标系
     *
     * @param coo     坐标系原点
     * @param winSize 屏幕尺寸
     */
    private static Picture getCoo(Point coo, Point winSize) {
        Picture picture = new Picture();
        Canvas recording = picture.beginRecording(winSize.x, winSize.y);
        //初始化网格画笔
        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        //设置虚线效果new float[]{可见长度, 不可见长度},偏移值
        paint.setPathEffect(null);

        //绘制直线
        recording.drawPath(HelpPath.cooPath(coo, winSize), paint);
        //左箭头
        recording.drawLine(winSize.x, coo.y, winSize.x - 40, coo.y - 20, paint);
        recording.drawLine(winSize.x, coo.y, winSize.x - 40, coo.y + 20, paint);
        //下箭头
        recording.drawLine(coo.x, winSize.y, coo.x - 20, winSize.y - 40, paint);
        recording.drawLine(coo.x, winSize.y, coo.x + 20, winSize.y - 40, paint);
        //为坐标系绘制文字
        drawText4Coo(recording, coo, winSize, paint);
        return picture;
    }

    /**
     * 为坐标系绘制文字
     *
     * @param canvas  画布
     * @param coo     坐标系原点
     * @param winSize 屏幕尺寸
     * @param paint   画笔
     */
    private static void drawText4Coo(Canvas canvas, Point coo, Point winSize, Paint paint) {
        //绘制文字
        paint.setTextSize(50);
        canvas.drawText("x", winSize.x - 60, coo.y - 40, paint);
        canvas.drawText("y", coo.x - 40, winSize.y - 60, paint);
        paint.setTextSize(25);
        //X正轴文字
        for (int i = 1; i < (winSize.x - coo.x) / 50; i++) {
            paint.setStrokeWidth(2);
            canvas.drawText(100 * i + "", coo.x - 20 + 100 * i, coo.y + 40, paint);
            paint.setStrokeWidth(5);
            canvas.drawLine(coo.x + 100 * i, coo.y, coo.x + 100 * i, coo.y - 10, paint);
        }

        //X负轴文字
        for (int i = 1; i < coo.x / 50; i++) {
            paint.setStrokeWidth(2);
            canvas.drawText(-100 * i + "", coo.x - 20 - 100 * i, coo.y + 40, paint);
            paint.setStrokeWidth(5);
            canvas.drawLine(coo.x - 100 * i, coo.y, coo.x - 100 * i, coo.y - 10, paint);
        }

        //y正轴文字
        for (int i = 1; i < (winSize.y - coo.y) / 50; i++) {
            paint.setStrokeWidth(2);
            canvas.drawText(100 * i + "", coo.x + 20, coo.y + 10 + 100 * i, paint);
            paint.setStrokeWidth(5);
            canvas.drawLine(coo.x, coo.y + 100 * i, coo.x + 10, coo.y + 100 * i, paint);
        }

        //y负轴文字
        for (int i = 1; i < coo.y / 50; i++) {
            paint.setStrokeWidth(2);
            canvas.drawText(-100 * i + "", coo.x + 20, coo.y + 10 - 100 * i, paint);
            paint.setStrokeWidth(5);
            canvas.drawLine(coo.x, coo.y - 100 * i, coo.x + 10, coo.y - 100 * i, paint);
        }
    }
}
```

##### 2.HelpPath

```
/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/5 0005:8:05<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：辅助分析路径
 */
public class HelpPath {

    /**
     * 绘制网格:注意只有用path才能绘制虚线
     *
     * @param step    小正方形边长
     * @param winSize 屏幕尺寸
     */
    public static Path gridPath(int step, Point winSize) {

        Path path = new Path();

        for (int i = 0; i < winSize.y / step + 1; i++) {
            path.moveTo(0, step * i);
            path.lineTo(winSize.x, step * i);
        }

        for (int i = 0; i < winSize.x / step + 1; i++) {
            path.moveTo(step * i, 0);
            path.lineTo(step * i, winSize.y);
        }
        return path;
    }

    /**
     * 坐标系路径
     *
     * @param coo     坐标点
     * @param winSize 屏幕尺寸
     * @return 坐标系路径
     */
    public static Path cooPath(Point coo, Point winSize) {
        Path path = new Path();
        //x正半轴线
        path.moveTo(coo.x, coo.y);
        path.lineTo(winSize.x, coo.y);
        //x负半轴线
        path.moveTo(coo.x, coo.y);
        path.lineTo(coo.x - winSize.x, coo.y);
        //y负半轴线
        path.moveTo(coo.x, coo.y);
        path.lineTo(coo.x, coo.y - winSize.y);
        //y负半轴线
        path.moveTo(coo.x, coo.y);
        path.lineTo(coo.x, winSize.y);
        return path;
    }
}
```

##### 3.Utils

```
public class Utils {
    /**
     * 获得屏幕高度
     *
     * @param ctx 上下文
     * @param winSize 屏幕尺寸
     */
    public static void loadWinSize(Context ctx, Point winSize) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        winSize.x = outMetrics.widthPixels;
        winSize.y = outMetrics.heightPixels;
    }

}

```
