package worldOfShapes;

import java.util.ArrayList;
import java.util.Arrays;
import utils.math;

public class Shape {

    // fields for shape's attributes
    private double[] x_coords;
    private double[] y_coords;
    private double[] lengths;
    private double[] angles;
    private double area;
    private String family;
    private String type;
    protected boolean isSimple;

    // polymorphed constructor
    public Shape(double[] rawx_coords, double[] rawy_coords) {
        setCoordinates(rawx_coords, rawy_coords);
    }

    public Shape(double[][] coordinates) {
        setCoordinates(coordinates);
    }

    // calculation
    private static double[] calculateLengths(double[] x_coords, double[] y_coords) {
        // calculating lengths of shape by Pythagoras' Theorem
        double[] lengths = new double[x_coords.length];
        for (int i = 0, j; i < lengths.length; i++) {
            if (i == lengths.length - 1) {
                j = 0;
            } else {
                j = i + 1;
            }
            double length = Math
                    .sqrt(Math.pow((x_coords[i] - x_coords[j]), 2) + Math.pow((y_coords[i] - y_coords[j]), 2));
            lengths[i] = length;
        }
        return lengths;
    }

    private static double[] calculateLengths(double[] x_coords, double[] y_coords, int dp) {
        return math.roundArrOfTo(calculateLengths(x_coords, y_coords), dp);
    }

    private static double[] calculateAnglesInDegrees(double[] lengths, double[] x_coords, double[] y_coords) {
        // calculating angles of shape (using Cosine rule) - can be simplified
        double[] anglesInDegrees = new double[x_coords.length];
        for (int i = 0; i < x_coords.length; i++) {
            int j;
            int k;
            if (i == x_coords.length - 1) {
                j = 1;
                k = 0;
            } else if (i == x_coords.length - 2) {
                j = 0;
                k = i + 1;
            } else {
                j = i + 2;
                k = i + 1;
            }
            double a2 = (Math.pow((x_coords[i] - x_coords[j]), 2) + Math.pow((y_coords[i] - y_coords[j]), 2));
            double b = lengths[i];
            double b2 = (Math.pow(b, 2));
            double c = lengths[k];
            double c2 = (Math.pow(c, 2));
            double angleInRadians = Math.acos((b2 + c2 - a2) / (2 * b * c));
            anglesInDegrees[i] = math.angleDeg(angleInRadians);
        }
        return anglesInDegrees;
    }

    private static double calculateArea(double[] x_coords, double[] y_coords, int dp) {
        // calculate area of shape using Geometrical Shoelace Method
        double area = 0;

        // setting up the shoelace method
        double[] x_coords_shoelace = new double[x_coords.length + 1];
        double[] y_coords_shoelace = new double[y_coords.length + 1];

        for (int i = 0; i < x_coords.length; i++) {
            x_coords_shoelace[i] = x_coords[i];
            y_coords_shoelace[i] = y_coords[i];
        }
        x_coords_shoelace[x_coords.length] = x_coords[0];
        y_coords_shoelace[y_coords.length] = y_coords[0];

        // calculating area...
        for (int i = 0; i < x_coords.length; i++) {
            area += (x_coords_shoelace[i] * y_coords_shoelace[i + 1]);
            area -= (y_coords_shoelace[i] * x_coords_shoelace[i + 1]);
        }
        area = math.roundOfTo(0.5 * Math.abs(area), dp);
        return area;
    }

