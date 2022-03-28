package com.bnnthang.fltestbed.commonutils.servers;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;

import java.io.IOException;
import java.util.List;

public interface IServerLocalRepository {
    List<byte[]> partitionAndSerializeDataset(int numPartitions);
    MultiLayerNetwork loadLatestModel() throws IOException;
    byte[] loadAndSerializeLatestModel() throws IOException;
    void saveNewModel(MultiLayerNetwork model) throws IOException;
    Evaluation evaluateCurrentModel();
    void createNewResultFile() throws IOException;
    void appendToCurrentFile(String s) throws IOException;
}