package com.bnnthang.fltestbed.commonutils.clients;

import java.io.IOException;
import java.time.LocalDateTime;

import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.bnnthang.fltestbed.commonutils.models.MemoryListener;
import com.bnnthang.fltestbed.commonutils.models.ModelUpdate;
import com.bnnthang.fltestbed.commonutils.utils.TimeUtils;

public class IrisTrainingWorker extends Thread {
    /**
     * Repository.
     */
    private IClientLocalRepository irisRepository;

    /**
     * Fixed epoch of 1.
     */
    private int EPOCHS = 1;

     /**
     * Model update here.
     */
    private ModelUpdate modelUpdate;

    /**
     * Training stat here.
     */
    private IClientTrainingStatManager trainingStatManager;

    public IrisTrainingWorker(IClientLocalRepository irisRepository, ModelUpdate modelUpdate, IClientTrainingStatManager trainingStatManager) {
        this.irisRepository = irisRepository;
        this.modelUpdate = modelUpdate;
        this.trainingStatManager = trainingStatManager;
    }

    @Override
    public void run() {
        try {
            // load model
            MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(irisRepository.getModelFile(), true);

            DataSetIterator iterator = new IrisDataSetIterator();
            DataSet allData = iterator.next();
            allData.shuffle();
            SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.8);  
            DataSet trainData = testAndTrain.getTrain();

            model.setListeners(new MemoryListener());

            // train and time track
            LocalDateTime startTime = LocalDateTime.now();
            for (int i = 0; i < EPOCHS; ++i) {
                model.fit(trainData);
            }
            LocalDateTime endTime = LocalDateTime.now();

            // update
            modelUpdate.setWeight(model.params().dup());
            trainingStatManager.setTrainingTime(TimeUtils.millisecondsBetween(startTime, endTime));

            model.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
