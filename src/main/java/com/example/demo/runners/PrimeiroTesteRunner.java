package com.example.demo.runners;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.locker.Locker;

@Component
public class PrimeiroTesteRunner implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PrimeiroTesteRunner.class);
	
	private final Locker<String> locker;
	
	@Autowired
	public PrimeiroTesteRunner(Locker<String> locker) {
		this.locker = locker;
	}

	@Override
	public void run(String... args) throws Exception {
		Set<CompletableFuture<Void>> executions = new HashSet<>();
		
		executions.add(runAsync(() -> execute("a")));
		executions.add(runAsync(() -> execute("a")));
		executions.add(runAsync(() -> execute("c")));
		executions.add(runAsync(() -> execute("d")));
		executions.add(runAsync(() -> execute("e")));
		
		CompletableFuture.allOf(executions.toArray(new CompletableFuture[0])).join();
	}
	
	private CompletableFuture<Void> runAsync(Runnable runnable) {
		return CompletableFuture.runAsync(runnable);
	}
	
	private void execute(String resource) {
		try {
			locker.lock(resource);
			testMethod(resource);
		} finally {
			locker.release(resource);
		}
	}
	
	private void testMethod(String resource) {
		for (int i = 0; i < 100; i++) {
			LOGGER.info("Thread {} | Value: {} | Resource: {}", Thread.currentThread().getId(), i, resource);
		}
	}
	
}
