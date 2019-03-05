package com.example.circlemenuview;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircleMenuView extends View {

    public CircleMenuView(Context context) {

        this(context, null);
    }

    public CircleMenuView(Context context, AttributeSet attr) {

        super(context, attr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        centerX = width / 2;
        centerY = height * 9 / 20;
        radius = width * 11 / 25;

        float arcLeft = centerX - radius;
        float arcTop = centerY - radius;
        float arcRight = centerX + radius;
        float arcBottom = centerY + radius;
        arcRectF.set(arcLeft, arcTop, arcRight, arcBottom);
    }

    @Override
    public void onDraw(Canvas canvas) {

        /*画布背景*/
        canvas.drawColor(Color.TRANSPARENT);

        float currPer = 0.0f;
        /*偏移角度*/
        float originalShiftD = 90f;
        String circleColor = "#aaffffff";
        int length = degrees.length;
        for (int i = 0; i < length; i++) {

            paintArc.setColor(Color.parseColor(colors[i % 2]));
            /*绘制扇形*/
            canvas.drawArc(arcRectF, currPer - originalShiftD, degrees[i], true, paintArc);
            /*下次的起始角度*/
            currPer += degrees[i];
        }

        /*画圆*/
        cirRadius = radius * 8 / 15;
        paintArc.setColor(Color.parseColor(circleColor));
        canvas.drawCircle(centerX, centerY, cirRadius, paintArc);

        /*绘制方向位图*/
        canvas.drawBitmap(bitmap, centerX - bitmap.getWidth() / 2, centerY - bitmap.getHeight() / 2, null);

        /*在圆环中心画文本*/
        drawTextInRingCenter(canvas, centerX, centerY, cirRadius + (radius - cirRadius) / 2);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                float downX = event.getX();
                float downY = event.getY();

                downIndex = getBelongsAreaIds(downX, downY, radius, cirRadius);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                float upX = event.getX();
                float upY = event.getY();

                int upIndex = getBelongsAreaIds(upX, upY, radius, cirRadius);

                if (upIndex >= 0 && downIndex >= 0 && upIndex == downIndex) {

                    onItemSelectedListener.onItemSelected(upIndex, upIndex);
                }
                break;
        }
        return true;
    }

    /*在圆环中心画文本*/
    private void drawTextInRingCenter(Canvas mCanvas, float cx, float cy, float ra) {

        float allAngles = 0f;
        for (int i = 0; i < texts.length; i++) {

            float angle = (float) ((degrees[i * 2] + 0.5f) / 2 * Math.PI / 180);
            float bx = cx + ra * (float) Math.sin(angle + allAngles);
            float by = cy - ra * (float) Math.cos(angle + allAngles);
            paintLabel.getTextBounds(texts[i], 0, texts[i].length(), rect);
            int textWidth = rect.width();
            if (i == 1 || i == 3) {

                mCanvas.drawText(OPEN, bx - textWidth / 3, by - 10, paintLabel);
                mCanvas.drawText(getTexts()[i], bx - textWidth / 2, by + 10, paintLabel);
            } else {

                mCanvas.drawText(getTexts()[i], bx - textWidth / 2, by, paintLabel);
            }

            allAngles += angle * 2;
        }
    }

    /*判断是否在区域内*/
    private boolean isInArea(float x, float y, float radius, float cirRadius) {

        /*计算点到圆心的距*/
        double distance = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2));
        return distance > cirRadius && distance < radius;
    }

    /*判断点所在区域*/
    private int getBelongsAreaIds(float x, float y, float radius, float cirRadius) {

        int index = -1;
        /*计算圆心与手指按下点两点间的斜率*/
        if (isInArea(x, y, radius, cirRadius)) {

            /*根据 x y所在项限确定角度*/
            double angleValue = 0f;

            /*在y轴上边*/
            if (y > centerY - radius && y < centerY) {

                int circleNum = (int) (askForTheAngle(x, y, radius)) / 360;
                angleValue = (askForTheAngle(x, y, radius)) - (circleNum * 360);
            }

            /*在y轴下边*/
            if (y > centerY && y < centerY + radius) {

                int circleNum = (int) (360d - askForTheAngle(x, y, radius)) / 360;
                angleValue = (360d - askForTheAngle(x, y, radius)) - (circleNum * 360);
            }

            angleValue = (angleValue - 90f + 360f) % 360f;

            int length = texts.length;
            if (length == 1) {

                index = 0;
            } else {
                for (int i = 0; i < length; i++) {

                    double totalAngles = 0d;
                    for (int m = 0; m < i; m++) {

                        totalAngles += degrees[m * 2] + 0.5f;
                    }

                    double entireAngles = 0d;
                    for (int n = 0; n <= i; n++) {

                        entireAngles += degrees[n * 2] + 0.5f;
                    }

                    if (angleValue > totalAngles && angleValue < entireAngles) {

                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    /*通过余玄定理求角度*/
    private double askForTheAngle(float x, float y, float radius) {

        /*圆半径*/
        float resDis02 = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        float desDis = (float) Math.sqrt(Math.pow(x - (centerX - radius), 2) + Math.pow(y - centerY, 2));
        double slope = (Math.pow(radius, 2) + Math.pow(resDis02, 2) - Math.pow(desDis, 2)) / (2 * radius * resDis02);
        return Math.acos(slope) * 180 / Math.PI;
    }

    public interface OnItemSelectedListener {

        void onItemSelected(int index, int flag);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {

        this.onItemSelectedListener = onItemSelectedListener;
    }

    /*初始化方法*/
    private void init(Context context) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.direction_navigation);

        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLabel.setColor(Color.BLACK);
        paintLabel.setTextSize(12);

        rect = new Rect();
        arcRectF = new RectF();

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);
    }

    private String[] getTexts() {

        return texts;
    }


    private int downIndex = -1;
    private String lockText = LOCK;

    /*圆心坐标*/
    private float centerX;
    private float centerY;
    /*大圆半径*/
    private float radius;
    /*小圆半径*/
    private float cirRadius;

    private Paint paintArc;
    private Paint paintLabel;

    private Rect rect;
    private RectF arcRectF;
    private Bitmap bitmap;
    private OnItemSelectedListener onItemSelectedListener;

    public static final String LOCK = "锁定";
    public static final String UNLOCK = "解锁";
    private static final String OPEN = "打开";

    private final String[] colors = {"#ffcccccc", "#ff888888"};
    private String[] texts = {lockText, "左移动", "关闭", "右移动", "查看列"};
    private final float[] degrees = {44.5f, 0.5f, 89.5f, 0.5f, 89.5f, 0.5f, 89.5f, 0.5f, 44.5f, 0.5f};
}
