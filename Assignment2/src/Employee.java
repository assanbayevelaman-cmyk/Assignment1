public class Employee extends Person {
    private static int employeeIdCounter = 1;
    private final int id;
    private String position;
    private double salary;

    public Employee() {
        super();
        this.id = employeeIdCounter++;
        this.position = "";
        this.salary = 0.0;
    }

    public Employee(String name, String surname, String position, double salary) {
        super(name, surname);
        this.id = employeeIdCounter++;
        this.position = position;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee: " + super.toString();
    }

    @Override
    public String getPosition() {
        return position;
    }

    @Override
    public double getPaymentAmount() {
        return salary;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getPositionField() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public int compareTo(Person other) {
        return Double.compare(this.getPaymentAmount(), other.getPaymentAmount());
    }
}

