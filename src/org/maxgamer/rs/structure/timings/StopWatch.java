package org.maxgamer.rs.structure.timings;

/**
 * @author netherfoam
 */
public class StopWatch {
	private String alias;
	private Timings src;
	private long start;
	private long end = 0;
	
	private long total = 0;
	
	protected StopWatch(String alias, Timings src) {
		this.alias = alias;
		this.src = src;
		this.start = System.nanoTime();
	}
	
	public void stop() {
		end = System.nanoTime();
		if (isStopped()) throw new IllegalStateException("Timer already stopped");
		
		src.stop(this, end - start);
		this.src = null;
	}
	
	/**
	 * The number of nanoseconds this timer has been running for. If it was not
	 * stopped, then this method returns the current System.nanoTime() -
	 * startTime. Otherwise, it returns endTime - startTime.
	 * @return the time elapsed from start to finish.
	 */
	public long getTime() {
		if (end == 0) {
			return System.nanoTime() - start + total;
		}
		else {
			return end - start + total;
		}
	}
	
	public void pause(){
		total = getTime();
		start = -1;
	}

	public boolean isPaused() {
		return start == -1;
	}
	
	public void resume(){
		if(!isPaused()) throw new IllegalStateException("StopWatch is not paused!");
		start = System.nanoTime();
	}
	
	public String getAlias() {
		return alias;
	}

	public boolean isStopped() {
		return this.src == null;
	}
}