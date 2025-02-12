package com.lima.consoleservice.config;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPool {

  private static final int DEFAULT_POOL_SIZE = 10;
  private ExecutorService executorService;

  public ThreadPool() {
    this(DEFAULT_POOL_SIZE);
  }

  public ThreadPool(int poolSize) {
    this.executorService = Executors.newFixedThreadPool(poolSize);
  }

  public void execute(Runnable task) {
    executorService.execute(task);
  }

  public void shutdown() {
    executorService.shutdown();
  }

  public void invokeAll(List<Callable<Void>> tasks) {
    try {
      List<Future<Void>> futures = executorService.invokeAll(tasks);
      for (Future<Void> future : futures) {
        try {
          future.get();
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("", e);
    }
  }
}
