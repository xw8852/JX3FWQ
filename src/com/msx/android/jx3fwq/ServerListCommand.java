package com.msx.android.jx3fwq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

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

}
