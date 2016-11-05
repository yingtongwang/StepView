package www.bejia.com.stepview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import www.bejia.com.stepview.R;

/**
 * Author   : Wangyingbao
 * <p>
 * Date     : 2016/11/5 9:22
 * <p>
 * Email    : 1551757778@qq.com
 * <p>
 * Describe :
 */

public class StepView extends View {

    //步数
    private String stepCount = "0";

    private int stepAllCcount = 10000;

    public int getStepAllCcount() {
        return stepAllCcount;
    }

    public void setStepAllCcount(int stepAllCcount) {
        this.stepAllCcount = stepAllCcount;
    }

    //步数+千卡
    private String step = String.format("%d米 | %d千卡", 0, 0);

    private int mRadius = 0; //---宽度 高度 半径

    public StepView(Context context) {
        this(context, null, 0);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.stepview);
        mRadius = typedArray.getInteger(R.styleable.stepview_radius, 200);
        stepAllCcount = typedArray.getInteger(R.styleable.stepview_stepcount, 1000);
    }

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }

    public void setStep(int range, int energy) {
        this.step = String.format("%d米 | %d千卡", range, energy);
    }

    public String getStep() {
        return step;
    }

    public void invalidateView() {
        this.invalidate();
    }

    /**
     * 設置自適應
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int textWidth = mRadius;
            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {

            int textHeight = mRadius;
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
        }
        setMeasuredDimension((int) width, (int) height);
    }

    /**
     * 獲取字體的準確長度
     *
     * @param paint
     * @param str
     * @return
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 画图
     *
     * @param canvas
     */
    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);


        int x_localion = getWidth() / 2;
        int y_localion = getHeight() / 2;

        /**
         * 画最外层的园
         */
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE); //空心
        paint.setStrokeWidth(1); //边框
        paint.setColor(getResources().getColor(R.color.colorAccent)); //字体颜色
        canvas.drawCircle(x_localion, y_localion, mRadius, paint); //画圆形

        /**
         * 圖形上的文字
         */
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(100);
        String degree = String.valueOf(stepCount);
        canvas.drawText(stepCount, x_localion - getTextWidth(paint, degree) / 2, y_localion + 50, paint);
//        paint.setTextSize(25);
//        String degree1 = String.valueOf(step);
//        canvas.drawText(step, x_localion - getTextWidth(paint, degree1) / 2, y_localion + 100, paint);

        /**
         * 画刻度
         */
        paint.setStrokeWidth(3);
        paint.setColor(getResources().getColor(R.color.colorPrimary));

        int rate = Integer.parseInt(this.stepCount) * 360 / stepAllCcount;
        for (int i = 0; i < 360; i++) {
            paint.setStrokeWidth(1);
            if (i < rate)
                paint.setColor(getResources().getColor(R.color.colorAccent));
            else
                paint.setColor(getResources().getColor(R.color.colorPrimary));
            canvas.drawLine(x_localion, y_localion - mRadius + 5, x_localion, y_localion - mRadius + 20 + 5, paint);
            canvas.rotate(1, x_localion, y_localion);
        }
        /**
         * 动态画圆形轨迹
         */
        MathDisc(canvas, x_localion, y_localion, paint, rate);
    }

    private void MathDisc(Canvas canvas, int x, int y, Paint paint, int rate) { //angle 角度
        float hudu = (float) ((2 * Math.PI / 360) * (angle));   //  360/8=45,即45度(这个随个人设置)
        float X = (float) (x + Math.sin(hudu) * mRadius);    //  r+5 是圆形中心的坐标X   即定位left 的值
        float Y = (float) (y - Math.cos(hudu) * mRadius);    //  r+5 是圆形中心的坐标Y   即定位top 的值
        canvas.drawCircle(X, Y, 5, paint);
        canvas.restore();
    }

    private float angle = 0;
    private ValueAnimator animator;

    public void startAnimation() {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, 1.0f);
            //动画时长，让进度条在CountDown时间内正好从0-360走完，
            animator.setDuration(2000);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());//匀速
            animator.setRepeatCount(-1);//表示不循环，-1表示无限循环
            //值从0-1.0F 的动画，动画时长为countdownTime，ValueAnimator没有跟任何的控件相关联，那也正好说明ValueAnimator只是对值做动画运算，而不是针对控件的，我们需要监听ValueAnimator的动画过程来自己对控件做操作
            //添加监听器,监听动画过程中值的实时变化(animation.getAnimatedValue()得到的值就是0-1.0)
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    /**
                     * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                     * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                     * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                     */
                    angle = 360 * (float) animation.getAnimatedValue();
                    invalidate();//实时刷新view，这样我们的进度条弧度就动起来了
                }
            });
            //开启动画
            animator.start();
        }
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }
}