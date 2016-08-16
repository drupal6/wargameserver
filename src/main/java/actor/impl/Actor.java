package actor.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actor.ActorTask;
import actor.IActor;
import actor.ICallback;
import actor.IRunner;

public class Actor implements IActor {

	private final Logger logger = LoggerFactory.getLogger(Actor.class);

	private final BlockingQueue<ActorTask> taskQueue;

	private Thread t = null;

	private AtomicBoolean running = new AtomicBoolean(false);

	private String name = "";

	private AtomicBoolean stopWhenEmpty = new AtomicBoolean(false);

	private AtomicInteger maxTaskCount = new AtomicInteger(0);

	public Actor(String name) {
		this(name, 80 * 1000);
	}

	public Actor(String name, int capacity) {
		this.name = "Actor-" + name;
		if (capacity <= 0) {
			// 鏃犻檺瀹归噺
			this.taskQueue = new LinkedBlockingDeque<ActorTask>();
		} else {
			// 鏈夐檺瀹归噺
			this.taskQueue = new ArrayBlockingQueue<ActorTask>(capacity);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "|" + getThreadName() + "|"
				+ getThreadId() + "|" + getQueueSize();
	}

	@Override
	public boolean start() {
		if (running.get() == true) {
			return false;
		}
		running.set(true);
		t = new Thread(new TaskRunner(), name);
		t.start();
		return true;
	}

	@Override
	public void clear() {
		taskQueue.clear();
	}

	/**
	 * 鏆村姏鍏�
	 */
	@Override
	public void stop() {
		if (running.get() == false) {
			return;
		}
		running.set(false);
		t.interrupt();
	}

	/**
	 * 鑷姩鍏�
	 */
	@Override
	public void stopWhenEmpty() {
		if (stopWhenEmpty.get() == true) {
			return;
		}
		stopWhenEmpty.set(true);
	}

	@Override
	public void waitForStop() {
		while (isRunning()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.error("", e);
				break;
			}
		}
	}

	@Override
	public int getQueueSize() {
		return this.taskQueue.size();
	}

	@Override
	public int getMaxQueueSize() {
		return this.maxTaskCount.get();
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	@Override
	public boolean isStopping() {
		return stopWhenEmpty.get();
	}

	@Override
	public boolean put(IRunner runner, long millisec) {
		return put(runner, null, null, millisec);
	}

	@Override
	public boolean put(IRunner runner) {
		return put(runner, null, null, -1);
	}

	@Override
	public boolean put(IRunner runner, ICallback callback, IActor target) {
		return put(runner, callback, target, -1);
	}

	@Override
	public boolean put(IRunner runner, ICallback callback, IActor target,
			long millisec) {
		if (stopWhenEmpty.get()) {
			logger.error("Actor is stopping: " + this.toString() + ", ignore: "
					+ runner + ", " + callback + ", " + target);
			return false;
		}
		if (!running.get()) {
			String msg = "Actor is not running, invalid put: "
					+ this.getThreadName();
			logger.error("", new IllegalStateException(msg));
			return false;
		}
		// 鏈琣ctor鐩存帴杩愯
		if (Thread.currentThread() == t) {
			ActorTask task = new ActorTask(runner, callback, target);
			try {
				runTask(task);
			} catch (Throwable e) {
				logger.error("", e);
			}
			return true;
		}
		// 鍔犲叆闃熷垪
		try {
			//
			int size = taskQueue.size();
			if (maxTaskCount.get() < size) {
				this.maxTaskCount.set(size);
			}
			ActorTask task = new ActorTask(runner, callback, target);
			if (millisec == 0) { // 婊′簡鐩存帴杩斿洖
				return taskQueue.offer(task);
			} else if (millisec > 0) { // 婊′簡绛夊緟millisec杩斿洖
				return taskQueue.offer(task, millisec, TimeUnit.MILLISECONDS);
			} else { // 涓�鐩寸瓑寰�
						// System.out.println("Thread interrupted 3 " +
						// Thread.currentThread().getName() + ", " +
						// (Thread.interrupted() ? "True" : "False"));
						// System.out.println("Thread interrupted 4 " +
						// Thread.currentThread().getName() + ", " +
						// (Thread.interrupted() ? "True" : "False"));
				taskQueue.put(task);
				return true;
			}
		} catch (InterruptedException e) {
			logger.error("", e);
			return false;
		}
	}

	@Override
	public long getThreadId() {
		return t == null ? 0 : t.getId();
	}

	@Override
	public String getThreadName() {
		return t == null ? "" : t.getName();
	}

	private class TaskRunner implements Runnable {

		@Override
		public void run() {
			while (running.get()) {
				try {
					if (stopWhenEmpty.get() && taskQueue.isEmpty()) {
						running.set(false);
						break;
					}
					final ActorTask task = taskQueue.poll(1000L,
							TimeUnit.MILLISECONDS);
					runTask(task);
				} catch (InterruptedException e) {
					logger.error("", e);
				} catch (Throwable e) {
					logger.error("", e);
				}
			}
		}
	}

	private void runTask(final ActorTask task) {
		if (task == null) {
			return;
		}
		// logger.info(getThreadName() + ", next task, remaining " +
		// taskQueue.size());
		final Object result = (task.runner == null ? null : task.runner.run());
		if (task.callback != null && task.target != null) {
			task.target.put(new IRunner() {

				@Override
				public Object run() {
					task.callback.onResult(result);
					return null;
				}
			});
		} else if (task.callback != null) {
			task.callback.onResult(result);
		}
	}
}
