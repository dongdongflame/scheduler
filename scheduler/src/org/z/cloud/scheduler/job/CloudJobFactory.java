package org.z.cloud.scheduler.job;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.z.cloud.scheduler.job.BaseJob.Execution;
import org.z.cloud.scheduler.job.CloudJob.JobType;

public class CloudJobFactory {

	public static Builder builder(JobDetail jobDetail, Trigger trigger, JobType jobType) {
		return new Builder(jobDetail, trigger, jobType);
	}

	public static class Builder {

		private Execution execution;
		private JobDetail jobDetail;
		private Trigger trigger;
		private JobType jobType;
		private String[] ips;

		Builder(JobDetail jobDetail, Trigger trigger, JobType jobType) {
			this.jobDetail = jobDetail;
			this.trigger = trigger;
			this.jobType = jobType;
			this.execution = Execution.ADD;
		}

		public Builder execution(Execution execution) {
			this.execution = execution;
			return this;
		}

		public Builder ips(String... ips) {
			this.ips = ips;
			return this;
		}

		public CloudJob build() {
			if (jobType == JobType.FIXED_IP && ips == null)
				throw new ExceptionInInitializerError("ips can not null");
			return new CloudJob(this);
		}

		public JobDetail getJobDetail() {
			return jobDetail;
		}

		public Trigger getTrigger() {
			return trigger;
		}

		public JobType getJobType() {
			return jobType;
		}

		public Execution getExecution() {
			return execution;
		}

		public String[] getIps() {
			return ips;
		}
	}

}