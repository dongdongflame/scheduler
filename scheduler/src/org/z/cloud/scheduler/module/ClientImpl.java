package org.z.cloud.scheduler.module;

import java.util.ArrayList;
import java.util.List;

import org.z.cloud.commom.module.ModuleFactory;
import org.z.cloud.common.util.ClassUtil;
import org.z.cloud.common.util.Common;
import org.z.cloud.scheduler.Quartz;
import org.z.cloud.scheduler.Scheduler;
import org.z.cloud.scheduler.job.CloudJob;

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
		switch (job.getJobType()) {
		case LOCAL:
			return handlerLocalJob(job);
		case FIXED_IP:
			return handlerFixIpJob(job);
		default:
			return handlerRemoteJob(job);
		}
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

	private boolean handlerFixIpJob(CloudJob job) {
		String[] ips = job.getIps();
		if (ips == null || ips.length == 0)
			return false;
		String[] noLocalIps = excludeLocalIp(ips);
		if (containLocalIp(ips, noLocalIps))
			scheduler.add(job);
		return handlerNoLocalIps(job, noLocalIps);
	}

	private String[] excludeLocalIp(String[] ips) {
		List<String> result = new ArrayList<>();
		for (String ip : ips)
			if (!ip.equals(Common.localIp))
				result.add(ip);
		return (String[]) result.toArray();
	}

	private boolean containLocalIp(String[] originIps, String[] excludeIps) {
		return originIps.length > excludeIps.length;
	}

	private boolean handlerNoLocalIps(CloudJob job, String[] ips) {
		if (ips.length == 0)
			return true;
		job.setIps(ips);
		return handlerRemoteJob(job);
	}

	private boolean handlerRemoteJob(CloudJob job) {
		ModuleFactory.INSTANCES.getModule(ServerImpl.class.getName()).service(ClassUtil.ObjectToBytes(job));
		return true;
	}

}