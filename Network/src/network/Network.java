package network;

//import required libaries
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Chubiyojo Michael Adejoh M00796245
 */
public class Network {

    /**
     * @param args the command line arguments
     */
    private static double inputs[] = new double[65]; //array to hold inputs
    private static int desiredOutput[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //desired output mapping
    private static double hiddenLayerWeights[][] = new double[370][64]; //360 sets of weights with 64 values each
    private static double hiddenLayerOutputs[] = new double[370]; //360 outputs from the hidden layer
    private static double outputLayerWeights[][] = new double[10][370]; //ten sets of weights with 360 values each
    private static double outputLayerOutputs[] = new double[10]; //10 outputs from the output layer
    private static int tempOutputLayerOutputs[] = new int[10]; //array to hold temporary values from output layer sigmoid values
    private static double success = 0; //success counnter
    private static double testingAccuracy = 0; //testing accuracy metric
    private static double trainingAccuracy = 0; //training accuracy metric
    private static double inputCount = 2810; //number of rows in training and testing files
    private static int epochs = 300; // number of training iterations
    private static double learningRate = 0.007; //constant learning rate

    public static void main(String[] args) {

        //populate input hidden layer weights
        for (double[] weights : hiddenLayerWeights) {
            populateWeights(weights);
        }
        //populate output layer weights
        for (double[] weights : outputLayerWeights) {
            populateWeights(weights);
        }
        training();
        testing();

    }

    //method that performs training
    private static void training() {
        //start computing
        int cycles = 0;
        while (cycles < epochs) {
            //read inputs file
            try {
                File file = new File("C:\\Users\\PC\\Documents\\NetBeansProjects\\Coursework2\\training.csv");
                //create scanner instance on file
                Scanner reader = new Scanner(file);
                //loop through the file
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
                    //perform summation on hidden layer neurons
                    hiddenOutput();
                    //compute output layer neuron outputs
                    outputLayerOutput();
                    //check if the temporary output layer mapping is equal to the desired output mapping
                    if (Arrays.equals(tempOutputLayerOutputs, desiredOutput)) {
                        //increment the success counter
                        success++;
                    } else {
                        //train the network and adjust the weights
                        networkTraining();
                    }
                    //clear desiredOutput array
                    clearDesiredOutput();
                }
                //calculate accuracy
                trainingAccuracy = (success / inputCount) * 100;
                //close scanner
                reader.close();
            } //catch clause
            catch (FileNotFoundException e) {
                System.out.println("Error reading file " + e);
            }
            //increment cycles 
            cycles++;
            //reset success metric to 0 after each cycle
            success = 0;
        }
        //print final training accuracy
        System.out.println("Accuracy is " + trainingAccuracy);
    }

