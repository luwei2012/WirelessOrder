package com.wirelessorder.widget;
  

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

 
public class LoadingDialog extends ProgressDialog {

	private TextView loadtext;

	public LoadingDialog(Context context) {
	 super(context);
	}

	public void setLoadText(String content){
		loadtext.setText(content);
	}
}