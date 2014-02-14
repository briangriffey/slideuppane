package com.briangriffey.slideuppane;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

/**
 * A UI component that will slide up and down similar to the google maps application.
 * class created by @briangriffey
 * http://www.briangriffey.com
 */
public class SlideUpPane extends LinearLayout implements GestureDetector.OnGestureListener {

    private static final int OPEN_POSITION = 0;
    private static final int ANIMATION_TIME = 250;
    private static final float CLOSED_STOP = 0.0f;

    private View mContentView;
    private GestureDetector mGestureDetector;
    private Animator.AnimatorListener mAnimatorListener;

    private float mDownY;
    private float mStartTranslationY;
    private float mMaxTranslationY;
    private float mTopTranslationY;
    private boolean mScrolling;


    private static final int INVALID_POINTER = -1;
    private int mTouchSlop;
    private float mLastMotionX;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionY;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;

    private float[] mIntermediateStops;

    private float mCurrentStop;

    public SlideUpPane(Context context) {
        super(context);
        init();
    }

    public SlideUpPane(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideUpPane(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mGestureDetector = new GestureDetector(getContext(), this);

        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
        mContentView = (ViewGroup)getParent();
        if (mContentView != null) {
            setMeasuredDimension(getMeasuredWidth(), Math.max(getMeasuredHeight(), mContentView.getMeasuredHeight()));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mTopTranslationY = OPEN_POSITION - getTop();

        //you can't translate past the height of the view
        mMaxTranslationY = -getMeasuredHeight();
        //subtract out the part of the view that is already showing
        mMaxTranslationY += mContentView.getHeight() - getTop();
    }


    private void slideToTranslation(float translation) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationY", getTranslationY(), translation).setDuration(ANIMATION_TIME);
        animator.setInterpolator(new OvershootInterpolator(1.2f));
        if (mAnimatorListener != null)
            animator.addListener(mAnimatorListener);
        animator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

//        Log.v(TAG, "intercepting touch");
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the drag.

            mIsBeingDragged = false;
            mActivePointerId = INVALID_POINTER;

            slideToNearestStop();


            if (mScrolling) {
                mScrolling = false;
            }

            mGestureDetector.onTouchEvent(ev);
            return false;
        }


        switch (action) {

            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex) + getTop() + getTranslationY();

                final float yDiff = Math.abs(y - mInitialMotionY);


                if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff) {
                    mIsBeingDragged = true;

                    mLastMotionX = dx > 0 ? mInitialMotionX + mTouchSlop :
                            mInitialMotionX - mTouchSlop;
                    mLastMotionY = y;

                } else if (xDiff > mTouchSlop) {

                    mIsUnableToDrag = true;
                }

                break;
            }

            case MotionEvent.ACTION_DOWN: {
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = getTop() + getTranslationY() + ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsUnableToDrag = false;
                mIsBeingDragged = false;

                mGestureDetector.onTouchEvent(ev);

                break;
            }
        }

        if (mIsBeingDragged && !mIsUnableToDrag)
            mGestureDetector.onTouchEvent(ev);

        return mIsBeingDragged && !mIsUnableToDrag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            slideToNearestStop();
        }

        return true;
    }

    private void slideToNearestStop() {
        //we'll start by assuming that we are going to sink to the bottom of the screen
        float endingTranslation = 0;
        float translation = getTranslationY();
        mCurrentStop = 0;

        //start with how far we are from 0
        float closestDiff = Math.abs(getTranslationY());

        //see how far we are from the top
        float diffFromTop = Math.abs(mMaxTranslationY - translation);

        //check to see if hte top is closer
        if (diffFromTop < closestDiff) {
            closestDiff = diffFromTop;
            endingTranslation = mMaxTranslationY;
            mCurrentStop = 1;
        }

        if (mIntermediateStops != null) {
            for (float stop : mIntermediateStops) {
                float intermediatStopInPixels = stop * mMaxTranslationY;
                if (Math.abs(intermediatStopInPixels - getTranslationY()) < closestDiff) {
                    endingTranslation = intermediatStopInPixels;
                    mCurrentStop = stop;
                }
            }
        }

        slideToTranslation(endingTranslation);
    }

    public void setAnimatorListener(Animator.AnimatorListener mAnimatorListener) {
        this.mAnimatorListener = mAnimatorListener;
    }

    public boolean onDown(MotionEvent e) {
        mDownY = getTop() + e.getY() + getTranslationY();
        mStartTranslationY = getTranslationY();
        return true;

    }

    public void onShowPress(MotionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        mScrolling = true;

        float realY = getTop() + getTranslationY() + e2.getY();
        float diff = mDownY - realY;

        float maxTranslation = OPEN_POSITION - getTop();

        if (diff > 0 && getTranslationY() < mMaxTranslationY)
            return true;
        else
            setTranslationY(mStartTranslationY - diff);

        return true;
    }

    public void onLongPress(MotionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        float realY = getTop() + getTranslationY() + e2.getY();
//        float diff = mDownY - realY;
//
//        if(getTranslationY() < 0 && diff > 0)
//            slideDown();
//
//        else if(diff > 0)
//            slideUp();
//        else
//            slideDown();

        return true;
    }

    public void setIntermediateStops(float[] stops) {
        for (float stop : stops) {
            if (stop <= 0 || stop >= 1)
                throw new IllegalArgumentException("Your stops must be between 0 and 1");
        }

        mIntermediateStops = stops;
    }

    public float getCurrentStop() {
        return mCurrentStop;
    }

    public boolean isClosed() {
        return getCurrentStop() != CLOSED_STOP;
    }

    public void slideClosed() {
        mCurrentStop = CLOSED_STOP;
        slideToTranslation(CLOSED_STOP);
    }

    public void slideToFirstStop() {
        if(mIntermediateStops != null && mIntermediateStops.length > 0) {
            mCurrentStop = mIntermediateStops[0];
            float intermediateStopInPixels = mCurrentStop * mMaxTranslationY;
            slideToTranslation(intermediateStopInPixels);
        }
    }
}
