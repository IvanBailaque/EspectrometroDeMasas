public class Physics {
    // Constants
    protected static final double CHARGE = 1.6e-19; // example charge
    protected static final double MASS = 9.11e-31;  // example mass of an electron

    public static double calculateForce(double charge, double field) {
        return charge * field;
    }

    public static double calculateAcceleration(double force, double mass) {
        return force / mass;
    }

    public static double[] calculateNextPosition(double[] position, double[] velocity, double timeStep) {
        double[] newPosition = new double[2];
        newPosition[0] = position[0] + velocity[0] * timeStep;
        newPosition[1] = position[1] + velocity[1] * timeStep;
        return newPosition;
    }
}
