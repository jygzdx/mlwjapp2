/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package easeui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.easeui.R;


public class EaseAlertDialog extends Dialog {
	
	public interface AlertDialogUser {
		public void onResult(boolean confirmed, Bundle bundle);
	}

	private String title;
	private String msg;
	private AlertDialogUser user;
    EditText edit;
	private Bundle bundle;
	private boolean showCancel = false;
	private boolean showEdit=false;

	public EaseAlertDialog(Context context, int msgId) {
		super(context);
		this.title = context.getResources().getString(R.string.prompt);
		this.msg = context.getResources().getString(msgId);
		this.setCanceledOnTouchOutside(true);
	}
	
	public EaseAlertDialog(Context context, String msg) {
		super(context);
		this.title = context.getResources().getString(R.string.prompt);
		this.msg = msg;
		this.setCanceledOnTouchOutside(true);
	}
	
	public EaseAlertDialog(Context context, int titleId, int msgId) {
		super(context);
		this.title = context.getResources().getString(titleId);
		this.msg = context.getResources().getString(msgId);
		this.setCanceledOnTouchOutside(true);
	}
	
	public EaseAlertDialog(Context context, String title, String msg) {
		super(context);
		this.title = title;
		this.msg = msg;
		this.setCanceledOnTouchOutside(true);
	}

	public EaseAlertDialog(Context context, int titleId, int msgId, Bundle bundle, AlertDialogUser user, boolean showCancel) {
		super(context);
		this.title = context.getResources().getString(titleId);
		this.msg = context.getResources().getString(msgId);
		this.user = user;
		this.bundle = bundle;
		this.showCancel = showCancel;
		this.setCanceledOnTouchOutside(true);
	}
	
	public EaseAlertDialog(Context context, String title, String msg, Bundle bundle, AlertDialogUser user, boolean showCancel) {
		super(context);
		this.title = title;
		this.msg = msg;
		this.user = user;
		this.bundle = bundle;
		this.showCancel = showCancel;
		this.setCanceledOnTouchOutside(true);
	}

    /**
     * @param context
     * @param title
     * @param msg
     * @param user
     * @param showCancel
     * @param showEdit
     */
    public EaseAlertDialog(Context context,String title,String msg,AlertDialogUser user,boolean showCancel,boolean showEdit){
        super(context);
        this.title=title;
        this.msg=msg;
        this.user=user;
        this.showCancel=showCancel;
        this.showEdit=showEdit;
    }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ease_alert_dialog);
		
		Button cancel = (Button)findViewById(R.id.btn_cancel);
		Button ok = (Button)findViewById(R.id.btn_ok);
		TextView titleView = (TextView) findViewById(R.id.title);
        TextView textMsg = (TextView) findViewById(R.id.alert_message);
         edit= (EditText) findViewById(R.id.alert_edit);

		setTitle(title);
		
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.btn_ok) {
					onOk(view);
				} else if (view.getId() == R.id.btn_cancel) {
					onCancel(view);
				}
			}
		};
		cancel.setOnClickListener(listener);
		ok.setOnClickListener(listener);

		if (title != null)
		    titleView.setText(title);
		
		if (showCancel) {
			cancel.setVisibility(View.VISIBLE);
		}

		if (msg != null)
		    textMsg.setText(msg);

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_ok) {
                    onOk(view);
                } else if (view.getId() == R.id.btn_cancel) {
                    onCancel(view);
                }
            }
        };
        if(showEdit){
            textMsg.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            edit.setText(msg);
            if (bundle==null){
                bundle = new Bundle();
            }
            cancel.setOnClickListener(listener2);
            ok.setOnClickListener(listener2);
        }

	}
	
	public void onOk(View view){
		this.dismiss();
		if (this.user != null) {
            if (showEdit){
                bundle.putString("alert",edit.getText().toString());
            }
			this.user.onResult(true, this.bundle);
		}
	}
	
	public void onCancel(View view) {
		this.dismiss();
		if (this.user != null) {
			this.user.onResult(false, this.bundle);
		}
	}
}