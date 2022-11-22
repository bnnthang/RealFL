package com.bnnthang.fltestbed.Client;

import com.bnnthang.fltestbed.commonutils.clients.*;
import com.bnnthang.fltestbed.commonutils.models.ClientParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final Logger _logger = LogManager.getLogger(App.class);

    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";

    private static final int DEFAULT_SERVER_PORT = 4602;

    private static final String DEFAULT_WORK_DIR = "C:/Users/buinn/DoNotTouch/crap/testbed";

    private static final int DEFAULT_NUM_CLIENTS = 1;
    private static final boolean DEFAULT_USE_HEALTH_DATASET = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        switch (args[0]) {
            case "--help":
                help();
                break;
            case "--ml":
                ml();
                break;
            case "--fl":
                fl(args);
                break;
            default:
                throw new UnsupportedOperationException("unsupported argument: " + args[0]);
        }
    }

    private static void help() {
        // TODO: list arguments documentation
        System.out.println("help");
    }

    private static void ml() throws IOException {
        ML.trainAndEval();
    }

    private static void fl(String[] args) throws IOException, InterruptedException {
        ClientParameters parameters = parseParameters(args);

        List<BaseClient> clientPool = new ArrayList<>();

        for (int i = 0; i < parameters.getNumClients(); ++i) {
            // make client dir if needed
            String clientDir = parameters.getWorkDirectory() + "/dirclient" + i;
            Path path = Paths.get(clientDir);
            if (Files.notExists(path))
                Files.createDirectory(path);

            String pathToModel = clientDir + "/model.zip";
            String pathToDataset = clientDir + "/dataset";
            Boolean useHealthDataset = parameters.getUseHealthDataset();
            IClientLocalRepository localRepository = new LocalRepositoryImpl(pathToModel, pathToDataset, useHealthDataset);
            IClientOperations clientOperations = new BaseClientOperations(localRepository);
            BaseClient client = new BaseClient(parameters.getServerHost(), parameters.getServerPort(), 5000, clientOperations);
            client.start();

            _logger.debug("running " + i);

            clientPool.add(client);
        }

        // wait for clients to serve
        for (BaseClient client : clientPool) {
            client.join();
        }
    }

    private static ClientParameters parseParameters(String[] args) {
        String host = DEFAULT_SERVER_HOST;
        int port = DEFAULT_SERVER_PORT;
        String workDir = DEFAULT_WORK_DIR;
        int numClients = DEFAULT_NUM_CLIENTS;
        boolean useHealthDataset = DEFAULT_USE_HEALTH_DATASET;
        for (int i = 1; i < args.length; i += 2) {
            switch (args[i]) {
                case "--host":
                    host = args[i + 1];
                    break;
                case "--port":
                    port = Integer.parseInt(args[i + 1]);
                    break;
                case "--workdir":
                    workDir = args[i + 1];
                    break;
                case "--nclients":
                    numClients = Integer.parseInt(args[i + 1]);
                    break;
                case "--healthdataset":
                    useHealthDataset = Boolean.parseBoolean(args[i + 1]);
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported parameter: " + args[i]);
            }
        }
        return new ClientParameters(host, port, workDir, numClients, useHealthDataset);
    }
}
