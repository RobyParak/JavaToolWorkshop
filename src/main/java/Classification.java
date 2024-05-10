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
import org.nd4j.linalg.factory.Nd4j;
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
        int epochs = 1;
        int inputs = 4;
        int outputs = 2;
        int hiddenNodes = 10;

        //Load training data
        RecordReader recordReaderTrain = new CSVRecordReader(1, ',');
        recordReaderTrain.initialize(new FileSplit(new File("src/main/java/data/badminton_dataset.csv")));
        DataSetIterator iteratorTrain = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 4, 2); //labelIndex = at which index is the Y value
        //numPossibleLabels: How many outcomes can the Y variable have? (Yes/No = 2 unique outcomes)

        //Load testing data
        RecordReader recordReaderTest = new CSVRecordReader(1, ',');
        recordReaderTest.initialize(new FileSplit(new File("src/main/java/data/badminton_dataset_test.csv")));
        DataSetIterator iteratorTest = new RecordReaderDataSetIterator(recordReaderTest, batchSize, 4, 2);

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(inputs)
                        .nOut(hiddenNodes)
                        .activation(Activation.RELU)
                        .build()
                )
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
                        .activation(Activation.SIGMOID)
                        .nIn(hiddenNodes)
                        .nOut(outputs)
                        .build()
                )
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();
        model.setListeners(new ScoreIterationListener(1));

        for (int i = 0; i < epochs; i++){
            iteratorTrain.reset();
            model.fit(iteratorTrain);

        }
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


        int[][] newValues = {{2, 2, 1, 0}};

        INDArray newValuesInd = Nd4j.create(newValues);

        INDArray prediction = model.output(newValuesInd);

        System.out.println("Prediction is: " + prediction);
    }
}
