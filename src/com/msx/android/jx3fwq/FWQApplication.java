package com.msx.android.jx3fwq;

import com.msx.android.jx3fwq.CMD.ServerListCommand;
import com.msx7.core.Controller;
import com.msx7.core.Manager;

public class FWQApplication extends Controller {
 public static final int ID_CMD_FWQ=Manager.CMD_GET<<1;
	@Override
	public void onCreate() {
		super.onCreate();
		Manager.getInstance().registerCommand(ID_CMD_FWQ, ServerListCommand.class);
	}

}
