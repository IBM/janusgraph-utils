package com.ibm.janusgraph.utils.importer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WorkerPool implements AutoCloseable {
	private static ExecutorService processor;
	private List<Future<?>> futures = new ArrayList<Future<?>>();

	private final long shutdownWaitMS = 10000;
	private int maxWorkers;
	private int numThreads;

	public WorkerPool(int numThreads, int maxWorkers) {
		this.numThreads = numThreads;
		this.maxWorkers = maxWorkers;
	    processor=Executors.newFixedThreadPool(numThreads);
	}
	
	public void submit(Runnable runnable) throws Exception {
		Future<?> future = processor.submit(runnable);
		futures.add(future);
		while (futures.size()>numThreads) {
			for (int i=0; i<futures.size();i++) {
				Future<?> f=futures.get(i);
				if (f.isDone()) {
					futures.remove(i);
				}
			}
			Thread.sleep(1000);
		}
	}

	private void closeProcessor() throws Exception {
		processor.shutdown();
		while (!processor.awaitTermination(shutdownWaitMS, TimeUnit.MILLISECONDS)) {
		}
		if (!processor.isTerminated()) {
			// log.error("Processor did not terminate in time");
			processor.shutdownNow();
		}
	}

	@Override
	public void close() throws Exception {
		closeProcessor();
	}
}
