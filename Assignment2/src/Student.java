public class Student extends Person {
    private static int studentIdCounter = 1;
    private final int id;
    private double gpa;
    private static final double stipendAmount = 36660.00;
    private static final double minGpa = 2.67;

    public Student() {
        super();
        this.id = studentIdCounter++;
        this.gpa = 0.0;
    }

    public Student(String name, String surname, double gpa) {
        super(name, surname);
        this.id = studentIdCounter++;
        this.gpa = gpa;
    }

    @Override
    public String toString() {
        return "Student: " + super.toString();
    }

    @Override
    public double getPaymentAmount() {
        if (gpa > minGpa) {
            return stipendAmount;
        }
        return 0.00;
    }

    @Override
    public int getId() {
        return id;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    @Override
    public int compareTo(Person other) {
        return Double.compare(this.getPaymentAmount(), other.getPaymentAmount());
    }
}
