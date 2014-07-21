package org.z.cloud.scheduler.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class BaseJobListener implements JobListener {

	@Override
	public String getName() {
		return BaseJobListener.class.getSimpleName();
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
		// TODO
	}

}