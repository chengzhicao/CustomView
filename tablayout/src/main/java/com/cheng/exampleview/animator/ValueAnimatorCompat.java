package com.cheng.exampleview.animator;

import android.support.annotation.NonNull;
import android.view.animation.Interpolator;

/**
 * Created by ICAN on 2017/5/18.
 */

public class ValueAnimatorCompat {
    public interface AnimatorUpdateListener {
        /**
         * <p>Notifies the occurrence of another frame of the animation.</p>
         *
         * @param animator The animation which was repeated.
         */
        void onAnimationUpdate(ValueAnimatorCompat animator);
    }

    /**
     * An animation listener receives notifications from an animation.
     * Notifications indicate animation related events, such as the end or the
     * repetition of the animation.
     */
    public interface AnimatorListener {
        /**
         * <p>Notifies the start of the animation.</p>
         *
         * @param animator The started animation.
         */
        void onAnimationStart(ValueAnimatorCompat animator);

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animator The animation which reached its end.
         */
        void onAnimationEnd(ValueAnimatorCompat animator);

        /**
         * <p>Notifies the cancellation of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animator The animation which was canceled.
         */
        void onAnimationCancel(ValueAnimatorCompat animator);
    }

    public static class AnimatorListenerAdapter implements ValueAnimatorCompat.AnimatorListener {
        @Override
        public void onAnimationStart(ValueAnimatorCompat animator) {
        }

        @Override
        public void onAnimationEnd(ValueAnimatorCompat animator) {
        }

        @Override
        public void onAnimationCancel(ValueAnimatorCompat animator) {
        }
    }

    public interface Creator {
        @NonNull
        ValueAnimatorCompat createAnimator();
    }

    static abstract class Impl {
        interface AnimatorUpdateListenerProxy {
            void onAnimationUpdate();
        }

        interface AnimatorListenerProxy {
            void onAnimationStart();

            void onAnimationEnd();

            void onAnimationCancel();
        }

        abstract void start();

        abstract boolean isRunning();

        abstract void setInterpolator(Interpolator interpolator);

        abstract void addListener(ValueAnimatorCompat.Impl.AnimatorListenerProxy listener);

        abstract void addUpdateListener(ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy updateListener);

        abstract void setIntValues(int from, int to);

        abstract int getAnimatedIntValue();

        abstract void setFloatValues(float from, float to);

        abstract float getAnimatedFloatValue();

        abstract void setDuration(long duration);

        abstract void cancel();

        abstract float getAnimatedFraction();

        abstract void end();

        abstract long getDuration();
    }

    private final ValueAnimatorCompat.Impl mImpl;

    public ValueAnimatorCompat(ValueAnimatorCompat.Impl impl) {
        mImpl = impl;
    }

    public void start() {
        mImpl.start();
    }

    public boolean isRunning() {
        return mImpl.isRunning();
    }

    public void setInterpolator(Interpolator interpolator) {
        mImpl.setInterpolator(interpolator);
    }

    public void addUpdateListener(final ValueAnimatorCompat.AnimatorUpdateListener updateListener) {
        if (updateListener != null) {
            mImpl.addUpdateListener(new ValueAnimatorCompat.Impl.AnimatorUpdateListenerProxy() {
                @Override
                public void onAnimationUpdate() {
                    updateListener.onAnimationUpdate(ValueAnimatorCompat.this);
                }
            });
        } else {
            mImpl.addUpdateListener(null);
        }
    }

    public void addListener(final ValueAnimatorCompat.AnimatorListener listener) {
        if (listener != null) {
            mImpl.addListener(new ValueAnimatorCompat.Impl.AnimatorListenerProxy() {
                @Override
                public void onAnimationStart() {
                    listener.onAnimationStart(ValueAnimatorCompat.this);
                }

                @Override
                public void onAnimationEnd() {
                    listener.onAnimationEnd(ValueAnimatorCompat.this);
                }

                @Override
                public void onAnimationCancel() {
                    listener.onAnimationCancel(ValueAnimatorCompat.this);
                }
            });
        } else {
            mImpl.addListener(null);
        }
    }

    public void setIntValues(int from, int to) {
        mImpl.setIntValues(from, to);
    }

    public int getAnimatedIntValue() {
        return mImpl.getAnimatedIntValue();
    }

    public void setFloatValues(float from, float to) {
        mImpl.setFloatValues(from, to);
    }

    public float getAnimatedFloatValue() {
        return mImpl.getAnimatedFloatValue();
    }

    public void setDuration(long duration) {
        mImpl.setDuration(duration);
    }

    public void cancel() {
        mImpl.cancel();
    }

    public float getAnimatedFraction() {
        return mImpl.getAnimatedFraction();
    }

    public void end() {
        mImpl.end();
    }

    public long getDuration() {
        return mImpl.getDuration();
    }
}
