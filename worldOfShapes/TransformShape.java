package worldOfShapes;

import utils.math;

public abstract class TransformShape {

    // base class for transforming and returning a shape
    private static Shape transformShape(Shape shape, double[][] transformationMatrix) {
        double[][] operandMatrix = shape.getCoordinates();
        if (math.validMatrixMultiplication(transformationMatrix, operandMatrix)) {
            double[][] transformedMatrix = math.multiplyMatrices(transformationMatrix, operandMatrix);
            double[] x_coords = transformedMatrix[0];
            double[] y_coords = transformedMatrix[1];
            Shape shapeToBeReturned = new Shape(x_coords, y_coords);
            return shapeToBeReturned;
        }
        return shape;
    }

    // most employ matrix multiplication using transformation matrices
    public static Shape scale(Shape shape, double xfactor, double yfactor) {
        double[][] transformationMatrix = { { xfactor, 0 }, { 0, yfactor } };
        return transformShape(shape, transformationMatrix);
    }

    // problems
    public static Shape shear(Shape shape, double xfactor, double yfactor) {
        double[][] transformationMatrix = { { 1, xfactor }, { yfactor, 1 } };
        return transformShape(shape, transformationMatrix);
    }

    public static Shape reflect(Shape shape, String axis) {
        axis = axis.trim().toLowerCase();
        double[][] transformationMatrix = { { 1, 0 }, { 0, 1 } };
        switch (axis) {
            case "x":
                transformationMatrix[1][1] = -1;
                break;
            case "y":
                transformationMatrix[0][0] = -1;
                break;
            case "y=x":
                transformationMatrix[0][0] = 0;
                transformationMatrix[0][1] = 1;
                transformationMatrix[1][0] = 1;
                transformationMatrix[1][1] = 0;
                break;
            case "y=-x":
                transformationMatrix[0][0] = 0;
                transformationMatrix[0][1] = -1;
                transformationMatrix[1][0] = -1;
                transformationMatrix[1][1] = 0;
                break;
            default:
                System.out.println("Only \"x\", \"y\", \"y=x\" and \"y=-x\" are allowed!");
        }
        return transformShape(shape, transformationMatrix);
    }

    public static Shape rotateAboutOrigin(Shape shape, double angleInDegrees) {
        double[][] transformationMatrix = {
                { Math.cos(math.angleRad(angleInDegrees)), 0 - Math.sin(math.angleRad(angleInDegrees)) },
                { Math.sin(math.angleRad(angleInDegrees)), Math.cos(math.angleRad(angleInDegrees)) } };
        return transformShape(shape, transformationMatrix);
    }

    public static Shape translate(Shape shape, double xfactor, double yfactor) {
        // iterate over each element, either adding to or subtracting from the cell's value
        double[] x_coords = shape.getCoordinates()[0];
        double[] y_coords = shape.getCoordinates()[1];
        for (int i = 0; i < x_coords.length; i++) {
            x_coords[i] += xfactor;
            y_coords[i] += yfactor;
        }
        shape.setCoordinates(x_coords, y_coords);
        return shape;
    }
}