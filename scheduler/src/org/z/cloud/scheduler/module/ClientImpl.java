package org.z.cloud.scheduler.module;

import org.z.cloud.commom.module.ModuleFactory;
import org.z.cloud.common.util.ClassUtil;
import org.z.cloud.scheduler.Quartz;
import org.z.cloud.scheduler.Scheduler;
import org.z.cloud.scheduler.job.CloudJob;
import org.z.cloud.scheduler.job.CloudJob.JobType;

public class ClientImpl implements Client {

	private Scheduler scheduler = null;

	@Override
	public boolean start() {
		scheduler = new Quartz();
		return scheduler.start();
	}

	@Override
	public boolean stop() {
		return scheduler.stop();
	}

	@Override
	public boolean reStart() {
		if (!stop())
			return false;
		return start();
	}

	@Override
	public void registerToControlCenter() {
		// TODO
	}

	@Override
	public Object service(Object... params) {
		if (params == null || params.length == 0)
			return false;
		CloudJob job = resloveCloudJob(params[0]);
		return handlerJob(job);
	}

	private CloudJob resloveCloudJob(Object param) {
		if (param instanceof CloudJob)
			return (CloudJob) param;
		if (param instanceof byte[])
			return (CloudJob) ClassUtil.BytesToObject((byte[]) param);
		return null;
	}

	private boolean handlerJob(CloudJob job) {
		if (job == null)
			return false;
		if (job.getJobType() == JobType.LOCAL)
			return handlerLocalJob(job);
		return handlerRemoteJob(job);
	}

	private boolean handlerLocalJob(CloudJob job) {
		switch (job.getExecution()) {
		case ADD:
			return scheduler.add(job);
		case DELETE:
			return scheduler.delete(job);
		case UPDATE:
			return scheduler.update(job);
		}
		return false;
	}

	private boolean handlerRemoteJob(CloudJob job) {
		ModuleFactory.INSTANCES.getModule(ServerImpl.class.getName()).service(ClassUtil.ObjectToBytes(job));
		return true;
	}

}