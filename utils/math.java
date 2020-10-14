package utils;

public class math {

    // arithmetic
    public static double sumOf(double[] numbers) {
        // returns the sum of all the elements in an array
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return sum;
    }

    // comparison
    public static boolean equalElements(double[] numbers) {
        // checks whether all elements of an array are equal
        double firstNumber = numbers[0];
        boolean areAllEqual = true;
        for (double number : numbers) {
            if (number != firstNumber) {
                areAllEqual = false;
                break;
            }
        }
        return areAllEqual;
    }

    // decimals
    public static double roundOfTo(double number, int dp) {
        // rounds of a single number to a specified number of decimal places
        return (Math.round(number * (Math.pow(10, dp)))) / (Math.pow(10, dp));
    }

    public static double[] roundArrOfTo(double[] numbers, int dp) {
        /// rounds of every element in array to a specified number of decimal places
        double[] roundedNumbers = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            roundedNumbers[i] = roundOfTo(numbers[i], dp);
        }
        return roundedNumbers;
    }

    // angles - not precise as PI value is only to 15 decimal points :(
    public static double angleDeg(double angleRad) {
        // converts an angle from radians to degrees
        return (angleRad * 180 / Math.PI);
    }

    public static double angleRad(double angleDeg) {
        // converts an angle from degrees to radians
        return (angleDeg / 180 * Math.PI);
    }

    // matrices
    public static boolean validMatrixMultiplication(double[][] transformationMatrix, double[][] operandMatrix) {
        // checking if transformation matrix is a square matrix
        boolean transMatIsSquare = true;
        for (int row = 0; row < transformationMatrix.length; row++) {
            if (transformationMatrix[row].length != transformationMatrix.length) {
                transMatIsSquare = false;
                break;
            }
        }
        if (!transMatIsSquare) {
            System.out.println("Transformation matrix must be a square matrix!");
        }
        // checking if the number of columns in each row of the matrix operand are the
        // same
        double[] numsOfOpCols = new double[operandMatrix.length];
        for (int row = 0; row < operandMatrix.length; row++) {
            numsOfOpCols[row] = operandMatrix[row].length;
        }
        boolean opMatIsValid = equalElements(numsOfOpCols);
        if (!opMatIsValid) {
            System.out.println("The number of columns in each row of the matrix operand must be the same!");
        }
        // checking if number of columns in transformation matrix matches the number of
        // rows in matrix operand
        if (transMatIsSquare && opMatIsValid) {
            if (transformationMatrix[0].length == operandMatrix.length) {
                return true;
            } else {
                System.out.println(
                        "The number of columns in the transformation matrix must match the number of rows in the matrix operand!");
            }
        }
        return false;
    }

    public static double[][] multiplyMatrices(double[][] transformationMatrix, double[][] operandMatrix) {
        if (validMatrixMultiplication(transformationMatrix, operandMatrix)) {
            double[][] transformedMatrix = new double[operandMatrix.length][operandMatrix[0].length];
            for (int oRow = 0; oRow < operandMatrix.length; oRow++) {
                for (int oCol = 0; oCol < operandMatrix[0].length; oCol++) {
                    double cellValue = 0;
                    for (int tCol = 0; tCol < transformationMatrix.length; tCol++) {
                        cellValue += (transformationMatrix[oRow][tCol] * operandMatrix[tCol][oCol]);
                    }
                    transformedMatrix[oRow][oCol] = cellValue;
                }
            }
            return transformedMatrix;
        } else {
            return operandMatrix;
        }
    }
}