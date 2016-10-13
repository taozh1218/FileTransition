package com.example.sinvoicedemo;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AnimotionActivity extends Activity implements OnClickListener{

	private WaterDropLoadingView waterDrop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.animotin_test_1);
		
		waterDrop = (WaterDropLoadingView) findViewById(R.id.waterDrop);
		waterDrop.setOnClickListener(this);
		
		findViewById(R.id.waterDrop_add).setOnClickListener(this);
		findViewById(R.id.waterDrop_less).setOnClickListener(this);
	}
	int speed = 0;
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.waterDrop:
			
		case R.id.waterDrop_add :
			speed ++;
			break;
		case R.id.waterDrop_less:
			speed --;
			break;
		default:
			break;
		}
	}
	
	
}