    //method that performs testing
    private static void testing() {
        //try clause
        try {
            //retrieve testing file
            File testfile = new File("C:\\Users\\PC\\Documents\\NetBeansProjects\\Coursework2\\testing.csv");
            //create scanner instance on testing file
            Scanner reader = new Scanner(testfile);
            //loop through the file
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
                //compute outputs from the hidden layer
                hiddenOutput();
                //compute weighted summation of output layer neurons and map them to a temporary array
                outputLayerOutput();
                //check if temporary array mapping is equal to desired outputs mapping
                if (Arrays.equals(tempOutputLayerOutputs, desiredOutput)) {
                    //increment success counter
                    success++;
                }
                //calculate testing accuracy
                testingAccuracy = (success / inputCount) * 100;
                //clear desiredOutput array
                clearDesiredOutput();
            }

        } //catch clause
        catch (FileNotFoundException e) {
            System.out.println("Error reading file " + e);
        }
        //print final testing accuracy
        System.out.println("Testing Accuracy is " + testingAccuracy);
    }

    //method that trains the network
    private static void networkTraining() {
        //create array to hold error from output layer neurons
        double[] outputLayerErrors = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        //loop through and populate output layer errors 
        for (int i = 0; i < outputLayerErrors.length; i++) {
            //error is the difference between desired output and actual output from the neuron
            outputLayerErrors[i] = desiredOutput[i] - outputLayerOutputs[i];
        }
        //array to hold hidden layer neurons errors
        double[] hiddenLayerErrors = new double[hiddenLayerOutputs.length];
        //loop through hidden layer outputs
        for (int j = 0; j < hiddenLayerOutputs.length; j++) {
            //loop through output layer outputs
            for (int i = 0; i < outputLayerOutputs.length; i++) {
                //calculate the errors of each nueron in the hidden layer
                hiddenLayerErrors[j] = outputLayerOutputs[i] * (1 - outputLayerOutputs[i]) * outputLayerErrors[i] * outputLayerWeights[i][j];
            }
        }
        //adjust the weights of hidden and output layers
        adjustWeights(outputLayerErrors, hiddenLayerErrors);
    }

    //method that creates desired outputs mapping
    public static void mapping(int digit, int[] array) {
        //set index of provided digit to 1
        array[digit] = 1;
    }

    //method that popuulates weights of hidden and outpu layer neurons
    public static void populateWeights(double weights[]) {
        //loop through weights
        for (int i = 0; i < weights.length; i++) {
            //create maximum and minimum weights
            double max = 0.15, min = -0.15;
            //populate weights to values between -3 and 3
            weights[i] = Math.random() * (max - min) + min;

        }
    }

    //method that calculates weighted summation
    public static double summation(double[] input, double[] weights) {
        //create sum variable
        double sum = 0;
        //creat neuron length variable
        int neuronLength = input.length - 1;
        //check if its the input layer or output layer neuron
        if (neuronLength >= weights.length) {
            //loop through input
            for (int i = 0; i < neuronLength; i++) {
                //calculate weighted sum
                sum += input[i] * weights[i];
            }
        } else {
            //loop through inputs array
            for (int i = 0; i < input.length; i++) {
                //calculate weighted sum 
                sum += input[i] * weights[i];
            }
        }
        //return sum
        return sum;
    }

    //method that calculates sigmoid values of weighted neuron sums
    public static double sigmoidFunction(double sum) {
        //return sigmoid value
        return 1 / (1 + (Math.pow(Math.E, -sum)));
    }

    //method to adjust the weights of the hidden and output layers
    public static void adjustWeights(double[] outputLayerErrors, double[] hiddenLayerErrors) {
        //loop through output layer weights
        for (int i = 0; i < outputLayerWeights.length; i++) {
            //loop through hidden layer outputs
            for (int j = 0; j < hiddenLayerOutputs.length; j++) {
                //adjust the weights of each weights between hidden and output layers
                outputLayerWeights[i][j] = outputLayerWeights[i][j] + learningRate * hiddenLayerOutputs[j] * outputLayerErrors[i];
            }
        }
        //loop through hidden layer weights
        for (int i = 0; i < hiddenLayerWeights.length; i++) {
            //loop through original inputs
            for (int j = 0; j < inputs.length - 1; j++) {
                //adjust weights between input and hidden layers
                hiddenLayerWeights[i][j] = hiddenLayerWeights[i][j] + learningRate * inputs[j] * hiddenLayerErrors[i];
            }
        }
    }

    //method to calculate weighted sum of neurons in the hidden layer and populates 
    private static void hiddenOutput() {
        //create loop counter
        int i = 0;
        //loop through the hidden layer weights
        for (double[] weights : hiddenLayerWeights) {
            //calculate the summation of each neuron
            double summation = summation(inputs, weights);
            //pass each summation through sigmoid activation function
            double neuronOutput = sigmoidFunction(summation);
            //populate the hidden layer outputs array
            hiddenLayerOutputs[i] = neuronOutput;
            //increment counter
            i++;
        }
    }

    //method to compute outputs from the output layer and map them to a temporary array
    private static void outputLayerOutput() {
        //create counter variable
        int a = 0;
        //loop through output layer weights
        for (double weights[] : outputLayerWeights) {
            //calculate the weighted summation of output layer neurons
            double summation = summation(hiddenLayerOutputs, weights);
            //apply the sigmoid function
            double neuronOutput = sigmoidFunction(summation);
            //populate the outputlayer output
            outputLayerOutputs[a] = neuronOutput;
            //increment counter
            a++;
        }
        //find the maximum value of the outputlayer outputs and set it to 1 and the others to 0
        //create max variable
        double max = 0;
        //loop through output layer outputs
        for (int r = 0; r < outputLayerOutputs.length; r++) {
            //check if each value in the output layer is greater than the max
            if (outputLayerOutputs[r] >= max) {
                //set the max to the corresponding value
                max = outputLayerOutputs[r];
                tempOutputLayerOutputs[r] = 1;
            }

        }
        //loop through output layer outputs
        for (int q = 0; q < outputLayerOutputs.length; q++) {
            //check if each digit is greater than the max
            if (outputLayerOutputs[q] >= max) {
                //set the corresponding index of temp output layer outputs array to 1
                tempOutputLayerOutputs[q] = 1;
            } else {
                //set the rest to 0
                tempOutputLayerOutputs[q] = 0;
            }
        }
    }

    //method to clear desired outputs mapping
    private static void clearDesiredOutput() {
        //loop through desired outputs array
        for (int val = 0; val < desiredOutput.length; val++) {
            //set each value to 0
            desiredOutput[val] = 0;
        }
    }
}
