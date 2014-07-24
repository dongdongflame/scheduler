package org.z.cloud.scheduler.module;

import java.io.IOException;
import java.util.Map;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.z.cloud.common.db.LevelDB;
import org.z.cloud.common.util.ClassUtil;
import org.z.cloud.scheduler.Quartz;
import org.z.cloud.scheduler.Scheduler;
import org.z.cloud.scheduler.job.CloudJob;
import org.z.cloud.scheduler.job.ServerJob;
import org.z.cloud.scheduler.job.ServerJobFactory;

public class ServerImpl implements Server {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private long startTime = 0L;
	private Scheduler scheduler = null;
	private LevelDB schedulerDB = null;

	@Override
	public boolean start() {
		startTime = System.currentTimeMillis();
		if (!startScheduler())
			return false;
		if (!startDB())
			return false;
		reloadJobFromDB();
		return true;
	}

	private boolean startScheduler() {
		scheduler = new Quartz();
		return scheduler.start();
	}

	private boolean startDB() {
		schedulerDB = LevelDB.SCHEDULER;
		return true;
	}

	@Override
	public void reloadJobFromDB() {
		DBIterator iterator = schedulerDB.dbInstance().iterator();
		if (iterator == null)
			return;
		try {
			executeDbIterator(iterator);
		} finally {
			closeDbIterator(iterator);
		}
	}

	private void executeDbIterator(DBIterator iterator) {
		for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
			Map.Entry<byte[], byte[]> entry = (Map.Entry<byte[], byte[]>) iterator.peekNext();
			CloudJob job = (CloudJob) ClassUtil.BytesToObject(entry.getValue());
			addServerJobToScheduler(job);
		}
	}

	private void closeDbIterator(DBIterator iterator) {
		try {
			iterator.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean stop() {
		schedulerDB.save();
		return scheduler.stop();
	}

	@Override
	public boolean reStart() {
		if (!stop())
			return false;
		return start();
	}

	@Override
	public Object service(Object... params) {
		if (params == null || params.length == 0)
			return false;
		CloudJob job = resloveCloudJob(params[0]);
		return handlerJob(job);
	}

	private CloudJob resloveCloudJob(Object param) {
		if (param instanceof byte[])
			return (CloudJob) ClassUtil.BytesToObject((byte[]) param);
		return null;
	}

	private boolean handlerJob(CloudJob job) {
		if (job == null)
			return false;
		switch (job.getExecution()) {
		case ADD:
			return addJob(job);
		case DELETE:
			return deleteJob(job);
		case UPDATE:
			deleteJob(job);
			return addJob(job);
		}
		return false;
	}

	private boolean addJob(CloudJob job) {
		if (!addServerJobToScheduler(job))
			return false;
		return addCloudJobToDB(job);
	}

	private boolean addServerJobToScheduler(CloudJob job) {
		return scheduler.add(createServerJob(job));
	}

	private boolean addCloudJobToDB(CloudJob job) {
		schedulerDB.put(job.getJobDetail().getKey().toString(), ClassUtil.ObjectToBytes(job));
		return true;
	}

	private boolean deleteJob(CloudJob job) {
		deleteCloudJobInDB(job);
		return deleteServerJobInScheduler(job);
	}

	private boolean deleteServerJobInScheduler(CloudJob job) {
		return scheduler.delete(createServerJob(job));
	}

	private ServerJob createServerJob(CloudJob job) {
		return ServerJobFactory.builder(job).build();
	}

	private boolean deleteCloudJobInDB(CloudJob job) {
		schedulerDB.delete(job.getJobDetail().getKey().toString());
		return true;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

}
