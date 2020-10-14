package worldOfShapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ShapesInAction {

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // main class for worldOfShapes package
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean run = true;
        int runNum = 1;
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        do {
            try {
                clearScreen();
                System.out.println("Enter coordinates:");
                System.out.println("(in the form x1, y1, x2, y2, ...)");
                if (runNum > 1) {
                    input.nextLine();
                }
                String coordinatesString = input.nextLine();
                ArrayList<Double> x_coords = new ArrayList<Double>();
                ArrayList<Double> y_coords = new ArrayList<Double>();
                String[] pointsStrings = coordinatesString.trim().split(", ");
                for (int point = 0; point < pointsStrings.length; point++) {
                    if (point % 2 == 0) {
                        double x_coord = Double.parseDouble(pointsStrings[point]);
                        x_coords.add(x_coord);
                    } else {
                        double y_coord = Double.parseDouble(pointsStrings[point]);
                        y_coords.add(y_coord);
                    }
                }
                double[][] coordinates = new double[2][x_coords.size()];
                for (int i = 0; i < coordinates[0].length; i++) {
                    coordinates[0][i] = x_coords.get(i);
                    coordinates[1][i] = y_coords.get(i);
                }
                Shape shape = new Shape(coordinates[0], coordinates[1]);
                shapes.add(shape);
                shape.printAttributes();
                boolean modify;
                if (shape.isSimple) {
                    System.out.println("\nDo you wish to modify your shape?");
                    modify = (input.next().trim().toLowerCase().equals("yes"));
                }
                else {
                    modify = false;
                }
                if (modify) {
                    do {
                        System.out.println("How do you wish to proceed?\n1) Reflect\n2) Rotate (about Origin)\n3) Scale\n4) Shear\n5) Translate\n6) Exit...\n>");
                        int action = input.nextInt();
                        Shape s;
                        switch (action) {
                            case 1:
                                System.out.println("Axis:");
                                String axis = input.next().trim().toLowerCase();
                                s = TransformShape.reflect(shape, axis);
                                break;
                            case 2:
                                System.out.println("Angle (anticlockwise; in degrees):");
                                double angle = input.nextDouble();
                                s = TransformShape.rotateAboutOrigin(shape, angle);
                                break;
                            case 3:
                                System.out.println("X-factor: ");
                                double xscale = input.nextDouble();
                                System.out.println("Y-factor: ");
                                double yscale = input.nextDouble();
                                s = TransformShape.scale(shape, xscale, yscale);
                                break;
                            case 4:
                                System.out.println("X-factor: ");
                                double xshear = input.nextDouble();
                                System.out.println("Y-factor: ");
                                double yshear = input.nextDouble();
                                s = TransformShape.shear(shape, xshear, yshear);
                                break;
                            case 5:
                                System.out.println("X-factor: ");
                                double xtranslate = input.nextDouble();
                                System.out.println("Y-factor: ");
                                double ytranslate = input.nextDouble();
                                s = TransformShape.translate(shape, xtranslate, ytranslate);
                                break;
                            case 6:
                                modify = false;
                                s = shape;
                                break;
                            default:
                                System.out.println("\nPlease enter an integer from 1 to 6 only!\n");
                                s = shape;
                        }
                        if (action >= 1 && action <= 5) {
                            shapes.add(s);
                            shape = s;
                            shape.printAttributes();
                            System.out.println(Arrays.deepToString(s.getCoordinates()));
                        }
                    } while (modify);
                }
            } catch (Exception e) {
                System.out.println("Invalid input!");
            }
            System.out.println("\nDo you wish to continue?");
            run = (input.next().trim().toLowerCase().equals("yes"));
            runNum++;
        } while (run);
        System.out.println("This session has ended. :)");
        System.out.println("History:\n");
        for (Shape s : shapes) {
            s.printAttributes();
            System.out.println();
        }
        input.close();
    }
}