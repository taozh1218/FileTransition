package com.taozhang.filetransition.ui;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.R.layout;
import com.taozhang.filetransition.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		getComponent();
	}

	private void getComponent() {
		TextView tv_transition = (TextView) findViewById(R.id.tv_help_first);
		TextView tv_computer = (TextView) findViewById(R.id.tv_help_second);
		TextView tv_history = (TextView) findViewById(R.id.tv_help_third);

		tv_transition
				.setText("手机互传页面，有两个非常引人注目的按钮，\"我要发送\"和\"我要接收\"。\n\n当您需要发送文件给他人时，\n1.请点击发送按钮，然后就会跳转到文件选择的页面，进行选择需要发送的文件；\n2.选择完成后，请点击右上角的\"完成\"按钮；\n3.接下来，会发送一段声波，为了方便识别，请把手机靠近接受者，接下来就只需等待文件的传输完成。\n\n当您需要接收文件时，\n1.请点击我要接收按钮");

		tv_computer.setText("");

		tv_history
				.setText("历史文件页面显示了您接收到的文件，并且进行了简单的分类。\n在这个页面，您只需长按目标文件，可以对历史文件进行简单的文件操作，譬如，复制、粘贴、剪切等。\n此外，对目标文件进行左滑，可以实现删除历史文件的功能（注意，会同时删除本地文件，请谨慎操作！）");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

}
