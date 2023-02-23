package com.bnnthang.fltestbed.Server.Repositories;

import com.bnnthang.fltestbed.commonutils.models.TrainingConfiguration;
import com.bnnthang.fltestbed.commonutils.servers.IServerLocalRepository;

import java.io.IOException;

public class ServerLocalRepositoryFactory {
    public static IServerLocalRepository getRepository(
            boolean test,
            boolean useHealthDataset,
            String workDir,
            boolean useConfig,
            TrainingConfiguration trainingConfiguration) throws IOException {
        if (test) {
            return new IrisServerRepository(workDir);
        } else if (useHealthDataset) {
            return new ChestXrayRepository(workDir, useConfig, trainingConfiguration);
        } else {
            return new Cifar10Repository(workDir, useConfig, trainingConfiguration);
        }
    }
}