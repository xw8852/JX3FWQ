package com.msx.android.jx3fwq;

public class FWQBean {
	public String server;
	public String area;
	public int type;
	public String ip;
	public String port;
	public String serverType;
	public String areaType;
	public int what1;
	public int what2;
	public String severNo;

	public FWQBean() {
		super();
	}

	public FWQBean(String[] arr) {
		server = arr[0];
		area = arr[1];
		type=Integer.parseInt(arr[2]);
		ip = arr[3];
		port = arr[4];
		serverType = arr[5];
		areaType = arr[6];
		what1 = Integer.parseInt(arr[7]);
		what2 = Integer.parseInt(arr[8]);
		severNo = arr[9];
	}
}
