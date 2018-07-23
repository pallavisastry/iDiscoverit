package com.sjsu.cmpe295B.idiscoverit.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
//import android.widget.EditText;

public class CustomAudiotronNameDialog extends DialogFragment implements DialogInterface.OnClickListener {
	
//	private EditText customAudiotronTitle;
	private CustomAudiotronNameDialogListener listener;
	
	public interface CustomAudiotronNameDialogListener {
		//void onFinishEditDialog(String inpuText);
		public void onPositiveClick();
		public void onNegativeClick();
	}
	public static CustomAudiotronNameDialog newInstance(int title){
		CustomAudiotronNameDialog frag = new CustomAudiotronNameDialog();
		Bundle args = new Bundle();
		args.putInt("title", title);
		frag.setArguments(args);
		
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		int title = getArguments().getInt("title");
		
		return new AlertDialog.Builder(getActivity())
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(title)
			.setPositiveButton(android.R.string.ok, this)
			.setNegativeButton(android.R.string.cancel, this)
			.create();
	}
	
//	public CustomAudiotronNameDialog() {
//
//	}

//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.activity_voice_recording,container);
//		customAudiotronTitle = (EditText) view.findViewById(R.id.audiotronTitle);
//		getDialog().setTitle("Audiotron Title");
//
//		customAudiotronTitle.requestFocus();
//		getDialog().getWindow().setSoftInputMode(LayoutParams.WRAP_CONTENT);
//		
//		customAudiotronTitle.setOnKeyListener(new View.OnKeyListener() {
//
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on key press
//                    EditNameDialogListener activity = (EditNameDialogListener) getActivity();
//                    activity.onFinishEditDialog(customAudiotronTitle.getText().toString());
//                    // EditNameDialog.this.dismiss();
//                    return true;
//                }
//                return false;
//            }
//
//		});
//		return view;
//	}
//
//	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//		if (EditorInfo.IME_ACTION_DONE == actionId) {
//			EditNameDialogListener activity = (EditNameDialogListener) getActivity();
//			activity.onFinishEditDialog(customAudiotronTitle.getText()
//					.toString());
//			this.dismiss();
//			return true;
//		}
//		return false;
//	}


//	public CustomAudiotronNameDialogListener getListener() {
//		return listener;
//	}


	public void setListener(CustomAudiotronNameDialogListener listener) {
		this.listener = listener;
	}

	public void onClick(DialogInterface dialog, int which) {
		if(listener!=null){
			switch(which){
			case DialogInterface.BUTTON_POSITIVE:
				listener.onPositiveClick();
			default:
				listener.onNegativeClick();
			}
		}
	}
}
