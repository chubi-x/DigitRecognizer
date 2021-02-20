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

    /**
     * @param args the command line arguments
     */
    private static double inputs[] = new double[65];
    private static double desiredOutput[] = new double[10];
    private static double hiddenLayerWeights[][] = new double[2000][64]; //ten sets of weights with 64 values each
    private static double hiddenLayerOutputs[] = new double[2000]; //ten outputs
    private static double outputLayerWeights[][] = new double[10][2000]; //ten sets of weights with 10 values each
    private static double outputLayerOutputs[] = new double[10]; //ten outputs
    private static float success = 0;
    private static float accuracy = 0;
    private static float inputCount = 0;
    private static int epochs = 20;
    private static double learningRate = 0.005;

    public static void main(String[] args) {
        //read inputs file
        try {
            File file = new File("C:\\Users\\PC\\Documents\\NetBeansProjects\\Coursework2\\training.csv");
            Scanner reader = new Scanner(file);

            //populate input hidden layer weights
            for (double[] weights : hiddenLayerWeights) {
                populateWeights(weights);
            }
            //populate output layer weights
            for (double[] weights : outputLayerWeights) {
                populateWeights(weights);
            }

            //start computing
            int observations = 0;
            while (observations < epochs) {
                //loop through the file
                // <editor-fold defaultstate="collapsed" desc="file reader while loop ">
                while (reader.hasNextLine()) {
                    //read the line of the file
                    String features = reader.nextLine();
                    //split the line into an arrray of features
                    String[] featuresOutput = features.split(",");
                    //convert features to double and populate inputs array
                    for (int i = 0; i < featuresOutput.length; i++) {
                        inputs[i] = Double.parseDouble(featuresOutput[i]);
                    }

                    //retrieve the desired digits from inputs array
                    int digit = (int) inputs[inputs.length - 1];
                    //create mapping for desired outputs
                    mapping(digit, desiredOutput);

                    //loop through the hidden layer weights
                    //calculate the summation of the inputs to get the hidden layer outputs
                    int i = 0;
                    for (double[] weights : hiddenLayerWeights) {
                        //calculate the summation of each neuron
                        double summation = summation(inputs, weights);
                        //populate the hidden layer outputs array
                        hiddenLayerOutputs[i] = summation;
                        i++;
                    }

                    //calculate the output layer outputs
                    int a = 0;
                    for (double weights[] : outputLayerWeights) {
                        //calculate the summation between the hidden layer neurons and the output layer neurons
                        double summation = summation(hiddenLayerOutputs, weights);
                        //apply the sigmoid function
                        int neuronOutput = sigmoidFunction(summation);
                        //populate the outputlayer output
                        outputLayerOutputs[a] = neuronOutput;
                        a++;
                    }
                    if (Arrays.equals(outputLayerOutputs, desiredOutput)) {
                        success++;
                    } else {
                        //calculate the error in each outputlayer neuron
                        //loop through the output layer outputs
                        for (int b = 0; b < outputLayerOutputs.length; b++) {
                            //compare each value with the desired outputs mapping to find the error
                            if (outputLayerOutputs[b] != desiredOutput[b]) {
                                //calculate the error for each output layer neuron
                                double outputLayerNeuronError = desiredOutput[b] - outputLayerOutputs[b];
                                //calculate the error for each hidden layer neuron
                                for (int k = 0; k < outputLayerWeights.length; k++) {
                                    double hiddenLayerNeuronError = outputLayerOutputs[b] * (1 - outputLayerOutputs[b]) * outputLayerWeights[b][k] * outputLayerNeuronError;
                                    //adjust the output layer weights
                                    adjustWeights(hiddenLayerOutputs, outputLayerWeights[b], learningRate, outputLayerNeuronError);
                                    //adjust the hidden layer weights
                                    adjustWeights(inputs, hiddenLayerWeights[b], learningRate, hiddenLayerNeuronError);
                                }

                            }
                        }
                    }
                    //clear desiredOutput array
                    desiredOutput = new double[10];
                    inputCount++;

                }

                observations++;
            }
            //</editor-fold>
            System.out.println("count and success are " + inputCount + " and  " + success);
            System.out.println("Accuracy is " + success / inputCount * 100);

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + e);
        }
    }

    public static void mapping(int digit, double[] array) {
        array[digit] = 1;
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

    public static int sigmoidFunction(double sum) {
        int result;
        result = (int) (1 / (1 + Math.pow(Math.E, -1 * sum)));
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