    // searches
    private static int[] findRedundantPoints(double[] x_coords, double[] y_coords) {
        // finding redundant points by altered area
        // --if area without one point yields the same value as the area with the point,
        // then that particular point is redundant (intraline or consecutive duplicate)--
        try {
            ArrayList<Integer> indicesOfRedundantPointsAL = new ArrayList<Integer>();
            double[] x_coordsWithoutPoint = new double[x_coords.length - 1];
            double[] y_coordsWithoutPoint = new double[y_coords.length - 1];
            double areaWithoutPoint;
            double xToBeRemoved;
            double yToBeRemoved;
            double previousx;
            double previousy;
            double calculatedArea = calculateArea(x_coords, y_coords, 6);
            for (int indexOfPointToBeRemoved = 0; indexOfPointToBeRemoved < x_coords.length; indexOfPointToBeRemoved++) {
                int[] indexOfPointToBeRemovedArr = { indexOfPointToBeRemoved };
                // finding altered area
                x_coordsWithoutPoint = removeFromArray(x_coords, indexOfPointToBeRemovedArr);
                y_coordsWithoutPoint = removeFromArray(y_coords, indexOfPointToBeRemovedArr);
                areaWithoutPoint = calculateArea(x_coordsWithoutPoint, y_coordsWithoutPoint, 6);
                xToBeRemoved = x_coords[indexOfPointToBeRemoved];
                yToBeRemoved = y_coords[indexOfPointToBeRemoved];
                if (indexOfPointToBeRemoved == 0) {
                    previousx = x_coords[x_coords.length - 1];
                    previousy = y_coords[y_coords.length - 1];
                } else {
                    previousx = x_coords[indexOfPointToBeRemoved - 1];
                    previousy = y_coords[indexOfPointToBeRemoved - 1];
                }
                if (areaWithoutPoint == calculatedArea && !(xToBeRemoved == previousx && yToBeRemoved == previousy)) {
                    indicesOfRedundantPointsAL.add(indexOfPointToBeRemoved);
                }
            }
            // converting arraylist to array
            int[] indicesOfRedundantPoints = new int[indicesOfRedundantPointsAL.size()];
            for (int i = 0; i < indicesOfRedundantPoints.length; i++) {
                indicesOfRedundantPoints[i] = indicesOfRedundantPointsAL.get(i);
            }
            return indicesOfRedundantPoints;
        } catch (NegativeArraySizeException e) {
            System.out.println("Shape cannot have fewer than 3 sides!");
            return new int[0];
        }
    }

    private static int[] findConcavePoints(double[] x_coords, double[] y_coords) {
        // finding concave points by deficit in area
        // (if area of the shape without that particular point yields a larger value,
        // point is depressed)
        ArrayList<Integer> indicesOfConcavePointsAL = new ArrayList<Integer>();
        double[] x_coordsWithoutPoint = new double[x_coords.length - 1];
        double[] y_coordsWithoutPoint = new double[y_coords.length - 1];
        double areaWithoutPoint;
        double calculatedArea = calculateArea(x_coords, y_coords, 6);
        for (int indexOfPointToBeRemoved = 0; indexOfPointToBeRemoved < x_coords.length; indexOfPointToBeRemoved++) {
            int[] indexOfPointToBeRemovedArr = { indexOfPointToBeRemoved };
            // finding altered area
            x_coordsWithoutPoint = removeFromArray(x_coords, indexOfPointToBeRemovedArr);
            y_coordsWithoutPoint = removeFromArray(y_coords, indexOfPointToBeRemovedArr);
            areaWithoutPoint = calculateArea(x_coordsWithoutPoint, y_coordsWithoutPoint, 6);
            if (areaWithoutPoint > calculatedArea) {
                // because the angle of the corresponding coords is offset by one index
                if (indexOfPointToBeRemoved == 0) {
                    indicesOfConcavePointsAL.add(x_coords.length - 1);
                } else {
                    indicesOfConcavePointsAL.add(indexOfPointToBeRemoved - 1);
                }
            }
        }
        // converting arraylist to array
        int[] indicesOfConcavePoints = new int[indicesOfConcavePointsAL.size()];
        for (int i = 0; i < indicesOfConcavePoints.length; i++) {
            indicesOfConcavePoints[i] = indicesOfConcavePointsAL.get(i);
        }
        return indicesOfConcavePoints;
    }

    private static String findFamily(double[] lengths) {
        // using the number of sides to determine the family of the shape
        String family;
        switch (lengths.length) {
            case 3:
                family = "triangle";
                break;
            case 4:
                family = "quadrilateral";
                break;
            case 5:
                family = "pentagon";
                break;
            case 6:
                family = "hexagon";
                break;
            case 7:
                family = "heptagon";
                break;
            case 8:
                family = "octagon";
                break;
            case 9:
                family = "nonagon";
                break;
            case 10:
                family = "decagon";
                break;
            default:
                family = "polygon";
        }
        return family;
    }

