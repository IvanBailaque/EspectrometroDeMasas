public class Vector3D {
    private double x;
    private double y;
    private double z;

    // Constructor
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Método para el producto vectorial
    public Vector3D crossProduct(Vector3D other) {
        double crossX = this.y * other.z - this.z * other.y;
        double crossY = this.z * other.x - this.x * other.z;
        double crossZ = this.x * other.y - this.y * other.x;

        return new Vector3D(crossX, crossY, crossZ);
    }

    // Métodos getters para acceder a los componentes
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Vector3D(" + x + ", " + y + ", " + z + ")";
    }

    // Método principal para pruebas
    public static void main(String[] args) {
        Vector3D v1 = new Vector3D(1, 2, 3);
        Vector3D v2 = new Vector3D(4, 5, 6);

        Vector3D crossProduct = v1.crossProduct(v2);

        System.out.println("El producto vectorial de " + v1 + " y " + v2 + " es " + crossProduct);
    }
}