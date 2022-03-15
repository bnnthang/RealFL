package com.bnnthang.fltestbed.Server.Repositories;

import com.sun.tools.javac.Main;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cifar10Repository {
    private Map<Byte, List<byte[]>> imagesByLabel;

    public Cifar10Repository() throws IOException {
        imagesByLabel = new HashMap<>();
        load("C:/Users/buinn/Repos/FederatedLearningTestbed/Server/src/main/resources/cifar-10/data_batch_1.bin");
        load("C:/Users/buinn/Repos/FederatedLearningTestbed/Server/src/main/resources/cifar-10/data_batch_2.bin");
        load("C:/Users/buinn/Repos/FederatedLearningTestbed/Server/src/main/resources/cifar-10/data_batch_3.bin");
        load("C:/Users/buinn/Repos/FederatedLearningTestbed/Server/src/main/resources/cifar-10/data_batch_4.bin");
        load("C:/Users/buinn/Repos/FederatedLearningTestbed/Server/src/main/resources/cifar-10/data_batch_5.bin");
    }

    private void load(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        int imageSize = 32 * 32 * 3;
        int labelSize = 1;
        int rowSize = imageSize + labelSize;
        while (inputStream.available() >= rowSize) {
            byte[] labelBytes = new byte[labelSize];
            byte[] imageBytes = new byte[imageSize];
            int bytesRead = inputStream.read(labelBytes) + inputStream.read(imageBytes);

            if (bytesRead != rowSize) {
                throw new IOException("read invalid row");
            }

            imagesByLabel.putIfAbsent(labelBytes[0], new ArrayList<>());
            imagesByLabel.get(labelBytes[0]).add(imageBytes);
        }
    }

    public List<List<byte[]>> splitDatasetIID(int nPartitions) {
        List<List<Pair<byte[], Byte>>> partitions = new ArrayList<>();
        for (int i = 0; i < nPartitions; ++i) {
            partitions.add(new ArrayList<>());
        }
        int partition = 0;
        for (byte label = 0; label < 10; ++label) {
            for (byte[] image : imagesByLabel.get(label)) {
                partitions.get(partition).add(Pair.of(image, label));

                ++partition;
                partition %= nPartitions;
            }
        }
        List<List<byte[]>> res = new ArrayList<>();
        for (int i = 0; i < nPartitions; ++i) {
            res.add(new ArrayList<>());
            for (int j = 0; j < partitions.get(i).size(); ++j) {
                byte[] t = new byte[partitions.get(i).get(j).getLeft().length + 1];
                t[0] = partitions.get(i).get(j).getRight();
                System.arraycopy(partitions.get(i).get(j).getLeft(), 0, t, 1, partitions.get(i).get(j).getLeft().length);
                res.get(i).add(t);
            }
        }
        return res;
    }

    public static int DatasetLength(List<byte[]> dataset) {
        int length = 0;
        for (int i = 0; i < dataset.size(); ++i) {
            length += dataset.get(i).length;
        }
        return length;
    }

    public static byte[] flatten(List<byte[]> dataset) {
        int length = DatasetLength(dataset);
        byte[] res = new byte[length];
        int cnt = 0;
        for (int i = 0; i < dataset.size(); ++i) {
            System.arraycopy(dataset.get(i), 0, res, cnt, dataset.get(i).length);
            cnt += dataset.get(i).length;
        }
        return res;
    }
}