    private static String findType(double[] lengths, double[] anglesInDegrees, double[] x_coords, double[] y_coords,
            String family) {
        // finding the specific type of the shape through significant properties
        String type = "";

        switch (family) {
            case "triangle":
                // angle-wise classification
                // acute
                String angleType;
                if (anglesInDegrees[0] < 90 && anglesInDegrees[1] < 90 && anglesInDegrees[2] < 90) {
                    angleType = "acute";
                }
                // right
                else if (anglesInDegrees[0] == 90 || anglesInDegrees[1] == 90 || anglesInDegrees[2] == 90) {
                    angleType = "right";
                }
                // obtuse
                else {
                    angleType = "obtuse";
                }
                // side-wise classification
                // equilateral
                String sideType;
                // equilateral
                if (lengths[0] == lengths[1] && lengths[1] == lengths[2] && lengths[2] == lengths[0]) {
                    sideType = "equilateral";
                }
                // isosceles
                else if (lengths[0] == lengths[1] || lengths[1] == lengths[2] || lengths[2] == lengths[0]) {
                    sideType = "isosceles";
                }
                // scalene
                else {
                    sideType = "scalene";
                }
                type = angleType + " " + sideType;
                break;
            case "quadrilateral":
                // square
                if (math.equalElements(lengths) && math.equalElements(anglesInDegrees)) {
                    type = "square";
                }
                // rhombus
                else if (math.equalElements(lengths)) {
                    type = "rhombus";
                }
                // rectangle
                else if (math.equalElements(anglesInDegrees)) {
                    type = "rectangle";
                }
                // parallelogram
                else if (lengths[0] == lengths[2] && lengths[1] == lengths[3]) {
                    type = "parallelogram";
                }
                // kite / dart
                else if ((lengths[0] == lengths[1] && lengths[2] == lengths[3])
                        || (lengths[0] == lengths[3] && lengths[1] == lengths[2])) {
                    if (findConcavePoints(x_coords, y_coords).length > 0) {
                        type = "dart";
                    } else {
                        type = "kite";
                    }
                }
                // trapezium
                else if ((anglesInDegrees[0] + anglesInDegrees[1] == 180)
                        || (anglesInDegrees[1] + anglesInDegrees[2] == 180)) {
                    type = "trapezium";
                }
                // irregular / arrow
                else {
                    if (findConcavePoints(x_coords, y_coords).length > 0) {
                        type = "arrow";
                    } else {
                        type = "irregular";
                    }
                }
                break;
            default:
                // concavity-wise classification
                // convex by default
                String concavity = "convex";
                // concave
                if (findConcavePoints(x_coords, y_coords).length > 0) {
                    concavity = "concave";
                }
                // side-wise classification
                // regular
                if (math.equalElements(lengths) && math.equalElements(anglesInDegrees)) {
                    sideType = "regular";
                }
                // equilateral
                else if (math.equalElements(lengths)) {
                    sideType = "equilateral";
                }
                // irregular
                else {
                    sideType = "irregular";
                }
                type = concavity + " " + sideType;
        }
        return type;
    }

    // alteration
    // removal
    private static double[] removeFromArray(double[] array, int[] indicesOfElementsToBeRemoved) {
        // to remove a set of elements from arrays
        ArrayList<Double> trimmedAL = new ArrayList<Double>(array.length - indicesOfElementsToBeRemoved.length);
        Arrays.sort(indicesOfElementsToBeRemoved);
        for (int i = 0; i < array.length; i++) {
            if (Arrays.binarySearch(indicesOfElementsToBeRemoved, i) < 0) {
                trimmedAL.add(array[i]);
            }
        }
        double[] trimmedArray = new double[trimmedAL.size()];
        for (int i = 0; i < trimmedAL.size(); i++) {
            trimmedArray[i] = trimmedAL.get(i);
        }
        return trimmedArray;
    }

    // conversion
    private static double[] convertToConcave(double[] anglesInDegrees, int[] indicesOfConcavePoints) {
        // to convert convex points to concave
        Arrays.sort(indicesOfConcavePoints);
        double[] concaveAnglesInDegrees = anglesInDegrees;
        for (int i = 0; i < anglesInDegrees.length; i++) {
            if (Arrays.binarySearch(indicesOfConcavePoints, i) >= 0) {
                concaveAnglesInDegrees[i] = 360 - anglesInDegrees[i];
            }
        }
        return concaveAnglesInDegrees;
    }

