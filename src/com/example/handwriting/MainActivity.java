package com.example.handwriting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button mUnDo;
	private Button mReDo;
	private HandWriteView mHandView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	private void init() {
		
		mUnDo = (Button) findViewById(R.id.undo_path);
		mReDo = (Button) findViewById(R.id.redo_path);
		mUnDo.setOnClickListener(this);
		mReDo.setOnClickListener(this);
		
		mHandView = (HandWriteView) findViewById(R.id.hand_view);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.undo_path:
			if (mHandView.isCanUndo()) {
				mHandView.undo();
			} else {
				Toast.makeText(this, "can not undo", Toast.LENGTH_SHORT).show();
			}
			
			break;
			
		case R.id.redo_path:
			if (mHandView.isCanRedo()) {
				mHandView.redo();
			} else {
				Toast.makeText(this, "can not redo", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}
}
