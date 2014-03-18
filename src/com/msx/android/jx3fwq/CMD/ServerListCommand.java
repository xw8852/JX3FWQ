package com.msx.android.jx3fwq.CMD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;

import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.msx.android.jx3fwq.FWQApplication;
import com.msx.android.jx3fwq.bean.AreaServer;
import com.msx.android.jx3fwq.bean.FWQBean;
import com.msx.android.jx3fwq.bean.ServerInfo;
import com.msx.android.jx3fwq.bean.VersionServerList;
import com.msx7.core.command.impl.HttpGetCommand;

public class ServerListCommand extends HttpGetCommand {

	@Override
	public URI getURI() {
		/**
		 * http://serverlist.jx3.xoyo.com/info.php?UserName=mb28710&type=2
		 */
		return URI.create("http://jx3gc.autoupdate.kingsoft.com/jx3gc/zhcn/serverlist/serverlist.ini");
	}

	@Override
	protected Object getSuccessResponse(HttpResponse response) {
		List<String[]> arr=new ArrayList<String[]>();
		
			try {
				InputStream in=response.getEntity().getContent();
				BufferedReader reader=new BufferedReader(new InputStreamReader(in, "GBK"));
				String str= null;
				while((str=reader.readLine())!=null){
					arr.add(str.split("\t"));
				}
				in.close();
				reader.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return arr;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onAfterExecute() {
		try {
			super.onAfterExecute();
			InputStream in=FWQApplication.getApplication().getAssets().open("serverList.txt");
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			StringBuffer buff=new StringBuffer();
			String _tmp=null;
			while((_tmp=reader.readLine())!=null){
				buff.append(_tmp);
			}
			reader.close();
			in.close();
			VersionServerList version=new Gson().fromJson(buff.toString(), VersionServerList.class);
			List<AreaServer> list=version.list;
			List<String[]> arr=(List<String[]>)getResponse().getData();
			for (String[] strs : arr) {
				FWQBean bean=new FWQBean(strs);
				FilterSever(bean, list);
			}
			getResponse().setData(convertInfo(list));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void FilterSever(FWQBean bean,List<AreaServer> arr){
		for (AreaServer area : arr) {
			if(area.area.equals(bean.server)){
				//初始化状态
				if(area.status==null){
					area.status=new int[area.servers.length];
				}
				//遍历已知列表，如果在已知列表中则 更新状态并结束此方法
				for(int i=0;i<area.servers.length;i++){
					if(Arrays.asList(area.servers[i]).contains(bean.area)){
						area.status[i]=bean.type;
						return;
					}
				}
				//未存在已知列表中，则新加
				String[][] _Narr=new String[area.servers.length+1][];
				System.arraycopy(area.servers, 0, _Narr, 0, area.servers.length);
				int[] _Nstatus=new int[area.status.length+1];
				System.arraycopy(area.status, 0, _Nstatus, 0, area.status.length);
				_Nstatus[_Nstatus.length-1]=bean.type;
				_Narr[_Narr.length-1]=new String[]{bean.area};
				area.servers=_Narr;
				area.status=_Nstatus;
				return;	
			}
		}
		AreaServer server=new AreaServer();
		server.servers=new String[1][];
		server.status=new int[]{bean.type};
		server.area=bean.server;
		server.servers[0]=new String[]{bean.area};
		arr.add(0, server);
	}
	
	private List<Pair<String, List<ServerInfo>>> convertInfo(List<AreaServer> arr){
		List<Pair<String, List<ServerInfo>>> pairs=new LinkedList<Pair<String,List<ServerInfo>>>();
		for (AreaServer areaServer : arr) {
			List<ServerInfo> infos=new LinkedList<ServerInfo>();
			for (int i=0;i<areaServer.status.length;i++) {
				ServerInfo info=new ServerInfo();
				info.area=areaServer.area;
				info.server= areaServer.servers[i][0];
				info.status=areaServer.status[i];
				StringBuffer buffer=new StringBuffer();
				for(int j=1;j<areaServer.servers[i].length;j++){
					buffer.append(areaServer.servers[i][j]);
//					if(j%4==0){
//						buffer.append("\n");
//					}else {
						buffer.append("  ");
//					}
				}
				info.otherServer=buffer.toString();
				infos.add(info);
			}
			Collections.sort(infos,comparator);
			pairs.add(new Pair<String, List<ServerInfo>>(areaServer.area, infos));
		}
		
		return pairs;
	}
 Comparator<ServerInfo> comparator=new Comparator<ServerInfo>() {

	@Override
	public int compare(ServerInfo lhs, ServerInfo rhs) {
		if(!TextUtils.isEmpty(lhs.otherServer)&&!TextUtils.isEmpty(rhs.otherServer))
		{
			if(lhs.status>2||lhs.status<0)return 1;
			if(rhs.status>2||rhs.status<0)return -1;
			return lhs.status-rhs.status;
		}else if(!TextUtils.isEmpty(lhs.otherServer)){
			return 1;
		}else if(!TextUtils.isEmpty(rhs.otherServer))
			return -1;
		return lhs.status-rhs.status;
	}
};
}
