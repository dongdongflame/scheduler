package org.z.cloud.scheduler;

import org.quartz.JobListener;
import org.z.cloud.scheduler.job.BaseJob;

public interface Scheduler {

	boolean start();

	boolean stop();

	boolean add(BaseJob job);

	boolean delete(BaseJob job);

	boolean update(BaseJob job);

	boolean isExists(BaseJob job);

	boolean addJobListener(JobListener listener, BaseJob job);

	org.quartz.Scheduler getScheduler();

}
