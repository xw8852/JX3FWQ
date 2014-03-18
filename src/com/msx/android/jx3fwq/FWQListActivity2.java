package com.msx.android.jx3fwq;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.msx.android.jx3fwq.bean.ServerInfo;
import com.msx7.annotations.Inject;
import com.msx7.annotations.InjectActivity;
import com.msx7.annotations.InjectView;
import com.msx7.core.Manager;
import com.msx7.core.command.ErrorCode;
import com.msx7.core.command.IResponseListener;
import com.msx7.core.command.model.Request;
import com.msx7.core.command.model.Response;
import com.msx7.widget.AbstractAdapter;

@InjectActivity(id = R.layout.activity_fwq2)
public class FWQListActivity2 extends Activity {

	@InjectView(id = R.id.radioGroup1)
	RadioGroup mGroup;

	@InjectView(id = R.id.listView1)
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Inject.inject(this);
		Manager.getInstance().execute(FWQApplication.ID_CMD_FWQ, new Request(),
				listener);
		mGroup.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int id = mGroup.getCheckedRadioButtonId();
			for (int i = 0; i < mGroup.getChildCount(); i++) {
				View view = mGroup.getChildAt(i);
				if (view.getId() == id) {
					mListView.setAdapter(new FWQServerAdapter(
							FWQListActivity2.this, (List<ServerInfo>) view
									.getTag()));
					break;
				}
			}
		}
	};

	IResponseListener listener = new IResponseListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(Response response) {
			List<Pair<String, List<ServerInfo>>> pairs = (List<Pair<String, List<ServerInfo>>>) response
					.getData();
			for (Pair<String, List<ServerInfo>> pair : pairs) {
				getLayoutInflater().inflate(R.layout.nobuttonradio, mGroup);
				RadioButton radioButton = (RadioButton) mGroup
						.getChildAt(mGroup.getChildCount() - 1);
				radioButton.setText(pair.first);
				radioButton.setTag(pair.second);
			}
			mGroup.check(mGroup.getChildAt(0).getId());
		}

		@Override
		public void onError(Response response) {
			Toast.makeText(FWQListActivity2.this,
					ErrorCode.getErrorCodeString(response.errorCode),
					Toast.LENGTH_LONG).show();
		}
	};

	class FWQServerAdapter extends AbstractAdapter<ServerInfo> {

		public FWQServerAdapter(Context context, List<ServerInfo> data) {
			super(context, data);
		}

		@Override
		public View createView(int position, View convertView,
				LayoutInflater inflater) {
			Holder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_rect, null);
				holder=new Holder();
				holder.server=(TextView)convertView.findViewById(R.id.server);
				holder.other=(TextView)convertView.findViewById(R.id.OtherSever);
				holder.status=(TextView)convertView.findViewById(R.id.status);
				holder.roleNum=(TextView)convertView.findViewById(R.id.RoleNum);
				convertView.findViewById(R.id.rect).setVisibility(View.GONE);
				convertView.findViewById(R.id.circle).setVisibility(View.GONE);
				convertView.setTag(holder);
			}
			holder = (holder == null) ? (Holder) convertView.getTag() : holder;
			ServerInfo bean = getItem(position);
			holder.server.setText(bean.server);
			if(TextUtils.isEmpty(bean.otherServer)){
				holder.other.setVisibility(View.GONE);
			}else 
				holder.other.setVisibility(View.VISIBLE);
			holder.other.setText(bean.otherServer);
			switch (bean.status) {
			case 0:
				setTextColor(Color.YELLOW, holder);
				holder.status.setText("推荐");
				break;
			case 1:
				setTextColor(Color.GREEN, holder);
				holder.status.setText("畅通");
				break;
			case 2:
				setTextColor(Color.RED, holder);
				holder.status.setText("火爆");
				break;
			default:
				setTextColor(Color.GRAY, holder);
				holder.status.setText("维护");
				break;
			}
/**			if (!TextUtils.isEmpty(getItem(position).otherServer)) {
				convertView.findViewById(R.id.rect).setVisibility(View.VISIBLE);
				showRect(convertView, position);
			} else {
				convertView.findViewById(R.id.circle).setVisibility(
						View.VISIBLE);
				showCircle(convertView, position);
			}
*/			return convertView;
		}

		public void setTextColor(int Color,Holder holder){
			holder.status.setTextColor(Color);
			holder.roleNum.setTextColor(Color);
		}
		 void showCircle(View view, int position) {
			TextView tv = (TextView) view.findViewById(R.id.circle)
					.findViewById(R.id.textView1);
			tv.setText(getItem(position).server);
		}

		 void showRect(View view, int position) {
			TextView tv = (TextView) view.findViewById(R.id.rect).findViewById(
					R.id.textView1);
			tv.setText(getItem(position).server);

			TextView tv3 = (TextView) view.findViewById(R.id.rect)
					.findViewById(R.id.textView3);
			switch (getItem(position).status) {
			case 0:
				tv3.setText("推");
				break;
			case 1:
				tv3.setText("畅");
				break;
			case 2:
				tv3.setText("火");
				break;
			default:
				tv3.setText("维");
				break;
			}

			TextView tv2 = (TextView) view.findViewById(R.id.rect)
					.findViewById(R.id.textView2);
			tv2.setText(getItem(position).otherServer);
		}

	}
	class Holder {
		TextView server;
		TextView other;
		TextView status;
		TextView roleNum;
	}
}
