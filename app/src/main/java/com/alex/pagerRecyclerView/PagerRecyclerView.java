package com.alex.pagerRecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


public class PagerRecyclerView extends RecyclerView implements View.OnTouchListener {

    private static final String TAG = "PagerRecyclerView";

    private int offsetY = 0;
    private int offsetX = 0;

    private int startY = 0;
    private int startX = 0;


    private int mPageWidth;
    private boolean firstTouch = true;

    private ValueAnimator mAnimator = null;

    onPageChangeListener mOnPageChangeListener;

    private int mOrientation = LinearLayout.HORIZONTAL;
    private static final int AnimationDuration = 150;

    public int getPageWidth() {
        return mPageWidth;
    }

    public PagerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PagerRecyclerView,
                0, 0);

        float mPageMargin;
        try {
            mPageMargin = a.getDimension(R.styleable.PagerRecyclerView_page_margin, 0f);
        } finally {
            a.recycle();
        }

        mPageWidth = (int) (getContext().getResources().getDisplayMetrics().widthPixels - mPageMargin);

        this.setOnFlingListener(onFlingListener);
        this.addOnScrollListener(onScrollListener);
        this.setOnTouchListener(this);

        updateLayoutManger();
    }


    public void updateLayoutManger() {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                mOrientation = LinearLayout.VERTICAL;
            } else if (layoutManager.canScrollHorizontally()) {
                mOrientation = LinearLayout.HORIZONTAL;
            }
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            startX = 0;
            startY = 0;
            offsetX = 0;
            offsetY = 0;

        }

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    OnFlingListener onFlingListener = new OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {

            //获取开始滚动时所在页面的index
            int p = getStartPageIndex();

            //记录滚动开始和结束的位置
            int endPoint;
            int startPoint;

            //如果是垂直方向
            if (mOrientation == LinearLayout.VERTICAL) {
                startPoint = offsetY;

                if (velocityY < 0) {
                    p--;
                } else if (velocityY > 0) {
                    p++;
                }
                //更具不同的速度判断需要滚动的方向
                //注意，此处有一个技巧，就是当速度为0的时候就滚动会开始的页面，即实现页面复位
                endPoint = p * getHeight();

            } else {
                startPoint = offsetX;
                if (velocityX < 0) {
                    p--;
                } else if (velocityX > 0) {
                    p++;
                }
                endPoint = p * mPageWidth + mPageWidth / 2 - getWidth() / 2;

            }
            if (endPoint < 0) {
                endPoint = 0;
            }

            //使用动画处理滚动
            if (mAnimator == null) {
                mAnimator = new ValueAnimator().ofInt(startPoint, endPoint);

                mAnimator.setDuration(AnimationDuration);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int nowPoint = (int) animation.getAnimatedValue();

                        if (mOrientation == LinearLayout.VERTICAL) {
                            int dy = nowPoint - offsetY;
                            //这里通过RecyclerView的scrollBy方法实现滚动。
                            scrollBy(0, dy);
                        } else {
                            int dx = nowPoint - offsetX;
                            scrollBy(dx, 0);
                        }
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //回调监听
                        if (null != mOnPageChangeListener) {
                            mOnPageChangeListener.onPageChange(getPageIndex());
                        }
                        //修复双击item bug
                        stopScroll();
                        startY = offsetY;
                        startX = offsetX;
                    }
                });
            } else {
                mAnimator.cancel();
                mAnimator.setIntValues(startPoint, endPoint);
            }

            mAnimator.start();

            return true;
        }
    };


    OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            //newState==0表示滚动停止，此时需要处理回滚
            if (newState == 0) {
                boolean move;
                int vX = 0, vY = 0;
                if (mOrientation == LinearLayout.VERTICAL) {
                    int absY = Math.abs(offsetY - startY);
                    //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                    move = absY > recyclerView.getHeight() / 2;
                    vY = 0;

                    if (move) {
                        vY = offsetY - startY < 0 ? -1000 : 1000;
                    }

                } else {

                    int absX = Math.abs(offsetX - startX);
                    move = absX > mPageWidth / 2;
                    if (move) {
                        vX = offsetX - startX < 0 ? -1000 : 1000;
                    }

                }

                onFlingListener.onFling(vX, vY);

            }

        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            //滚动结束记录滚动的偏移量
            offsetY += dy;
            offsetX += dx;
        }
    };


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //手指按下的时候记录开始滚动的坐标
        if (firstTouch) {
            //第一次touch可能是ACTION_MOVE或ACTION_DOWN,所以使用这种方式判断
            firstTouch = false;
            startY = offsetY;
            startX = offsetX;

        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            firstTouch = true;
        }


        return false;
    }

    private int getPageIndex() {
        int p = 0;
        if (getHeight() == 0 || mPageWidth == 0) {
            return p;
        }
        if (mOrientation == LinearLayout.VERTICAL) {
            p = offsetY / getHeight();
        } else {
            p = offsetX / mPageWidth;
        }
        return p;
    }

    private int getStartPageIndex() {
        int p = 0;
        if (getHeight() == 0 || mPageWidth == 0) {
            //没有宽高无法处理
            return p;
        }
        if (mOrientation == LinearLayout.VERTICAL) {
            p = startY / getHeight();
        } else {

            int i = mPageWidth + getWidth() / 2;
            if (startX > 0 && startX < i) {
                p = 1;
            } else {
                p = (startX + getWidth() / 2) / mPageWidth;
            }

        }
        return p;
    }


    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public interface onPageChangeListener {
        void onPageChange(int index);
    }

}
