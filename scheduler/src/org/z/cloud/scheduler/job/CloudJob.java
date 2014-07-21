package org.z.cloud.scheduler.job;

import org.z.cloud.scheduler.job.CloudJobFactory.Builder;

public class CloudJob extends BaseJob {

	private static final long serialVersionUID = -6663703607818490598L;

	public static enum JobType {
		REMOTE, FIXED_IP, LOCAL, BROADCAST;
	}

	public CloudJob(Builder builder) {
		this.execution = builder.getExecution();
		this.jobType = builder.getJobType();
		this.ips = builder.getIps();
		this.trigger = builder.getTrigger();
		this.jobDetail = builder.getJobDetail();
	}

	private JobType jobType;
	private String[] ips;

	public String[] getIps() {
		return ips;
	}

	public void setIps(String[] ips) {
		this.ips = ips;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution execution) {
		this.execution = execution;
	}

}