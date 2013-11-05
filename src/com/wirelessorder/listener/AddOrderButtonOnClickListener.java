package com.wirelessorder.listener;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.wirelessorder.fragment.AddOrderAdjustmentDialogFragment;

public class AddOrderButtonOnClickListener implements OnClickListener {
	private String m_currentSelectedDishRemark;
	private int m_currentSelectedDishIdString;
	private String m_currentSelectedDishNumber = new String("1");

	public String getCurrentSelectedDishNumber() {
		return m_currentSelectedDishNumber;
	}

	public void setCurrentSelectedDishNumber(String m_currentSelectedDishNumber) {
		this.m_currentSelectedDishNumber = m_currentSelectedDishNumber;
	}

	public int getCurrentSelectedDishId() {
		return m_currentSelectedDishIdString;
	}

	public void setCurrentSelectedDishId(int m_currentSelectedDishIdString) {
		this.m_currentSelectedDishIdString = m_currentSelectedDishIdString;
	}

	public AddOrderButtonOnClickListener() {
		super();
	}

	public void onClick(View v) {
		FragmentTransaction ft = ((Activity) v.getContext())
				.getFragmentManager().beginTransaction();
		Fragment prev = ((Activity) v.getContext()).getFragmentManager()
				.findFragmentByTag("adjustment");
		if (prev != null) {
			ft.remove(prev);
		} 

		// Create and show the dialog.
		DialogFragment newFragment = AddOrderAdjustmentDialogFragment
				.newInstance(this, m_currentSelectedDishIdString);
		newFragment.show(ft, "adjustment");
	}

	public void doAdd(String v_number, String v_remark) {
		m_currentSelectedDishNumber = v_number;

	}

	public void setCurrentSelectedDishRemark(String m_currentSelectedDishRemark) {
		this.m_currentSelectedDishRemark = m_currentSelectedDishRemark;
	}

	public String getCurrentSelectedDishRemark() {
		return m_currentSelectedDishRemark;
	}

	public void doCancel() {
		// TODO Auto-generated method stub

	}

}
