/*******************************************************************************
 *   Copyright 2017 IBM Corp. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.ibm.janusgraph.utils.importer.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerPool implements AutoCloseable, WorkerListener {
    private static ExecutorService processor;
    private static final int sQueueCap = 50;
    private LinkedBlockingQueue<Worker> queue = new LinkedBlockingQueue<Worker>(sQueueCap);
    private AtomicInteger workerCounter = new AtomicInteger(0);
    private final long shutdownWaitMS = 10000;
    private Semaphore finished = new Semaphore(1);
    private int numThreads;

    public WorkerPool(int numThreads, int maxWorkers) {
        this.numThreads = numThreads;
        processor = Executors.newFixedThreadPool(numThreads);
        finished.acquireUninterruptibly();
    }

    public void submit(Worker worker) {
        worker.addListener(this);
        if (workerCounter.get() <= numThreads) {
            workerCounter.incrementAndGet();
            processor.submit(worker);
        } else {
            // adding the worker into queue is better then
            // using semaphore to block the feeding and wait
            // for some worker to finish. This creates
            // some sort of buffer effect.
            try {
                queue.put(worker);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
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

    /**
     * Pause the current thread and wait until all
     * workers finish their jobs.
     */
    public void wait4Finish() {
        finished.acquireUninterruptibly();
    }

    @Override
    public void close() throws Exception {
        closeProcessor();
    }

    @Override
    public void notify(Worker worker, WorkerListener.State state) {
        switch (state) {
        case Running:
            break;
        case Done:
            Worker queueItem = queue.poll();
            if (queueItem != null) {
                processor.submit(queueItem);
            } else if (workerCounter.decrementAndGet() == 0){
                finished.release();
            }
            break;
        default:
            break;
        }
    }
}
