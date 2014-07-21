package org.z.cloud.scheduler;

import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.z.cloud.scheduler.job.BaseJob;
import org.z.cloud.scheduler.job.BaseJobListener;

public class Quartz implements Scheduler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private org.quartz.Scheduler scheduler;

	@Override
	public boolean start() {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			return true;
		} catch (SchedulerException e) {
			loggerError(e);
			return false;
		}
	}

	@Override
	public boolean stop() {
		try {
			scheduler.shutdown(true);
			return true;
		} catch (SchedulerException e) {
			loggerError(e);
			return false;
		}
	}

	@Override
	public boolean add(BaseJob job) {
		if (!addToScheduler(job))
			return false;
		return addJobListener(new BaseJobListener(), job);
	}

	@Override
	public boolean addJobListener(JobListener listener, BaseJob job) {
		Matcher<JobKey> matcher = KeyMatcher.keyEquals(job.getJobDetail().getKey());
		try {
			scheduler.getListenerManager().addJobListener(listener, matcher);
			return true;
		} catch (SchedulerException e) {
			loggerError(e);
			return false;
		}
	}

	private boolean addToScheduler(BaseJob job) {
		if (job == null || isExists(job))
			return false;
		try {
			scheduler.scheduleJob(job.getJobDetail(), job.getTrigger());
			return true;
		} catch (SchedulerException e) {
			loggerError(e);
			return false;
		}
	}

	@Override
	public boolean delete(BaseJob job) {
		if (job == null)
			return false;
		try {
			return scheduler.deleteJob(job.getJobDetail().getKey());
		} catch (SchedulerException e) {
			loggerError(e);
			return false;
		}
	}

	@Override
	public boolean update(BaseJob job) {
		delete(job);
		return add(job);
	}

	@Override
	public boolean isExists(BaseJob job) {
		if (job == null)
			return false;
		try {
			return scheduler.checkExists(job.getJobDetail().getKey());
		} catch (SchedulerException e) {
			loggerError(e);
			return false;
		}
	}

	private void loggerError(Exception e) {
		logger.error(e.getMessage(), e);
	}

	@Override
	public org.quartz.Scheduler getScheduler() {
		return scheduler;
	}

}