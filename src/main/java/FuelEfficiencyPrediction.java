import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FuelEfficiencyPrediction {

    private static final Logger log = LoggerFactory.getLogger(FuelEfficiencyPrediction.class);

    public static void main(String[] args) throws Exception {
        final int batchSize = 400;
        final int nEpochs = 2000;
        int seed = 12345;
        double learningRate = 0.001;
        int numInputs = 9;
        int numOutputs = 1;

        SplitTestAndTrain testAndTrain = getSplitTestAndTrain(batchSize);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        MultiLayerConfiguration conf = buildMultiLayerConfiguration(seed, learningRate, numInputs, numOutputs);
        MultiLayerNetwork net = buildMultiLayerNetwork(conf);

        log.debug("Fit training data");
        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainingData);
        }

        // Generate scatter plots for key features against the target variable (MPG)
        generateScatterPlot(trainingData, "Weight", 4, "Weight vs MPG");
        generateScatterPlot(trainingData, "Horsepower", 3, "Horsepower vs MPG");
        generateScatterPlot(trainingData, "Cylinders", 4, "Cylinders vs MPG");
        generateScatterPlot(trainingData, "Displacement", 3, "Displacement vs MPG");
        generateScatterPlot(trainingData, "Model Year", 4, "Model Year vs MPG");
        generateScatterPlot(trainingData, "Origin_1", 3, "Origin_1 vs MPG");

        // Calculate and print correlations
        calculateAndPrintCorrelations(trainingData);

        RegressionEvaluation eval = new RegressionEvaluation(1);
        INDArray output = net.output(testData.getFeatures(), false);
        eval.eval(testData.getLabels(), output);
        log.debug("This is the average MSE: " + eval.averageMeanSquaredError());
        log.debug("This is the average MAE: " + eval.averageMeanAbsoluteError());
        log.debug("This is the average RMSE: " + eval.averagerootMeanSquaredError());
        log.debug("This is the average relative square error: " + eval.averagerelativeSquaredError());
        log.debug("This is the average pearson correlation: " + eval.averagePearsonCorrelation());
        log.debug("This is the average R Square: " + eval.averageRSquared());
    }

    private static MultiLayerNetwork buildMultiLayerNetwork(MultiLayerConfiguration conf) {
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        return net;
    }

    private static MultiLayerConfiguration buildMultiLayerConfiguration(int seed, double learningRate, int numInputs, int numOutputs) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(64)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(64).nOut(64)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(64).nOut(numOutputs).build())
                .build();
        return conf;
    }

    private static SplitTestAndTrain getSplitTestAndTrain(int batchSize) throws IOException, InterruptedException {

        /**
         * CSV headers
         * 'MPG', 'Cylinders', 'Displacement', 'Horsepower', 'Weight', 'Acceleration', 'Model Year', 'Origin', "Model Name"
         */

        CSVRecordReader csvRecordReader = new CSVRecordReader(0, ' ');
        FileSplit inputSplit = new FileSplit(new File("src/main/java/data/auto-mpg.csv"));
        csvRecordReader.initialize(inputSplit);

        Schema schema = buildSchema();
        TransformProcess transformProcess = buildTransformProcess(schema);
        Schema finalSchema = transformProcess.getFinalSchema();

        TransformProcessRecordReader trainRecordReader = new TransformProcessRecordReader(csvRecordReader, transformProcess);
        RecordReaderDataSetIterator trainIterator = new RecordReaderDataSetIterator.Builder(trainRecordReader, batchSize)
                .regression(finalSchema.getIndexOfColumn("MPG"))
                .build();

        DataSet allData = trainIterator.next();
        normalizeDataSet(allData);
        allData.shuffle(123);

        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training
        return testAndTrain;
    }

    private static void normalizeDataSet(DataSet allData) {
        NormalizerStandardize normalizerStandardize = new NormalizerStandardize();
        normalizerStandardize.fit(allData);
        normalizerStandardize.transform(allData);
    }

    private static TransformProcess buildTransformProcess(Schema schema) {
        TransformProcess transformProcess = new TransformProcess.Builder(schema)
                .removeColumns("Model Name")
                .integerToOneHot("Origin", 1, 3)
                .build();
        return transformProcess;
    }

    private static Schema buildSchema() {
        Schema schema = new Schema.Builder()
                .addColumnDouble("MPG")
                .addColumnInteger("Cylinders")
                .addColumnsDouble("Displacement", "Horsepower", "Weight", "Acceleration")
                .addColumnInteger("Model Year")
                .addColumnInteger("Origin")
                .addColumnString("Model Name")
                .build();
        return schema;
    }

    private static void generateScatterPlot(DataSet data, String featureName, int featureIndex, String title) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(featureName + " vs MPG");

        INDArray features = data.getFeatures();
        INDArray labels = data.getLabels();

        for (int i = 0; i < features.rows(); i++) {
            double x = features.getDouble(i, featureIndex);
            double y = labels.getDouble(i);
            series.add(x, y);
        }

        dataset.addSeries(series);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                title,
                featureName,
                "MPG",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) scatterPlot.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(scatterPlot));
        frame.pack();
        frame.setVisible(true);
    }

    private static void calculateAndPrintCorrelations(DataSet data) {
        INDArray features = data.getFeatures();
        INDArray labels = data.getLabels();
        int numFeatures = features.columns();

        double[] labelArray = labels.toDoubleVector();
        PearsonsCorrelation correlation = new PearsonsCorrelation();

        List<String> featureNames = Arrays.asList("Cylinders", "Displacement", "Horsepower", "Weight", "Acceleration", "Model Year", "Origin_1", "Origin_2", "Origin_3");

        for (int i = 0; i < numFeatures; i++) {
            double[] featureArray = features.getColumn(i).toDoubleVector();
            double corr = correlation.correlation(featureArray, labelArray);
            log.debug("Correlation between MPG and feature " + featureNames.get(i) + ": " + corr);
        }
    }
}
