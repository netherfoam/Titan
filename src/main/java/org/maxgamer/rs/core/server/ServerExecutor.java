package org.maxgamer.rs.core.server;

import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberScheduler;
import org.maxgamer.rs.core.Core;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * @author netherfoam
 */
public class ServerExecutor implements Executor {
    private Server server;
    private ServerThread thread;
    private FiberScheduler fex;

    public ServerExecutor(Server server) {
        this.server = server;
        this.fex = new FiberExecutorScheduler("ServerThread-FiberExec", this);

        this.thread = new ServerThread(this, "ServerThread-thread");
        this.thread.setContextClassLoader(Core.CLASS_LOADER);
        this.thread.setPriority(Thread.MAX_PRIORITY);
    }

    public Server getServer() {
        return this.server;
    }

    /**
     * Returns true if the current thread is the server thread
     *
     * @return true if the current thread is the server thread
     */
    public boolean isServerThread() {
        return this.thread != null && Thread.currentThread().getId() == this.thread.getId();
    }

    /**
     * Joins with the server thread until it stops
     */
    public void shutdown() throws InterruptedException {
        this.thread.terminate();
        this.thread.join();
        this.thread = null;
    }

    /**
     * Starts the server thread running.
     *
     * @throws IllegalStateException if the server thread is running
     */
    public void start() {
        this.thread.start();
    }

    public Future<Void> submit(Runnable r) {
        if (thread == null) return null;

        return thread.submit(r);
    }

    public FiberScheduler getFiberScheduler() {
        return fex;
    }

    @Override
    public void execute(Runnable command) {
        this.submit(command);
    }

    /**
     * Asserts that the current thread is the ServerThread / Primary thread.
     *
     * @throws IllegalThreadException if the assertion fails
     */
    public void assertThread() {
        if (Thread.currentThread() != this.thread) {
            throw new IllegalThreadException("Attempted to run thread " + Thread.currentThread() + " but must be only run on the ServerThread " + this.thread);
        }
    }

    public double getUsage() {
        return thread.getUsage();
    }

    public void resetUsage() {
        thread.resetUsage();
    }
}