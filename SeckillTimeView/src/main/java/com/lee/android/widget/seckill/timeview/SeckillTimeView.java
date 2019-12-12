package com.lee.android.widget.seckill.timeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.lee.android.widget.seckill.seckilltimeview.R;


public class SeckillTimeView extends View implements Runnable {

    private final static int COUNT_DOWN_INTERVAL = 1000;//倒数间隔,默认为1000
    private long endTime;//结束时间
    private OnFinishListener listener;
    private boolean isCountDowning = false;//是否在倒计时中
    private String hourDecade = "0";//时
    private String hourUnit = "0";
    private String minuteDecade = "0";
    private String minuteUnit = "0";//分
    private String secondDecade = "0";//秒
    private String secondUnit = "0";
    private long millisInFuture;

    private Paint bgPaint, textPaint;
    private RectF bgRect;
    private int bgSize;
    private int margin;
    private int numMargin;
    private float textY;
    private int bgColor, textColor;
    private int textSize;
    private Drawable colonDrawable;
    private Paint transparentPaint;
    public final static int SECONDS = 1;
    public final static int MINUTES = 60 * SECONDS;
    public final static int HOURS = 60 * MINUTES;

    //    public final static int DAYS = 24 * HOURS;
    public SeckillTimeView(Context context) {
        super(context);
        init(context, null);
    }

    public SeckillTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SeckillTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        bgColor = ContextCompat.getColor(context, R.color.primary);
        bgSize = SizeUtils.dp2px(context, 27);
        margin = SizeUtils.dp2px(context, 12);
        numMargin = SizeUtils.dp2px(context, 4);
        colonDrawable = ContextCompat.getDrawable(context, R.drawable.ic_time_colon);
        textColor = ContextCompat.getColor(context, R.color.white);
        textSize = SizeUtils.dp2px(context, 16);

        initAttrs(context, attrs);

        bgRect = new RectF();
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);

        transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        transparentPaint.setColor(Color.BLACK);
        transparentPaint.setAlpha(175);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(textColor);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textY = (bgSize - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    /**
     * 初始化属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeckillTimeView);
        bgSize = a.getDimensionPixelSize(R.styleable.SeckillTimeView_seckill_time_bg_size,
                bgSize);
        bgColor = a.getColor(R.styleable.SeckillTimeView_seckill_time_bg, bgColor);
        Drawable colonDrawable = a
                .getDrawable(R.styleable.SeckillTimeView_seckill_colon_drawable);
        textColor = a
                .getColor(R.styleable.SeckillTimeView_seckill_time_text_color, textColor);
        textSize = a
                .getDimensionPixelSize(R.styleable.SeckillTimeView_seckill_time_text_size,
                        textSize);
        margin = a
                .getDimensionPixelSize(R.styleable.SeckillTimeView_seckill_time_margin, margin);
        a.recycle();
        if (colonDrawable != null) {
            this.colonDrawable = colonDrawable;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int marginleft = 0;
        drawTime(canvas, marginleft, hourDecade);
        marginleft = bgSize + numMargin;
        drawTime(canvas, marginleft, hourUnit);
        marginleft += bgSize + margin / 2;
        drawColon(canvas, marginleft);//冒号1
        marginleft += margin / 2;
        drawTime(canvas, marginleft, minuteDecade);//分
        marginleft += bgSize + numMargin;
        drawTime(canvas, marginleft, minuteUnit);//分
        marginleft += bgSize + margin / 2;
        drawColon(canvas, marginleft);//冒号2
        marginleft += margin / 2;
        drawTime(canvas, marginleft, secondDecade);//秒
        marginleft += bgSize + numMargin;
        drawTime(canvas, marginleft, secondUnit);//秒
    }

    /**
     * 画时间
     *
     * @param canvas 画布
     * @param left   左边位置
     * @param text   文字
     */
    private void drawTime(Canvas canvas, int left, String text) {
        bgRect.left = left;
        bgRect.top = 0;
        bgRect.right = left + bgSize;
        bgRect.bottom = bgSize;
        canvas.drawRoundRect(bgRect, 10, 10, bgPaint);
        canvas.drawText(text, bgRect.centerX(), textY, textPaint);
        bgRect.bottom = bgSize / 2;
        canvas.drawRoundRect(bgRect, 10, 10, transparentPaint);
    }

    /**
     * 画冒号
     *
     * @param canvas  画布
     * @param centerX 左边位置
     */
    private void drawColon(Canvas canvas, int centerX) {
        int dw = colonDrawable.getIntrinsicWidth();
        int dh = colonDrawable.getIntrinsicHeight();
        int left = centerX - dw / 2;
        int top = bgSize / 2 - dh / 2;
        int right = left + dw;
        int bottom = top + dh;
        colonDrawable.setBounds(left, top, right, bottom);
        colonDrawable.draw(canvas);
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        millisInFuture = endTime - System.currentTimeMillis();
        start();
    }

    /**
     * 开始倒计时
     */
    public void start() {
        isCountDowning = true;
        if (millisInFuture > 0) {
            int allSec = (int) (millisInFuture / 1000);
            int hour = allSec / HOURS;
            int min = (allSec - hour * HOURS) / MINUTES;
            int sec = (allSec - hour * HOURS - min * MINUTES);
            hourDecade = hour < 10 ? "0" : hour / 10 + "";
            hourUnit = hour < 10 ? "" + hour : hour % 10 + "";
            minuteDecade = min < 10 ? "0" : min / 10 + "";
            minuteUnit = min < 10 ? "" + min : min % 10 + "";
            secondDecade = sec < 10 ? "0" : sec / 10 + "";
            secondUnit = sec < 10 ? "" + sec : sec % 10 + "";
            removeCallbacks(this);
            postDelayed(this, COUNT_DOWN_INTERVAL);
        } else {
            hourDecade = "0";//时
            hourUnit = "0";
            minuteDecade = "0";
            minuteUnit = "0";//分
            secondDecade = "0";//秒
            secondUnit = "0";
            if (listener != null && endTime > 0) {
                listener.onCountFinish(this);
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        width = bgSize * 6 + numMargin * 3 + margin * 2;
        setMeasuredDimension(width, bgSize);
    }

    /**
     * 停止倒计时
     */
    public void stop() {
        isCountDowning = false;
        removeCallbacks(this);
    }

    /**
     * 是否在倒计时中
     *
     * @return true:在倒计时中，false，不在倒计时
     */
    public boolean isCountDowning() {
        return isCountDowning;
    }

    @Override
    public void run() {
        if (!isCountDowning) {
            return;
        }
        if (millisInFuture <= 0) {
            return;
        }
        millisInFuture -= COUNT_DOWN_INTERVAL;
        post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        millisInFuture = endTime - System.currentTimeMillis();
        start();
    }

    public interface OnFinishListener {

        void onCountFinish(View view);
    }

    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }
}
