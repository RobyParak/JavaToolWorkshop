import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Classification {
    public static void main(String[] args) throws IOException {
        int batchSize = 1;
        int labelIndex = 4;
        int numClasses = 2;

        RecordReader recordReader = new CSVRecordReader(1 ,',');
        try
        {
            recordReader.initialize(new FileSplit(new File("src/main/java/data/badminton_dataset.csv")));

            //Create Categorical Schema
            Schema schema = new Schema.Builder()
                    .addColumnString("Outlook")
                    .addColumnString("Temperature")
                    .addColumnString("Humidity")
                    .addColumnString("Wind")
                    .addColumnString("Play_Badminton")
                    .build();

            String[] outlookCategories = {"Overcast", "Sunny", "Rain"};
            String[] temperatureCategories = {"Hot", "Mild", "Cool"};
            String[] humidityCategories = {"High", "Normal"};
            String[] windCategories = {"Weak", "Strong"};
            String[] playBadmintonCategories = {"Yes", "No"};

            TransformProcess transformProcess = new TransformProcess.Builder(schema)
                    .stringToCategorical("Outlook", Arrays.asList(outlookCategories))
                    .stringToCategorical("Temperature", Arrays.asList(temperatureCategories))
                    .stringToCategorical("Humidity", Arrays.asList(humidityCategories))
                    .stringToCategorical("Wind", Arrays.asList(windCategories))
                    .stringToCategorical("Play_Badminton", Arrays.asList(playBadmintonCategories))
                    .build();

            List<List<Writable>> csvData = new ArrayList<>();

            while(recordReader.hasNext()){
                csvData.add(recordReader.next());
            }

            List<List<Writable>> transformedData = LocalTransformExecutor.execute(csvData, transformProcess);

            for (List<Writable> data:
                    transformedData) {
                System.out.println(data);
            }

            RecordReaderDataSetIterator iterator = new RecordReaderDataSetIterator.Builder(recordReader, batchSize)
                    .classification(labelIndex, numClasses)
                    .build();
            iterator.setPreProcessor(new NormalizerStandardize());

            //Define Network
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(123)
                    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                    .updater(new Adam(0.01))
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(12).nOut(10)
                            .activation(Activation.RELU)
                            .build())
                    .layer(1, new DenseLayer.Builder().nIn(10).nOut(8)
                            .activation(Activation.RELU)
                            .build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX)
                            .nIn(8).nOut(numClasses).build())
                    .build();

            //Model Training
            MultiLayerNetwork model = new MultiLayerNetwork(conf);
            model.init();
            model.setListeners(new ScoreIterationListener(10));
            int numEpochs = 100;
            for (int i = 0; i < numEpochs; i++) {
                model.fit(iterator);
            }

            Evaluation evaluation = model.evaluate(iterator);
            System.out.println("Evaluation: " + evaluation.stats());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            recordReader.close();
        }
    }
}
