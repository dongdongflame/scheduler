package org.z.cloud.scheduler.job;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class ServerJobFactory {

	private final static String GROUP_NAME = "server-jobs";

	public static Builder builder(CloudJob job) {
		return new Builder(job);
	}

	public static class Builder {

		private Trigger trigger;
		private JobDetail jobDetail;
		private CloudJob cloudJob;

		public Builder(CloudJob cloludJob) {
			this.cloudJob = cloludJob;
			this.jobDetail = createJobDetail();
			this.trigger = createTrigger();
			setCloudJob();
		}

		public ServerJob build() {
			return new ServerJob(this);
		}

		private String createJobName() {
			return getCloudJobGroup() + "-" + getCloudJobName();
		}

		private String getCloudJobName() {
			return cloudJob.getJobDetail().getKey().getName();
		}

		private String getCloudJobGroup() {
			return cloudJob.getJobDetail().getKey().getGroup();
		}

		private JobDetail createJobDetail() {
			return JobBuilder.newJob(ServerJob.class).withIdentity(createJobName(), GROUP_NAME).build();
		}

		private Trigger createTrigger() {
			return TriggerBuilder.newTrigger().withIdentity(createJobName(), GROUP_NAME).startAt(cloudJob.getTrigger().getStartTime()).build();
		}

		public Trigger getTrigger() {
			return trigger;
		}

		public JobDetail getJobDetail() {
			return jobDetail;
		}

		private void setCloudJob() {
			this.jobDetail.getJobDataMap().put(ServerJob.CLOUD_JOB, cloudJob);
		}

	}

}