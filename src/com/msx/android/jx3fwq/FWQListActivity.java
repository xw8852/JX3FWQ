package com.msx.android.jx3fwq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msx.android.jx3fwq.bean.FWQBean;
import com.msx7.annotations.Inject;
import com.msx7.annotations.InjectActivity;
import com.msx7.annotations.InjectView;
import com.msx7.core.Manager;
import com.msx7.core.command.ErrorCode;
import com.msx7.core.command.IResponseListener;
import com.msx7.core.command.model.Request;
import com.msx7.core.command.model.Response;
import com.msx7.widget.AbstractAdapter;
import com.msx7.widget.PushHeader;
import com.msx7.widget.PushHeader.OnRefreshListener;

@InjectActivity(id = R.layout.activity_fwq)
public class FWQListActivity extends Activity {
	@InjectView(id = R.id.sosUniversalListView1)
	SosUniversalListView listView2;
	
	SosUniversalAdapter sAdapter;
	ListView listView;
	PushHeader header;
	List<FWQBean> fwqBeans = new ArrayList<FWQBean>();
	Map<String, List<FWQBean>> fwqs = new TreeMap<String, List<FWQBean>>();
	FWQSeverListAdapter adapter;
	LinkedList<String> servers=new LinkedList<String>();
	@InjectView(id=R.id.ac_head)
	View pinnedHeaderView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Inject.inject(this);
		header = new PushHeader(listView2, refreshListener);	
		pinnedHeaderView.setVisibility(View.GONE);
		sAdapter=new FwqServerListAdapter2(new HashMap<String, List<FWQBean>>(), servers,this);
		listView2.setAdapter(sAdapter);
		listView2.setPinnedHeaderView(pinnedHeaderView);
		header.onRefresh();
//		adapter = new FWQSeverListAdapter(this, new ArrayList<FWQBean>());
//		listView.setAdapter(adapter);
	}

	OnRefreshListener refreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			Manager.getInstance()
					.execute(FWQApplication.ID_CMD_FWQ, new Request(),
							header.getResponseListener(responseListener));
		}
	};
	IResponseListener responseListener = new IResponseListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(Response response) {
			fwqs.clear();
			List<String[]> arr = (List<String[]>) response.getData();
			for (String[] arrs : arr) {
				FWQBean fwqBean = new FWQBean(arrs);
				List<FWQBean> value = fwqs.get(fwqBean.server);
				if(!servers.contains(fwqBean.server))
					servers.add(fwqBean.server);
				if (value == null) {
					value = new ArrayList<FWQBean>();
				}
				fwqBeans.add(fwqBean);
				value.add(fwqBean);
				fwqs.put(fwqBean.server, value);
			}
			listView2.setAdapter(new FwqServerListAdapter2(fwqs,servers, FWQListActivity.this));
		}

		@Override
		public void onError(Response response) {
			Toast.makeText(FWQListActivity.this,
					ErrorCode.getErrorCodeString(response.errorCode),
					Toast.LENGTH_LONG).show();
		}
	};

	class FWQSeverListAdapter extends AbstractAdapter<FWQBean> {

		public FWQSeverListAdapter(Context context, List<FWQBean> data) {
			super(context, data);
		}

		@Override
		public View createView(int position, View convertView,
				LayoutInflater inflater) {
			Holder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_fwq, null);
				holder = new Holder();
				holder.server = (TextView) convertView
						.findViewById(R.id.Server);
				holder.area = (TextView) convertView.findViewById(R.id.Area);
				holder.roleNum = (TextView) convertView
						.findViewById(R.id.RoleNum);
				convertView.setTag(holder);
			}
			holder = (holder == null) ? (Holder) convertView.getTag() : holder;
			FWQBean bean = getItem(position);
			holder.server.setText(bean.server);
			holder.area.setText(bean.area);
			switch (bean.type) {
			case 0:
				holder.roleNum.setText("推荐");
				break;
			case 1:
				holder.roleNum.setText("畅通");
				break;
			case 2:
				holder.roleNum.setText("火爆");
				break;
			default:
				holder.roleNum.setText("维护");
				break;
			}
			return convertView;
		}

		class Holder {
			TextView server;
			TextView area;
			TextView roleNum;
		}

	}
	
}
