package org.z.cloud.scheduler.module;

import org.z.cloud.commom.module.Module;

public interface Server extends Module {

	public void reloadJobFromDB();
}
