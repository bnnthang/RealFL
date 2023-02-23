package com.bnnthang.fltestbed.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.commons.lang3.SerializationUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bnnthang.fltestbed.commonutils.clients.IClientLocalRepository;
import com.bnnthang.fltestbed.commonutils.models.TimedValue;
import com.bnnthang.fltestbed.commonutils.utils.SocketUtils;

/**
 * Iris Local Client Repository.
 * For testing purpose only.
 */
public class IrisRepository implements IClientLocalRepository {
    /**
     * Path to model.
     */
    private final String pathToModel;

    /**
     * Path to dataset file.
     */
    private final String pathToDataset;
    
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(IrisRepository.class);

    public IrisRepository(String pathToModel, String pathToDataset) {
        this.pathToDataset = pathToDataset;
        this.pathToModel = pathToModel;
    }

    @Override
    public Boolean datasetExists() {
        return true;
    }

    @Override
    public Long downloadDataset(Socket socket) throws IOException {
        LOG.info("downloaded dataset");
        return 123L;
    }

    @Override
    public TimedValue<Long> downloadModel(Socket socket) throws IOException {
        File modelFile = new File(pathToModel);

        // create file if not exists
        modelFile.createNewFile();

        // download and write to model file
        FileOutputStream modelFileOutputStream = new FileOutputStream(modelFile);
        TimedValue<Long> foo = SocketUtils.readAndSaveBytes(socket, modelFileOutputStream);
        modelFileOutputStream.close();

        LOG.info("model downloaded");

        return foo;
    }

    @Override
    public File getDatasetFile() throws IOException {
        LOG.info("retrieved dataset");
        return null;
    }

    @Override
    public InputStream getDatasetInputStream() throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'getDatasetInputStream'");
    }

    @Override
    public String getDatasetName() {
        return "Iris";
    }

    @Override
    public Long getDatasetSize() throws IOException {
        return 123L;
    }

    @Override
    public File getModelFile() throws IOException {
        return new File(pathToModel);
    }

    @Override
    public String getModelPath() {
        return pathToModel;
    }

    @Override
    public MultiLayerNetwork loadModel() throws IOException {
        LOG.info("model loading");
        return ModelSerializer.restoreMultiLayerNetwork(pathToModel);
    }

    @Override
    public Boolean modelExists() {
        return (new File(pathToModel)).exists();
    }

    @Override
    public TimedValue<Long> updateModel(Socket socket) throws IOException {
        TimedValue<byte[]> foo = SocketUtils.readBytesWrapper(socket);

        INDArray params = SerializationUtils.deserialize(foo.getValue());
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(pathToModel);
        model.setParams(params);
        model.save(new File(pathToModel), true);
        model.close();

        LOG.info("model updated");

        return new TimedValue<Long>((long) foo.getValue().length, foo.getElapsedTime());
    }
}
