package com.taozhang.filetransition.ui.customComponent;


import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.taozhang.filetransition.R;


/**
 * Created by 10129302 on 15-2-12. 鏉╂瑦妲告稉锟介嚋缁鎶�弨顖欑帛鐎规繂锛愬▔銏℃暜娴犳娈戝▔銏㈡睏閺佸牊鐏夌敮鍐ㄧ湰,
 * 鐠囥儱绔风仦锟借厬姒涙顓诲ǎ璇插娴滃棔绗夐崣顖濐潌閻ㄥ嫬娓捐ぐ銏㈡畱鐟欏棗娴�閸氼垰濮╅崝銊ф暰閺冩湹绱伴崥顖氬З缂傗晜鏂侀妴渚�杹閼瑰弶绗庨崣妯哄З閻㈣濞囧妞鹃獓閻㈢喐灏濈痪瑙勬櫏閺嬶拷
 * 鏉╂瑤绨洪崝銊ф暰闁姤妲搁弮鐘绘瀵邦亞骞嗛惃锟介獮鏈电瑬濮ｅ繋閲淰iew閻ㄥ嫬濮╅悽璁崇闂傛挳鍏橀張澶嬫闂傛挳妫块梾鏃撶礉鏉╂瑤绨洪弮鍫曟？闂傛挳娈х亸鍙樼窗鐎佃壈鍤х憴鍡楁禈閺堝銇囬張澶婄毈閿涳拷 * <p/>
 * 娴犲氦锟芥禍褏鏁撳▔銏㈡睏閻ㄥ嫭鏅ラ弸锟� */
public class RippleOutLayout extends RelativeLayout
{

    private static final int DEFAULT_RIPPLE_COUNT = 6;
    private static final int DEFAULT_DURATION_TIME = 4 * 1000;
    private static final float DEFAULT_SCALE = 5.0f;
    private static final int DEFAULT_RIPPLE_COLOR = Color.rgb(0x33, 0x99, 0xcc);
    private static final int DEFAULT_RADIUS = 100;
    private static final int DEFAULT_STROKE_WIDTH = 0;

    private int mRippleColor = DEFAULT_RIPPLE_COLOR;
    private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private float mRippleRadius = DEFAULT_RADIUS;
    private int mAnimDuration = DEFAULT_DURATION_TIME;
    private int mRippleViewNums = DEFAULT_RIPPLE_COUNT;
    private float mRippleScale = DEFAULT_SCALE;

    private boolean animationRunning = false;
    private int mAnimDelay;

    private Paint mPaint;

    /**
     * 閸斻劎鏁鹃梿锟�    */
    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private ArrayList<Animator> mAnimatorList = new ArrayList<Animator>();

    private LayoutParams mRippleViewParams;

    public RippleOutLayout(Context context)
    {
        super(context);
        init(context, null);
    }

    public RippleOutLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleOutLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        if (isInEditMode())
            return;