    protected static double[][] convertToSeparateCoordinatesSets(double[][] coordinates) {
        double[][] coordinates_x_y = new double[2][coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates_x_y[0][i] = coordinates[i][0];
            coordinates_x_y[1][i] = coordinates[i][1];
        }
        return coordinates_x_y;
    }

    protected static double[][] convertToCollapsedCoordinatesSet(double[] x_coords, double[] y_coords) {
        double[][] coords = new double[x_coords.length][2];
        for (int i = 0; i < x_coords.length; i++) {
            coords[i][0] = x_coords[i];
            coords[i][1] = y_coords[i];
        }
        return coords;
    }

    // printing properties
    public void printAttributes() {
        if (this.isSimple) {
            String lengthsStr = "Lengths:\t";
            String anglesStr = "Angles:\t\t";
            String areaStr = "Area:\t\t" + this.area;
            for (int i = 0; i < lengths.length; i++) {
                lengthsStr += lengths[i] + ", ";
                anglesStr += this.angles[i] + ", ";
            }
            System.out.println((lengthsStr + "\b\b \n" + anglesStr + "\b\b \n" + areaStr + "\nFamily:\t\t" + this.family
                    + "\nType:\t\t" + this.type));
        } else {
            System.out.println("Complex shape; cannot compute...");
        }
    }

    // encapsulation
    // coodinates have to be evaluated before attributes are calculated
    protected void setCoordinates(double[] rawx_coords, double[] rawy_coords) {
        // attributes of shape
        double[] x_coords = rawx_coords;
        double[] y_coords = rawy_coords;
        double[] lengths;
        double[] anglesInDegrees;

        double area;
        String family;
        String type;

        try {
            do {
                // removing redundant points
                int[] redundantPoints = findRedundantPoints(x_coords, y_coords);
                x_coords = removeFromArray(x_coords, redundantPoints);
                y_coords = removeFromArray(y_coords, redundantPoints);
            } while (findRedundantPoints(x_coords, y_coords).length > 0);

            // assigning initial values to attributes
            lengths = calculateLengths(x_coords, y_coords, 2);
            anglesInDegrees = calculateAnglesInDegrees(lengths, x_coords, y_coords);
            area = calculateArea(x_coords, y_coords, 2);

            // validating coordinates
            if (x_coords.length == y_coords.length && lengths.length >= 3) {

                x_coords = math.roundArrOfTo(x_coords, 4);
                y_coords = math.roundArrOfTo(y_coords, 4);

                // angles (for concave if applicable)
                anglesInDegrees = convertToConcave(anglesInDegrees, findConcavePoints(x_coords, y_coords));

                // not accurate - Pi is only precise to the 15th decimal place
                this.isSimple = ((math.sumOf(anglesInDegrees) <= ((lengths.length - 2) * 180 + 10))
                        && (math.sumOf(anglesInDegrees) >= ((lengths.length - 2) * 180 - 10)));

                anglesInDegrees = math.roundArrOfTo(anglesInDegrees, 2);
                family = findFamily(lengths);
                type = findType(lengths, anglesInDegrees, x_coords, y_coords, family);

                // mandatory fields
                this.x_coords = x_coords;
                this.y_coords = y_coords;
                this.lengths = lengths;
                if (this.isSimple) {
                    this.angles = anglesInDegrees;
                    this.area = area;
                    this.family = family;
                    this.type = type;
                }
            } else if (x_coords.length != y_coords.length) {
                System.out.println("Number of x-coordinates doesn't match number of y-coordinates! Invalid Shape!");
            } else {
                System.out.println("Shape cannot have fewer than 3 edges or nodes! Invalid Shape!");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            this.x_coords = x_coords;
            this.y_coords = y_coords;
            System.out.println("Shape cannot have fewer than 3 sides!");
        }
    }

    protected void setCoordinates(double[][] coordinates) {
        double[] x_coords = convertToSeparateCoordinatesSets(coordinates)[0];
        double[] y_coords = convertToSeparateCoordinatesSets(coordinates)[1];
        setCoordinates(x_coords, y_coords);
    }

    // coordinates are read-only
    protected double[][] getCoordinates() {
        return convertToSeparateCoordinatesSets(convertToCollapsedCoordinatesSet(this.x_coords, this.y_coords));
    }
}