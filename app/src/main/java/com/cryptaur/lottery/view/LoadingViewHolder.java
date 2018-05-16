package com.cryptaur.lottery.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.cryptaur.lottery.R;

import java.util.ArrayList;
import java.util.List;

public class LoadingViewHolder extends RecyclerView.ViewHolder {

    private static final float SPEED_DP_S = 320.0f;

    private final View[] balls = new View[6];
    private final DetectVisibleConstraintLayout mView;
    private final View fix;
    private boolean viewVisible = false;
    private int width;

    private AnimatorSet animator;
    private boolean runAnimation = true;
    private boolean animationCancelled;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        mView = (DetectVisibleConstraintLayout) itemView;
        fix = mView.findViewById(R.id.fix);
        balls[0] = mView.findViewById(R.id.ball0);
        balls[1] = mView.findViewById(R.id.ball1);
        balls[2] = mView.findViewById(R.id.ball2);
        balls[3] = mView.findViewById(R.id.ball3);
        balls[4] = mView.findViewById(R.id.ball4);
        balls[5] = mView.findViewById(R.id.ball5);

        mView.setVisibilityAggregatedListener(this::onViewVisibilityAggregated);
        mView.addOnLayoutChangeListener(this::onViewLayoutChange);
    }

    public static LoadingViewHolder create(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_loading, parent, false);
        return new LoadingViewHolder(view);
    }


    public void onViewVisibilityAggregated(boolean isVisible) {
        viewVisible = isVisible;
        updateAnimationState();
    }

    public void setRunAnimation(boolean runAnimation) {
        this.runAnimation = runAnimation;
        updateAnimationState();
    }

    private void updateAnimationState() {
        if (runAnimation & viewVisible && width != 0)
            startAnimation();
        else
            stopAnimation();
    }

    private void startAnimation() {
        stopAnimation();
        long start = 0;
        float speedPxS = SPEED_DP_S * mView.getResources().getDisplayMetrics().density;
        List<Animator> animators = new ArrayList<>();

        LinearInterpolator linear = new LinearInterpolator();

        for (int i = balls.length - 1; i >= 0; i--) {
            View ball = balls[i];
            ball.setTranslationX(0);
            int ballWidth = ball.getWidth();
            int distance = ball.getLeft() + ballWidth;
            long duration = (long) (distance * 1000 / speedPxS);
            float angle = (float) (distance / ballWidth / Math.PI * 360);

            ball.setTranslationX(-distance);

            animators.add(translateAnimator(ball, -distance, 0, duration, start, linear));
            animators.add(rollAnimator(ball, -angle, 0, duration, start, linear));

            start += duration;
        }

        for (int i = balls.length - 1; i >= 0; i--) {
            View ball = balls[i];
            int ballWidth = ball.getWidth();
            int distance = width - ball.getLeft() + 2;
            long duration = (long) (distance * 1000 / speedPxS);
            float angle = (float) (distance / ballWidth / Math.PI * 360);

            animators.add(translateAnimator(ball, 0, distance, duration, start, null));
            animators.add(rollAnimator(ball, 0, angle, duration, start, null));

            start += duration;
        }

        this.animator = new AnimatorSet();
        this.animator.playTogether(animators);
        this.animator.addListener(new AnimatorListenerAdapter() {


            @Override
            public void onAnimationStart(Animator animation) {
                animationCancelled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animationCancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animationCancelled) {
                    animation.start();
                }
            }
        });
        this.animator.start();
    }

    private Animator translateAnimator(View ball, int from, int to, long duration, long startDelay, Interpolator interpolator) {
        ObjectAnimator translate = ObjectAnimator.ofFloat(ball, "translationX", from, to);
        translate.setDuration(duration);
        translate.setStartDelay(startDelay);
        if (interpolator != null)
            translate.setInterpolator(interpolator);
        return translate;
    }

    private Animator rollAnimator(View ball, float from, float to, long duration, long start, Interpolator interpolator) {
        ObjectAnimator roll = ObjectAnimator.ofFloat(ball, "rotation", from, to);
        roll.setDuration(duration);
        roll.setStartDelay(start);
        if (interpolator != null)
            roll.setInterpolator(interpolator);
        return roll;
    }

    private void stopAnimation() {
        if (animator != null) {
            for (Animator animator1 : animator.getChildAnimations()) {
                animator1.setTarget(fix);
                animator1.cancel();
            }
            animator.cancel();
            animationCancelled = true;
            animator = null;
        }
    }


    public void onViewLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        width = right - left;
        updateAnimationState();
    }
}
