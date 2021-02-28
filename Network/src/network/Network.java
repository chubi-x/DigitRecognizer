/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import org.apache.commons.math3.util.Precision;

/**
 *
 * @author PC
 */
public class Network {

    private static int desiredOutputsArray[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static double hiddenLayerWeights[][] = new double[3][64]; //ten sets of weights with 64 values each
    private static double outputLayerWeights[][] = new double[10][3]; //ten sets of weights with 10 values each

    private static double trainingInputs[] = new double[65];
    private static double trainingHiddenLayerOutputs[] = new double[3]; //ten outputs
    private static double trainingOutputLayerOutputs[] = new double[10]; //ten outputs
    private static int trainingTempOutputLayerOutputs[] = new int[10];  //array to hold temporary values from sigmoid

    private static double testingInputs[] = new double[65];
    private static double testingHiddenLayerOutputs[] = new double[3]; //ten outputs 
    private static double testingOutputLayerOutputs[] = new double[10]; //ten outputs
    private static int testingTempOutputLayerOutputs[] = new int[10];  //array to hold temporary values from sigmoid

    private static double trainingSuccess = 0;
    private static double testingSuccess = 0;
    private static double inputCount = 2810;
    private static int epochs = 7;
    private static double learningRate = 0.005;
    private static double trainingAccuracy = 0;
    private static double testingAccuracy = 0;

    public static void main(String[] args) {

        //populate input hidden layer weights
        populateAllWeights();

        //start computing
        int cycles = 0;
        //file reading should be inside
        while (cycles < epochs) {
            //read inputs file
            try {
                File TestingFile = new File("C:\\Users\\PC\\Documents\\NetBeansProjects\\Coursework2\\training.csv");
                //loop through the file
                // <editor-fold defaultstate="collapsed" desc="file reader while loop ">
                Scanner reader = new Scanner(TestingFile);
                // <editor-fold defaultstate="collapsed" desc="file reader while loop ">
                while (reader.hasNextLine()) {
                    //read file
                    readFile(reader, trainingInputs);
                    //retrieve the desired digits from inputs array
                    //rename todesired output
                    int digit = (int) trainingInputs[trainingInputs.length - 1];
                    //create mapping for desired outputs
                    mapping(digit);
                    //loop through the hidden layer weights
                    //calculate the summation of the inputs to get the hidden layer outputs
                    hiddenLayerSummation(trainingInputs, trainingHiddenLayerOutputs);
                    //calculate the output layer outputs
                    outputLayerOutput(trainingHiddenLayerOutputs, trainingOutputLayerOutputs);
                    //populate temporary output layer outputs
                    tempOutputLayerOutput(trainingOutputLayerOutputs, trainingTempOutputLayerOutputs);
                    //network training
                    //check success rate
                    if (Arrays.equals(trainingTempOutputLayerOutputs, desiredOutputsArray)) {
                        trainingSuccess++;
                    } else {
                        networkTraining();
                    }

                    //clear desiredOutput array
                    clearDesiredOutput();
                }
//                close the scanner
                reader.close();

            } catch (FileNotFoundException e) {
                System.out.println("Error reading file " + e);
            }
//            inputCount = 0;
            cycles++;
        }
        cycles = 0;
        //clear desiredOutput array
        clearDesiredOutput();

        //</editor-fold>
        System.out.println("count and success are " + inputCount + " and  " + trainingSuccess);
        trainingAccuracy = trainingSuccess / inputCount;
        System.out.println(" Training Accuracy is " + trainingAccuracy * 100);

        System.out.println("\n\n################################\n TESTING \n#################################");
        while (cycles < epochs) {
            //retrieve testing file
            try {
                File trainingFile = new File("C:\\Users\\PC\\Documents\\NetBeansProjects\\Coursework2\\testing.csv");
                //read testing file
                Scanner reader = new Scanner(trainingFile);
                while (reader.hasNextLine()) {
                    //convert the file to double array
                    readFile(reader, testingInputs);
                    //retrieve desired output
                    int digit = (int) testingInputs[testingInputs.length - 1];
                    //desired output mapping
                    mapping(digit);
                    //calculate the summation of the inputs to get the hidden layer outputs
                    hiddenLayerSummation(testingInputs, testingHiddenLayerOutputs);
                    //calculate the output layer outputs
                    outputLayerOutput(testingHiddenLayerOutputs, testingOutputLayerOutputs);
                    //populate temporary output layer outputs
                    tempOutputLayerOutput(testingOutputLayerOutputs, testingTempOutputLayerOutputs);
                    //calculate success
                    if (Arrays.equals(testingTempOutputLayerOutputs, desiredOutputsArray)) {
                        testingSuccess++;
                    }
                    //clear desiredOutput array
                    clearDesiredOutput();
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found" + e);
            }
            cycles++;
        }
        System.out.println("count and success are " + inputCount + " and  " + testingSuccess);
        testingAccuracy = testingSuccess / inputCount;
        System.out.println(" Testing Accuracy is " + testingAccuracy * 100);
    }

    public static void populateAllWeights() {
        for (double[] weights : hiddenLayerWeights) {
            populateWeights(weights);
        }
        //populate output layer weights
        for (double[] weights : outputLayerWeights) {
            populateWeights(weights);
        }
    }

    public static void readFile(Scanner reader, double[] inputs) {
        //read the line of the file
        String features = reader.nextLine();
        //split the line into an arrray of features
        String[] featuresOutput = features.split(",");
        //convert features to double and populate inputs array
        for (int i = 0; i < featuresOutput.length; i++) {
            inputs[i] = Double.parseDouble(featuresOutput[i]);
        }
    }

    public static void mapping(int digit) {
        desiredOutputsArray[digit] = 1;
    }

    public static void hiddenLayerSummation(double[] inputs, double[] outputs) {
        int i = 0;
        for (double[] weights : hiddenLayerWeights) {
            //calculate the summation of each neuron
            double summation = summation(inputs, weights);
            //populate the hidden layer outputs array
            outputs[i] = sigmoidFunction(summation);
            i++;
        }
        i = 0;
    }

    public static void outputLayerOutput(double[] hiddenLayerOutputs, double[] outputLayerOutputs) {
        int a = 0;
        for (double weights[] : outputLayerWeights) {
            //calculate the summation between the hidden layer neurons and the output layer neurons
            double summation = summation(hiddenLayerOutputs, weights);
            //apply the sigmoid function
            double neuronOutput = sigmoidFunction(summation);
            //populate the outputlayer output
            outputLayerOutputs[a] = neuronOutput;
            a++;
        }
        a = 0;
    }

    public static void tempOutputLayerOutput(double[] outputs, int[] tempOutputs) {
        //find the maximum value of the outputlayer outputs and set it to 1 and the others to 0
        double max = 0;
        for (int r = 0; r < outputs.length; r++) {
            if (outputs[r] >= max) {
                max = outputs[r];
            }
        }
        for (int q = 0; q < outputs.length; q++) {
            if (outputs[q] >= max) {
                tempOutputs[q] = 1;
            } else {
                tempOutputs[q] = 0;
            }
        }
    }

    public static void networkTraining() {
        //calculate the error in each outputlayer neuron
        //loop through the output layer outputs
        for (int b = 0; b < trainingOutputLayerOutputs.length; b++) {
            //compare each value with the desired outputs mapping to find the error
            if (trainingOutputLayerOutputs[b] != desiredOutputsArray[b]) {
                //calculate the error for each output layer neuron
                double outputLayerNeuronError = desiredOutputsArray[b] - trainingOutputLayerOutputs[b];
                //calculate the error for each hidden layer neuron
                for (int k = 0; k < trainingHiddenLayerOutputs.length; k++) {
                    //hidden layer neuron error is always 0 since output layer outputs is binary
                    double hiddenLayerNeuronError = trainingOutputLayerOutputs[b] * (1 - trainingOutputLayerOutputs[b]) * outputLayerWeights[b][k] * outputLayerNeuronError;
                    //adjust the output layer weights
                    adjustWeights(trainingHiddenLayerOutputs, outputLayerWeights[b], learningRate, outputLayerNeuronError);
                    //adjust the hidden layer weights
                    adjustWeights(trainingInputs, hiddenLayerWeights[k], learningRate, hiddenLayerNeuronError);
                }

            }
        }
    }

    public static void clearDesiredOutput() {
        for (int val = 0; val < desiredOutputsArray.length; val++) {
            desiredOutputsArray[val] = 0;
        }
    }

    public static void populateWeights(double[] weights) {
        for (int i = 0; i < weights.length; i++) {
            //populate weights to value between -3 and 3
            //round
            weights[i] = Precision.round(Math.random() * (3 - -3 + 1) + -3, 3);

        }
    }

    public static double summation(double[] input, double[] weights) {
        double sum = 0;
        //check if its the input layer or output layer neuron
        int neuronLength = input.length - 1;
        if (neuronLength >= weights.length) {
            for (int i = 0; i < neuronLength; i++) {
                if (i < weights.length) {
                    sum += input[i] * weights[i];
                }
            }
        } else {
            for (int i = 0; i < input.length; i++) {
                sum += input[i] * weights[i];
            }
        }
        return sum;
    }

    public static double sigmoidFunction(double sum) {
        double result;
        result = 1 / (1 + Math.pow(Math.E, -1 * sum));
        return result;
    }

    public static void adjustWeights(double[] input, double[] weights, double learning_rate, double error) {
        for (int i = 0; i < input.length; i++) {
            if (i < weights.length) {
                weights[i] = Precision.round(weights[i] + learning_rate * error * input[i], 3);
            }
        }

    }

}
