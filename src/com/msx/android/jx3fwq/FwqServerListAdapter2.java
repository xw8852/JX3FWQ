package com.msx.android.jx3fwq;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.msx.android.jx3fwq.bean.FWQBean;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FwqServerListAdapter2 extends SosUniversalAdapter {
	Map<String, List<FWQBean>> fwqs;
	LinkedList<String> servers;
	Context context;
	public FwqServerListAdapter2(Map<String, List<FWQBean>> fwqs,LinkedList<String> servers,
			Context context) {
		super();
		this.fwqs = fwqs;
		this.servers=servers;
		this.context = context;
	}

	@Override
	public int getCount() {
		int size=0;
		Iterator<String> it=fwqs.keySet().iterator();
		while(it.hasNext()){
			size+=fwqs.get(it.next()).size();
		}
		return size;
	}

	@Override
	public FWQBean getItem(int position) {
		int section=getSectionForPosition(position);
		List<FWQBean> beans=fwqs.get(servers.get(section));
		return beans.get(position-getPositionForSection(section));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected void onNextPageRequested(int page) {

	}

	@Override
	protected void bindSectionHeader(View view, int position,
			boolean displaySectionHeader) {
		TextView tv=(TextView)view.findViewById(R.id.serverTitle);
		if(displaySectionHeader){
			tv.setVisibility(View.VISIBLE);
		}
		else tv.setVisibility(View.GONE);
		
	}

	@Override
	public View getAmazingView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fwq, null);
			holder = new Holder();
			holder.server = (TextView) convertView
					.findViewById(R.id.Server);
			holder.area = (TextView) convertView.findViewById(R.id.Area);
			holder.serverTitle=(TextView)convertView.findViewById(R.id.serverTitle);

			holder.roleNum = (TextView) convertView
					.findViewById(R.id.RoleNum);
			holder.status=(TextView)convertView.findViewById(R.id.status);
			convertView.setTag(holder);
		}
		holder = (holder == null) ? (Holder) convertView.getTag() : holder;
		FWQBean bean = getItem(position);
		holder.server.setText(bean.server);
		holder.serverTitle.setText(bean.server);
		holder.area.setText(bean.area);
		switch (bean.type) {
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
		return convertView;
	}
	public void setTextColor(int Color,Holder holder){
		holder.status.setTextColor(Color);
		holder.server.setTextColor(Color);
		holder.area.setTextColor(Color);
		holder.roleNum.setTextColor(Color);
	}
	class Holder {
		TextView status;
		TextView serverTitle;
		TextView server;
		TextView area;
		TextView roleNum;
	}
	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		TextView tv=(TextView)header.findViewById(R.id.serverTitle);
		tv.setText(getItem(position).server);	
	}

	@Override
	public int getPositionForSection(int section) {
		int size=0;
		for (int i = 0; i < servers.size(); i++) {
			if(i<section)
				size+=fwqs.get(servers.get(i)).size();
			else break;
		}
		return size;
	}

	@Override
	public int getSectionForPosition(int position) {
		int size=0;
		for (int i = 0; i < servers.size(); i++) {
				size+=fwqs.get(servers.get(i)).size();
				if(size>position){
					return i;
				}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return fwqs.values().toArray();
	}

}
