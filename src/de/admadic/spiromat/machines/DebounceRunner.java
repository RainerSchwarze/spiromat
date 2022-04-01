package de.admadic.spiromat.machines;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import de.admadic.spiromat.SpiromatException;
import de.admadic.spiromat.log.Logger;

/**
 * Provides a class which runs a given task when notified and skips overflowing
 * notifications (like a circuit which debounces bouncing switches). For 
 * instance if some task is quite lengthy and notification 
 * events queue up faster than the task speed allows to process them, this 
 * class only calls the given task as long as notifications were received. 
 * If two notifications were received in the mean time, the task is still run 
 * only once.
 * <p>
 * A boolean parameter is available to force the task being run in the EDT.
 * <p>
 * If the task Runnable given to the constructor causes an exception, it will 
 * be wrapped in a newly thrown SpiromatException. All 
 * <code>InterruptedException</code>s simply cause the run loop to terminate. 
 * <p>
 * To stop the worker thread call <code>stopThread</stop>. 
 * This will call <code>interrupt</code> 
 * on the worker thread. Note: the worker thread is set as a daemon in the 
 * constructor of this class, so this thread running does not stop the EDT from 
 * exiting.
 * 
 * @author Rainer Schwarze
 */
public class DebounceRunner implements Runnable {
	final static Logger logger = Logger.getLogger(DebounceRunner.class);
	
	private volatile Thread thread;		// the thread instance wrapping this instance
	private volatile boolean hasValue;	// indicates whether a new value is available or not
	private Runnable task;				// the task which is passed in with the ctor.
	private boolean inEDT;				// true, if task shall be run in EDT
	private int holdOffTime = 50;		// hold off time for fast signals

	/**
	 * Creates an instance of the DebounceRunner with the given task.
	 * The inner thread is started immediately.
	 * 
	 * @param task 
	 * @param inEDT 
	 */
	public DebounceRunner(Runnable task, boolean inEDT) {
		super();
		this.task = task;
		this.inEDT = inEDT;
		thread = new Thread(this, "DebounceRunner"); //$NON-NLS-1$
		thread.setPriority(Thread.NORM_PRIORITY - 1);
		thread.setDaemon(true);	// don't block the EDT for exiting...
		logger.debug("starting thread..."); //$NON-NLS-1$
		thread.start();
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (thread==Thread.currentThread()) {
			synchronized (this) {
				if (!hasValue) {
					try {
						wait();
					} catch (InterruptedException e) {
						logger.debug("InterruptedException caught, breaking..."); //$NON-NLS-1$
						break;	// leave the run loop
					}
				}
			}
			if (hasValue) {
				synchronized (this) {
					hasValue = false;
				}
				logger.debug("woke up and got value"); //$NON-NLS-1$

				if (!inEDT || EventQueue.isDispatchThread()) {
					logger.debug("simple fire"); //$NON-NLS-1$
					task.run();
				} else {
					logger.debug("invokeAndWait fire"); //$NON-NLS-1$
					try {
						EventQueue.invokeAndWait(task);
					} catch (InterruptedException e) {
						logger.debug("InterruptedException caught, breaking..."); //$NON-NLS-1$
						break;	// leave the run loop
					} catch (InvocationTargetException e) {
						// and now?
						throw new SpiromatException(e);
					}
				}
				try {
					Thread.sleep(holdOffTime);
				} catch (InterruptedException e) {
					logger.debug("InterruptedException caught, breaking..."); //$NON-NLS-1$
					break;	// leave the run loop
				}
			}
		}
	}

	/**
	 * @return the hasValue
	 */
	public synchronized boolean isHasValue() {
		return hasValue;
	}

	/**
	 * @param hasValue the hasValue to set
	 */
	public synchronized void setHasValue(boolean hasValue) {
		this.hasValue = hasValue;
		notifyAll();
	}

	/**
	 * 
	 */
	public void stopThread() {
		if (thread!=null) {
			logger.debug("stopping thread (interrupting)..."); //$NON-NLS-1$
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * @return the sleepTime
	 */
	public int getHoldOffTime() {
		return holdOffTime;
	}

	/**
	 * @param sleepTime the sleepTime to set
	 */
	public void setHoldOffTime(int sleepTime) {
		this.holdOffTime = sleepTime;
	}
}
