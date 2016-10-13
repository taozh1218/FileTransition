package com.taozhang.filetransition.ui;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.runnable.SendMessageRunnable;

public class FileOperationActivity extends Activity {


    private float temp_x;
    private float temp_y;
    private int mX1;
    private int mY1;
    private ThreadPoolExecutor threadPool;
    private BlockingQueue<Runnable> queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operation);
        
        
        queue = new LinkedBlockingQueue<Runnable>(100);
        
        threadPool = new ThreadPoolExecutor(20, 40, 0L, TimeUnit.MILLISECONDS, queue);
        
        
        findViewById(R.id.iv_UpOperation).setOnClickListener(mOnClickListener);//向上
        ImageView imageView = (ImageView) findViewById(R.id.img_fileOperationAct);
        findViewById(R.id.iv_DownOperation).setOnClickListener(mOnClickListener);//向下

        imageView.setOnTouchListener(mOnTouchListener);//触摸板
        imageView.setOnClickListener(mOnClickListener);


    }
	/**
	 * 为了避免motion的up动作和click冲突,加上了这个标记
	 */
    private boolean isMoveting ;
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
        	
        	
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //                mBtn.setText("鎸変笅");
                temp_x = event.getX();
                temp_y = event.getY();

            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
            	// 移动不能太小，太小的话视为点击而不是move
            	if(Math.abs(temp_x - event.getX()) > 2 || Math.abs(temp_x - event.getX()) > 2) {
            		//移动标记设为true
            		isMoveting = true;
            		 //  mBtn.setText("鎸夐挳宸茬粡寮硅捣");
                    caculate(event.getX(), event.getY());
                    threadPool.execute(
                    		new SendMessageRunnable(new String[]{"move",mX1 +"",mY1 + ""}
                    		,FileListActivity.connect));
//                    try {
//						Thread.sleep(40);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
                    return true; //消费了事件，因为已经move了
            	}
            }
            
            if(event.getAction() == MotionEvent.ACTION_UP){
            	if(isMoveting){
            		// 移动标记设为false,结束了move
                	isMoveting = false;
            		return true;//视为moving的一部分
            	}else{
            		return false; //没有moving,则抬手事件是click的一部分,让onclick方法接收
            	}
            	
           
            }

            return false;//，因为不算move,没有消费事件，这样clickListener会接受到点击事件
        }


    };


    /**
     * 璁＄畻涓ゆ鐨勫樊鍊�
     *
     * @param x
     * @param y
     */
    private void caculate(float x, float y) {
        mX1 = (int) ((x - temp_x) * 0.1); //取0.2这样滑动的时候pc端的鼠标会操作的更好
        mY1 = (int) ((y - temp_y) * 0.1);

    }


    private long mLastTime;
    private long mCurTime;
    
    private static final String Click2 = "2click";
    private static final String Click = "click";
    private static final String UP = "up";
    private static final String DOWN = "down";
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	int id = v.getId();
        	switch (id) {
			case R.id.iv_UpOperation: //键盘上
				 threadPool.execute(new SendMessageRunnable(new String[]{UP},FileListActivity.connect));
				break;
			case R.id.iv_DownOperation: //键盘下
				 threadPool.execute(new SendMessageRunnable(new String[]{DOWN},FileListActivity.connect));
				break;
			case R.id.img_fileOperationAct:
				if(!isMoveting){ //如果不是在move,才视为点击
	        		  mLastTime = mCurTime;
	                  mCurTime = System.currentTimeMillis();
	                  if (mCurTime - mLastTime < 300) {
	                      Toast.makeText(getApplicationContext(),
	                              "双击", Toast.LENGTH_SHORT).show();
	                      threadPool.execute(new SendMessageRunnable(new String[]{Click2},FileListActivity.connect));

	                  } else {
	                  	threadPool.execute(new SendMessageRunnable(new String[]{Click},FileListActivity.connect));
	                  }
	        	}
				break;
			default:
				break;
			}
        }
    };
}
