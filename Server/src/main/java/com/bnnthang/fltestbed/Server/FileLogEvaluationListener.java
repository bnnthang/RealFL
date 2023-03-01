package com.bnnthang.fltestbed.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;

public class FileLogEvaluationListener extends BaseTrainingListener {
    private DataSetIterator evalDSIterator;
    private ICSVWriter csvWriter;

    public FileLogEvaluationListener(DataSetIterator evalDSIterator, File logFile) throws IOException {
        this.evalDSIterator = evalDSIterator;
        csvWriter = new CSVWriter(new FileWriter(logFile));
        csvWriter.writeNext(new String[] { "accuracy", "precision", "f1", "recall" });
    }

    @Override
    public void onEpochEnd(Model model) {
        if (model instanceof MultiLayerNetwork) {
            Evaluation evaluation = ((MultiLayerNetwork) model).evaluate(evalDSIterator);
            csvWriter.writeNext(new String[] {
                    String.valueOf(evaluation.accuracy()),
                    String.valueOf(evaluation.precision()),
                    String.valueOf(evaluation.recall()),
                    String.valueOf(evaluation.f1()),
            });
            try {
                csvWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // do nothing
        }
    }

    public void close() throws IOException {
        csvWriter.close();
    }
}
