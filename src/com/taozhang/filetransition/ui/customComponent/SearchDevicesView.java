package com.taozhang.filetransition.ui.customComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.taozhang.filetransition.BuildConfig;
import com.taozhang.filetransition.R;

public class SearchDevicesView extends BaseView {

	

	public static final String TAG = "SearchDevicesView";
	public static final boolean D = BuildConfig.DEBUG;


	public boolean isSearching = true;
	private Bitmap bitmap;
	// private Bitmap bitmap1;
	private Paint paint;

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
		// offsetArgs = 0;
		invalidate();
	}

//	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		initBitmap(context);
//	}
	public SearchDevicesView(Context context) {
		super(context);
		initBitmap(context);
	}

	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBitmap(context);
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBitmap(context);
	}

	@SuppressLint("NewApi")
	private void initBitmap(Context context) {
		if(!isInEditMode()){
			if (bitmap == null) {
				bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.gplus_search_bg));
				
				paint = new Paint();
				
				paint.setStrokeWidth((float) 6.0);
				
				coordinateY = bitmap.getHeight() / 2;// ����
			}
		}
		
	}

	private int coordinateY;
	private int speed = 15;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if(!isInEditMode()){
			canvas.drawLine(0, coordinateY / 2, bitmap.getWidth(), coordinateY / 2,
					paint);
			canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
					getHeight() / 2 - bitmap.getHeight() / 2, null);
			
				
			if (isSearching) {
				// offsetArgs = offsetArgs + 3;
				coordinateY = coordinateY + speed;
				if (coordinateY >= bitmap.getHeight() - 480|| coordinateY <= 5) {
					speed = -(speed);
				}

			}
			if (isSearching){
				invalidate();// ���� �ػ棬�ﵽѭ��ɨ���Ŀ��
			}
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handleActionDownEvenet(event);
			return true;
		case MotionEvent.ACTION_MOVE:
			return true;
		case MotionEvent.ACTION_UP:
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void handleActionDownEvenet(MotionEvent event) {
		RectF rectF = new RectF(getWidth() / 2 - 60, getHeight() / 2 - 60,
				getWidth() / 2 + 60, getHeight() / 2 + 60);

		if (rectF.contains(event.getX(), event.getY())) {// �жϵ����λ���Ƿ�������
			if (D)
				Log.d(TAG, "click search device button");
			if (!isSearching()) {
				setSearching(true);
			} else {
				setSearching(false);
			}
		}
	}
	
	
}