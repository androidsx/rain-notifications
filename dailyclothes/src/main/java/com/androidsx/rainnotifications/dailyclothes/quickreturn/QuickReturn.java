package com.androidsx.rainnotifications.dailyclothes.quickreturn;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;

public class QuickReturn {

    private static int mCachedVerticalScrollRange;
    private static int mQuickReturnHeight;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private static final int STATE_EXPANDED = 3;

    private static int mState = STATE_ONSCREEN;
    private static int mScrollY;
    private static int mMinRawY = 0;
    private static int rawY;

    private static boolean noAnimation = false;

    private static TranslateAnimation anim;

    private QuickReturn() {
        // Non-instantiate
    }

    public static void configureQuickReturn(final View mQuickReturnView, final QuickReturnListView mListView, final View mPlaceHolder) {
        mListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mQuickReturnHeight = mQuickReturnView.getHeight();
                        mListView.computeScrollY();
                        mCachedVerticalScrollRange = mListView.getListHeight();
                    }
                });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                mScrollY = 0;
                int translationY = 0;

                if (mListView.scrollYIsComputed()) {
                    mScrollY = mListView.getComputedScrollY();
                }

                rawY = mPlaceHolder.getTop()
                        - Math.min(
                        mCachedVerticalScrollRange
                                - mListView.getHeight(), mScrollY);

                switch (mState) {
                    case STATE_OFFSCREEN:
                        if (rawY <= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                        translationY = rawY;
                        break;

                    case STATE_ONSCREEN:
                        if (rawY < -mQuickReturnHeight) {
                            System.out.println("test3");
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING:

                        if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - mQuickReturnHeight;
                        }

                        else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        }

                        else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;

                        } else if (mQuickReturnView.getTranslationY() != 0
                                && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0,
                                    -mQuickReturnHeight, 0);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            mQuickReturnView.startAnimation(anim);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mMinRawY = rawY;
                                    mState = STATE_EXPANDED;
                                }
                            });
                        }
                        break;

                    case STATE_EXPANDED:
                        if (rawY < mMinRawY - 2 && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0, 0,
                                    -mQuickReturnHeight);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mState = STATE_OFFSCREEN;
                                }
                            });
                            mQuickReturnView.startAnimation(anim);
                        } else if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - mQuickReturnHeight;
                        }

                        else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        }

                        else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        } else {
                            mMinRawY = rawY;
                        }
                }
                /** this can be used if the build is below honeycomb **/
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                    anim = new TranslateAnimation(0, 0, translationY,
                            translationY);
                    anim.setFillAfter(true);
                    anim.setDuration(0);
                    mQuickReturnView.startAnimation(anim);
                } else {
                    mQuickReturnView.setTranslationY(translationY);
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }
}
