package com.bnnthang.fltestbed.Server;

import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;

import java.io.IOException;

public class ServerCifar10DataSetIterator extends RecordReaderDataSetIterator {
    private int counter = 0;
    public ServerCifar10Loader loader;

    public ServerCifar10DataSetIterator(ServerCifar10Loader _loader, int batchSize) {
        super(null, batchSize, 1, 10);
        loader = _loader;
    }

    @Override
    public void reset() {
        super.reset();
        counter = 0;
    }

    @Override
    public DataSet next(int num) {
        DataSet res = null;
        try {
            res = loader.createDataSet(num, counter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        counter += num;
        return res;
    }

    @Override
    public boolean hasNext() {
        return counter < loader.getSize();
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }
}
