package org.maxgamer.rs.structure.timings;

public class NullTimings extends Timings{
	/**
	 * Constructs a new Timer for the given alias, use this method to start
	 * tracking time. Call {@link StopWatch#stop()} when done to finalize the
	 * time and add it to this Timings object.
	 * @param alias the name of the timer to add to, this must be unique for
	 *        different tasks, and identifiable to the user as it is used in the
	 *        report this class generates.
	 * @return the timer object
	 */
	public StopWatch start(String alias) {
		return new NullStopWatch(alias, this);
	}
	
	/**
	 * Called by a timer object when it is finished.
	 * @param t the timer that finished
	 * @param nanosecs the number of nanoseconds that the timer was running for
	 */
	protected void stop(StopWatch t, long nanosecs) {
		// Meh
	}
	
	/**
	 * Returns a basic output of the timings for all the activities. The output
	 * format is alias: xxx.xxms. (yyyy calls)\n for each unique timer.
	 * @return A formatted report on the time usage of particular tasks.
	 */
	public String getReport() {
		return "Not enabled";
	}
}