package com.taozhang.filetransition.ui;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.R.layout;
import com.taozhang.filetransition.R.menu;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class TransitionProgressActivity extends Activity {

	private WaterDropLoadingView water;
	private TextView tv_fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transition_progress);

		getComponent();
	}

	private void getComponent() {
		water = (WaterDropLoadingView) findViewById(R.id.waterDrop);
		tv_fileName = (TextView) findViewById(R.id.tv_waterDropAct_fileName);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transition_progress, menu);
		return true;
	}

}
