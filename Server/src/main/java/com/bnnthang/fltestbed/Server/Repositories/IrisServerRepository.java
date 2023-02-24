package com.bnnthang.fltestbed.Server.Repositories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import com.bnnthang.fltestbed.commonutils.servers.IServerLocalRepository;

/**
 * Iris Server Repository.
 * For testing purpose only.
 */
public class IrisServerRepository implements IServerLocalRepository {
    /**
     * Working Directory.
     */
    private String workDir;

    /**
     * Current model name.
     */
    private String currentModelName;

    public IrisServerRepository(String workDir) {
        this.workDir = workDir;
    }

    @Override
    public void createNewResultFile() throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'createNewResultFile'");
    }

    @Override
    public Evaluation evaluateCurrentModel() {
        try {
            IrisDataSetIterator iris = new IrisDataSetIterator();
            DataSet allData = iris.next();
            allData.shuffle();
            SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.80);
            DataSet testData = testAndTrain.getTest();
            MultiLayerNetwork model = loadLatestModel();
            Evaluation eval = new Evaluation(3);
            INDArray output = model.output(testData.getFeatures());
            eval.eval(testData.getLabels(), output);
            model.close();
            return eval;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File getLogFolder() throws IOException {
        File logFolder = new File(workDir, "logs");
        logFolder.mkdir();
        return logFolder;
    }

    @Override
    public byte[] loadAndSerializeLatestModel() throws IOException {
        File f = new File(workDir, currentModelName);
        int modelLength = (int) f.length();
        FileInputStream fis = new FileInputStream(f);
        byte[] bytes = new byte[modelLength];
        int readBytes = fis.read(bytes, 0, modelLength);
        if (readBytes != modelLength) {
            fis.close();
            throw new IOException(String.format("read %d bytes; expected %d bytes", readBytes, modelLength));
        }
        fis.close();
        return bytes;
    }

    @Override
    public byte[] loadAndSerializeLatestModelWeights() throws IOException {
        MultiLayerNetwork model = loadLatestModel();
        INDArray params = model.params().dup();
        model.close();
        byte[] bytes = SerializationUtils.serialize(params);
        // TODO: params.close() ?
        return bytes;
    }

    @Override
    public MultiLayerNetwork loadLatestModel() throws IOException {
        return ModelSerializer.restoreMultiLayerNetwork(workDir + "/" + currentModelName);
    }

    @Override
    public List<byte[]> partitionAndSerializeDataset(int arg0, float arg1) {
        return new ArrayList<>();
    }

    @Override
    public void saveNewModel(MultiLayerNetwork newModel) throws IOException {
        String newModelName = "irisModel-" + (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime())) + ".zip";
        newModel.save(new File(workDir, newModelName));
        currentModelName = newModelName;
    }
}
