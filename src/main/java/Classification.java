import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

/*
    Credits here
*/

public class Classification {
    public static void main(String[] args) throws IOException, InterruptedException {
        //Set the variables used for the model
        int seed = 123;
        double learningRate = 0.01;
        int batchSize = 21;
        int epochs = 30;
        int inputs = 4;
        int outputs = 1;
        int hiddenNodes = 10;

        //Load training data
        RecordReader recordReaderTrain = new CSVRecordReader(1, ',');
        recordReaderTrain.initialize(new FileSplit(new File("data/badminton_dataset.csv")));
        DataSetIterator iteratorTrain = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 5, 4);

        //Load testing data
        RecordReader recordReaderTest = new CSVRecordReader(1, ',');
        recordReaderTest.initialize(new FileSplit(new File("data/badminton_dataset_test.csv")));
        DataSetIterator iteratorTest = new RecordReaderDataSetIterator(recordReaderTest, batchSize, 5, 4);

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(inputs)
                        .nOut(outputs)
                        .activation(Activation.RELU)
                        .build()
                )
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.RELU)
                        .nIn(hiddenNodes)
                        .nOut(outputs)
                        .build()
                )
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        model.setListeners(new ScoreIterationListener(10));
        model.fit(iteratorTrain, epochs);

        System.out.println("Evaluating Model...");
        Evaluation evaluation = new Evaluation(outputs);

        while(iteratorTest.hasNext()){
            DataSet testDataSet = iteratorTest.next();
            INDArray features = testDataSet.getFeatures();
            INDArray labels = testDataSet.getLabels();
            INDArray predicted = model.output(features, false);
            evaluation.eval(labels, predicted);
        }

        System.out.println(evaluation.stats());

    }
}
