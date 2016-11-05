# StepView
计数功能，仿照小米运动的动画。
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

    /**
     * 旋转画圆，rate...根据拆分的比例进行画圆
     *
     * @param canvas
     * @param x
     * @param y
     * @param paint
     * @param rate
     */
    private void MathDisc(Canvas canvas, int x, int y, Paint paint, int rate) { //angle 角度
        float L = (float) ((2 * Math.PI / 360) * (angle));   //  360/8=45,即45度(这个随个人设置)
        float X = (float) (x + Math.sin(L) * mRadius);    //  r+5 是圆形中心的坐标X   即定位left 的值
        float Y = (float) (y - Math.cos(L) * mRadius);    //  r+5 是圆形中心的坐标Y   即定位top 的值
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
                    angle = 360 * (float) animation.getAnimatedValue();
                    invalidate();//实时刷新view，这样我们的进度条弧度就动起来了
                }
            });
            //开启动画
            animator.start();
        }
    }

    #计数
    /**
     * Receives sensor updates and alerts a StepListener when a step has been detected.
     */
    public class SimpleStepDetector {

        private static final int ACCEL_RING_SIZE = 50;
        private static final int VEL_RING_SIZE = 10;
        private static final float STEP_THRESHOLD = 4f;
        private static final int STEP_DELAY_NS = 250000000;

        private int accelRingCounter = 0;
        private float[] accelRingX = new float[ACCEL_RING_SIZE];
        private float[] accelRingY = new float[ACCEL_RING_SIZE];
        private float[] accelRingZ = new float[ACCEL_RING_SIZE];
        private int velRingCounter = 0;
        private float[] velRing = new float[VEL_RING_SIZE];
        private long lastStepTimeNs = 0;
        private float oldVelocityEstimate = 0;

        private StepListener listener;

        public void registerListener(StepListener listener) {
            this.listener = listener;
        }

        /**
         * Accepts updates from the accelerometer.
         */
        public void updateAccel(long timeNs, float x, float y, float z) {
            float[] currentAccel = new float[3];
            currentAccel[0] = x;
            currentAccel[1] = y;
            currentAccel[2] = z;

            // First step is to update our guess of where the global z vector is.
            accelRingCounter++;
            accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
            accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
            accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

            float[] worldZ = new float[3];
            worldZ[0] = SensorFusionMath.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
            worldZ[1] = SensorFusionMath.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
            worldZ[2] = SensorFusionMath.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

            float normalization_factor = SensorFusionMath.norm(worldZ);

            worldZ[0] = worldZ[0] / normalization_factor;
            worldZ[1] = worldZ[1] / normalization_factor;
            worldZ[2] = worldZ[2] / normalization_factor;

            // Next step is to figure out the component of the current acceleration
            // in the direction of world_z and subtract gravity's contribution
            float currentZ = SensorFusionMath.dot(worldZ, currentAccel) - normalization_factor;
            velRingCounter++;
            velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

            float velocityEstimate = SensorFusionMath.sum(velRing);

            if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                    && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
                listener.step(timeNs);
                lastStepTimeNs = timeNs;
            }
            oldVelocityEstimate = velocityEstimate;
        }
    }