        if (attrs != null)
        {
            initTypedArray(context, attrs);
        }
        initPaint();
        initRippleViewLayoutParams();
        generateRippleViews();
    }

    private void initTypedArray(Context context, AttributeSet attrs)
    {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.RippleOutLayout);

        mRippleColor = typedArray.getColor(R.styleable.RippleOutLayout_rippleout_color,
            DEFAULT_RIPPLE_COLOR);
        mStrokeWidth = typedArray.getDimension(
            R.styleable.RippleOutLayout_rippleout_stroke_width, DEFAULT_STROKE_WIDTH);
        mRippleRadius = typedArray.getDimension(
            R.styleable.RippleOutLayout_rippleout_radius, DEFAULT_RADIUS);
        mAnimDuration = typedArray.getInt(R.styleable.RippleOutLayout_rippleout_duration,
            DEFAULT_DURATION_TIME);
        mRippleViewNums = typedArray.getInt(
            R.styleable.RippleOutLayout_rippleout_rippleNums, DEFAULT_RIPPLE_COUNT);
        mRippleScale = typedArray.getFloat(R.styleable.RippleOutLayout_rippleout_scale,
            DEFAULT_SCALE);

        typedArray.recycle();
    }

    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth((float)3.0); 
        mPaint.setColor(mRippleColor);
    }

    private void initRippleViewLayoutParams()
    {
        //RippleView閻ㄥ嫬銇囩亸蹇庤礋(閸楀﹤绶�閻㈣崵鐟�閻ㄥ嫪琚遍崐锟�     
    	int rippleSide = (int) (2 * (mRippleRadius + mStrokeWidth));
        mRippleViewParams = new LayoutParams(rippleSide, rippleSide);
        mRippleViewParams.addRule(CENTER_IN_PARENT, TRUE);  //鐏炲懍鑵戦弰鍓с仛
    }

    /**
     * 閸掓繂顫愰崠鏈ppleViews閿涘苯鑻熸稉鏂跨殺閸斻劎鏁剧拋鍓х枂閸掔櫅ippleView娑擄拷娴ｅじ绠ｉ崷鈻� y娑撳秵鏌囬幍鈺併亣,楠炴湹绗栭懗灞炬珯閼规煡锟藉〒鎰窗閸栵拷     */
    private void generateRippleViews()
    {
        calculateAnimDelay();
        initAnimSet();
        //濞ｈ濮濺ippleView
        for (int i = 0; i < mRippleViewNums; i++)
        {
            RippleView rippleView = new RippleView(getContext());
            addView(rippleView, mRippleViewParams);
            //濞ｈ濮為崝銊ф暰
            addAnimToRippleView(rippleView, i);
        }

        // x, y, alpha閸斻劎鏁炬稉锟芥健閹笛嗩攽
        mAnimatorSet.playTogether(mAnimatorList);
    }

    /**
     * 娑撶儤鐦℃稉鐚俰ppleView濞ｈ濮為崝銊ф暰閺佸牊鐏�楠炴湹绗栫拋鍓х枂閸斻劎鏁惧鑸垫,濮ｅ繋閲滅憴鍡楁禈閸氼垰濮╅崝銊ф暰閻ㄥ嫭妞傞梻缈犵瑝閸氾拷鐏忓彉绱版禍褏鏁撳▔銏㈡睏
     *
     * @param rippleView
     * @param i
     */
    private void addAnimToRippleView(RippleView rippleView, int i)
    {
        // x鏉炲娈戠紓鈺傛杹閸斻劎鏁�        
    	ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "scaleX",
            1.0f, mRippleScale);
        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
        scaleXAnimator.setStartDelay(i * mAnimDelay);
        scaleXAnimator.setDuration(mAnimDuration);
        mAnimatorList.add(scaleXAnimator);

        // y鏉炲娈戠紓鈺傛杹閸斻劎鏁�      
        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView,
            "scaleY", 1.0f, mRippleScale);
        scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setStartDelay(i * mAnimDelay);
        scaleYAnimator.setDuration(mAnimDuration);
        mAnimatorList.add(scaleYAnimator);

        // 妫版粏澹婇惃鍒焞pha濞撴劕褰夐崝銊ф暰
        final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "alpha",
            1.0f, 0f);
        alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator.setDuration(mAnimDuration);
        alphaAnimator.setStartDelay(i * mAnimDelay);
        mAnimatorList.add(alphaAnimator);
    }

    private void initAnimSet()
    {
        mAnimatorSet.setDuration(mAnimDuration);
        mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * 鐠侊紕鐣诲В蹇庨嚋RippleView娑斿妫块惃鍕З閻㈢粯妞傞梻鎾？闂呮棑绱濇禒搴わ拷娴溠呮晸濞夈垻姹楅弫鍫熺亯
     */
    private void calculateAnimDelay()
    {
        mAnimDelay = mAnimDuration / mRippleViewNums;
    }

    public void startRippleAnimation()
    {
        if (!isRippleAnimationRunning())
        {
            animationRunning = true;
            mAnimatorSet.start();
            makeRippleViewsVisible();
        }
    }

    public void stopRippleAnimation()
    {
        if (isRippleAnimationRunning())
        {
            mAnimatorSet.end();
            animationRunning = false;
        }
    }

    private void makeRippleViewsVisible()
    {
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View childView = this.getChildAt(i);
            if (childView instanceof RippleView)
            {
                childView.setVisibility(VISIBLE);
            }
        }
    }

    public boolean isRippleAnimationRunning()
    {
        return animationRunning;
    }

    /**
     * RippleView娴溠呮晸濞夈垻姹楅弫鍫熺亯, 姒涙顓绘稉宥呭讲鐟欙拷瑜版挸鎯庨崝銊ュЗ閻㈢粯妞傞幍宥堫啎缂冾喕璐熼崣顖濐潌
     */
    private class RippleView extends View
    {

        public RippleView(Context context)
        {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius - mStrokeWidth, mPaint);
        }
    }

}
