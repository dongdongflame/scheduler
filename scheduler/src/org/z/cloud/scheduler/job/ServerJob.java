package org.z.cloud.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.z.cloud.common.util.ClassUtil;
import org.z.cloud.scheduler.job.CloudJob.JobType;
import org.z.cloud.scheduler.job.ServerJobFactory.Builder;

public class ServerJob extends BaseJob implements Job {

	private static final long serialVersionUID = -8739799847179681729L;
	private static final Logger logger = LoggerFactory.getLogger(ServerJob.class);
	public static final String CLOUD_JOB = "cloudJob";

	public ServerJob() {
	}

	public ServerJob(Builder builder) {
		this.jobDetail = builder.getJobDetail();
		this.trigger = builder.getTrigger();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		CloudJob cloudJob = (CloudJob) context.getJobDetail().getJobDataMap().get(CLOUD_JOB);
		if (cloudJob == null || cloudJob.getJobType() == null)
			return;
		handlerJob(cloudJob);
	}

	private void handlerJob(CloudJob cloudJob) {
		switch (cloudJob.getJobType()) {
		case FIXED_IP:
			handlerFixIp(resetJobTypeToLocal(cloudJob));
			return;
		case REMOTE:
			handlerRemote(resetJobTypeToLocal(cloudJob));
			return;
		case BROADCAST:
			handlerBroadcast(resetJobTypeToLocal(cloudJob));
			return;
		case LOCAL:
			handlerLocal(cloudJob);
			return;
		default:
			handlerDefault(cloudJob);
			return;
		}
	}

	private void handlerFixIp(CloudJob cloudJob) {
		dispatch(cloudJob, cloudJob.getIps());
	}

	private void dispatch(CloudJob cloudJob, String[] ips) {
		if (ips == null || ips.length == 0)
			return;
		byte[] bytesJob = ClassUtil.ObjectToBytes(cloudJob);
		for (String ip : ips)
			dispatch(bytesJob, ip);
	}

	private void dispatch(byte[] cloudJob, String ip) {
		// TODO
	}

	private void handlerRemote(CloudJob cloudJob) {
		// TODO
	}

	private void handlerBroadcast(CloudJob cloudJob) {
		// TODO
	}

	private CloudJob resetJobTypeToLocal(CloudJob cloudJob) {
		cloudJob.setJobType(JobType.LOCAL);
		return cloudJob;
	}

	private void handlerLocal(CloudJob cloudJob) {
		logger.error("localJob [{}] should execute here", cloudJob.getJobDetail().getKey());
	}

	private void handlerDefault(CloudJob cloudJob) {
		logger.error("job [{}] has invalid job type", cloudJob.getJobDetail().getKey());
	}

}