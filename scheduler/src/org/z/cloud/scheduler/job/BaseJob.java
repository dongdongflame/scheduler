package org.z.cloud.scheduler.job;

import java.io.Serializable;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class BaseJob implements Serializable {

	private static final long serialVersionUID = -1351447572808657903L;

	public static enum Execution {
		ADD, UPDATE, DELETE;
	}

	protected Trigger trigger;
	protected JobDetail jobDetail;
	protected Execution execution;

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution execution) {
		this.execution = execution;
	}

}