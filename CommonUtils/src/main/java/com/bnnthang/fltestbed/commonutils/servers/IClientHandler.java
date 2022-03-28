package com.bnnthang.fltestbed.commonutils.servers;

import com.bnnthang.fltestbed.commonutils.models.TrainingReport;

import java.io.IOException;

public interface IClientHandler {
    /**
     * Accept client to training queue.
     */
    void accept() throws IOException;

    /**
     * Deny client connection.
     */
    void reject() throws IOException;

    /**
     * Push dataset to client.
     * @param bytes serialized dataset
     */
    void pushDataset(byte[] bytes) throws IOException;

    /**
     * Push model to client.
     * @param bytes serialized model
     */
    void pushModel(byte[] bytes) throws IOException;

    /**
     * Initiate training process at client.
     */
    void startTraining() throws Exception;

    /**
     * Retrieve training report from client.
     * @return the training report
     */
    TrainingReport getTrainingReport() throws IOException;

    /**
     * Close connection.
     */
    void done() throws Exception;

    /**
     * Check if client is still training.
     * @return <code>true</code> if client is training and
     * <code>false</code> otherwise
     */
    Boolean isTraining();

    Double getUplinkTime();
}