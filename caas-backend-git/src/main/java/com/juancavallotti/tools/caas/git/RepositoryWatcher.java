package com.juancavallotti.tools.caas.git;

import com.juancavallotti.tools.caas.git.model.GitRepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepositoryWatcher {

    private final ExecutorService executor;
    private boolean watching;
    private final WatchService watcher;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryWatcher.class);

    RepositoryWatcher() {
        try {
            logger.debug("Registering a file system watcher to reload the model.");
            watcher = FileSystems.getDefault().newWatchService();
            executor = Executors.newSingleThreadExecutor();
            watching = true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public RepositoryWatcher watch(Path dir, Runnable changeAction) {

        try {
            final WatchKey key = dir.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            final ExecutorService ex = Executors.newSingleThreadExecutor();
            ex.execute(() -> {
                while (watching) {
                    try {
                        WatchKey event = watcher.take();

                        //we probably want to run only where a real thing happens.
                        event.pollEvents().forEach(watchEvent ->
                                logger.debug("Event type: {}, Modified Files: {}, Contex: {}",
                                        watchEvent.kind(), watchEvent.count(), watchEvent.context().toString()));

                        if (!event.pollEvents().isEmpty()) {
                            logger.debug("Running action when directory change happens...");
                            try {
                                changeAction.run();
                            } catch (Throwable t) {
                                logger.error("Action produced unexpected exception... ", t);
                            }
                        }

                        event.reset();

                    } catch (InterruptedException e) {
                        logger.error("Error while polling filesystem.", ex);
                    } catch (ClosedWatchServiceException err) {
                        logger.error("Watch service stopped.");
                        break;
                    }
                }
            });

        } catch (IOException ex) {
            logger.error("Error while attempting to register filesystem watcher.", ex);
        }

        return this;
    }


    public void shutdown() {
        logger.debug("Stopping Repository Watcher...");

        try {
            watching = false;
            watcher.close();
        } catch (IOException ex) {
            logger.debug("Error while trying to shutdown filesystem watcher", ex);
        }
        executor.shutdownNow();
    }
}
