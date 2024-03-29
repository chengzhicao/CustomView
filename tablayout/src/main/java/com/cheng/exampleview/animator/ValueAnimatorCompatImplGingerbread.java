package com.cheng.exampleview.animator;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;

/**
 * Created by ICAN on 2017/5/18.
 */

public class ValueAnimatorCompatImplGingerbread extends ValueAnimatorCompat.Impl {
    private static final int HANDLER_DELAY = 10;
    private static final int DEFAULT_DURATION = 200;

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private long mStartTime;
    private boolean mIsRunning;
    private float mAnimatedFraction;

    private final int[] mIntValues = new int[2];
    private final float[] mFloatValues = new float[2];

    private long mDuration = DEFAULT_DURATION;
    private Interpolator mInterpolator;
    private ArrayList<ValueAnimatorCompat.Impl.AnimatorListenerProxy> mListeners;
    private ArrayList<ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy> mUpdateListeners;

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            update();
        }
    };

    @Override
    public void start() {
        if (mIsRunning) {
            // If we're already running, ignore
            return;
        }
        if (mInterpolator == null) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        }
        mIsRunning = true;

        // Reset the animated fraction
        mAnimatedFraction = 0f;

        startInternal();
    }

    final void startInternal() {
        mStartTime = SystemClock.uptimeMillis();
        dispatchAnimationUpdate();
        dispatchAnimationStart();
        // Now start our animation ticker
        sHandler.postDelayed(mRunnable, HANDLER_DELAY);
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override
    public void addListener(ValueAnimatorCompat.Impl.AnimatorListenerProxy listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    @Override
    public void addUpdateListener(ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy updateListener) {
        if (mUpdateListeners == null) {
            mUpdateListeners = new ArrayList<>();
        }
        mUpdateListeners.add(updateListener);
    }

    @Override
    public void setIntValues(int from, int to) {
        mIntValues[0] = from;
        mIntValues[1] = to;
    }

    @Override
    public int getAnimatedIntValue() {
        return lerp(mIntValues[0], mIntValues[1], getAnimatedFraction());
    }

    @Override
    public void setFloatValues(float from, float to) {
        mFloatValues[0] = from;
        mFloatValues[1] = to;
    }

    @Override
    public float getAnimatedFloatValue() {
        return lerp(mFloatValues[0], mFloatValues[1], getAnimatedFraction());
    }

    static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    /**
     * Linear interpolation between {@code startValue} and {@code endValue} by {@code fraction}.
     */
    static float lerp(float startValue, float endValue, float fraction) {
        return startValue + (fraction * (endValue - startValue));
    }

    static int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (endValue - startValue));
    }

    @Override
    public void setDuration(long duration) {
        mDuration = duration;
    }

    @Override
    public void cancel() {
        mIsRunning = false;
        sHandler.removeCallbacks(mRunnable);

        dispatchAnimationCancel();
        dispatchAnimationEnd();
    }

    @Override
    public float getAnimatedFraction() {
        return mAnimatedFraction;
    }

    @Override
    public void end() {
        if (mIsRunning) {
            mIsRunning = false;
            sHandler.removeCallbacks(mRunnable);
            // Set our animated fraction to 1
            mAnimatedFraction = 1f;
            dispatchAnimationUpdate();
            dispatchAnimationEnd();
        }
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    final void update() {
        if (mIsRunning) {
            // Update the animated fraction
            final long elapsed = SystemClock.uptimeMillis() - mStartTime;
            final float linearFraction = constrain(elapsed / (float) mDuration, 0f, 1f);
            mAnimatedFraction = mInterpolator != null
                    ? mInterpolator.getInterpolation(linearFraction)
                    : linearFraction;

            // If we're running, dispatch to the update listeners
            dispatchAnimationUpdate();

            // Check to see if we've passed the animation duration
            if (SystemClock.uptimeMillis() >= (mStartTime + mDuration)) {
                mIsRunning = false;

                dispatchAnimationEnd();
            }
        }

        if (mIsRunning) {
            // If we're still running, post another delayed runnable
            sHandler.postDelayed(mRunnable, HANDLER_DELAY);
        }
    }

    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    private void dispatchAnimationUpdate() {
        if (mUpdateListeners != null) {
            for (int i = 0, count = mUpdateListeners.size(); i < count; i++) {
                mUpdateListeners.get(i).onAnimationUpdate();
            }
        }
    }

    private void dispatchAnimationStart() {
        if (mListeners != null) {
            for (int i = 0, count = mListeners.size(); i < count; i++) {
                mListeners.get(i).onAnimationStart();
            }
        }
    }

    private void dispatchAnimationCancel() {
        if (mListeners != null) {
            for (int i = 0, count = mListeners.size(); i < count; i++) {
                mListeners.get(i).onAnimationCancel();
            }
        }
    }

    private void dispatchAnimationEnd() {
        if (mListeners != null) {
            for (int i = 0, count = mListeners.size(); i < count; i++) {
                mListeners.get(i).onAnimationEnd();
            }
        }
    }
}
