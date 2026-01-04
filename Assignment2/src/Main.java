import java.util.ArrayList;
import java.util.Collections;
public class Main {
    public static void printData(Iterable<Person> persons) {
        for (Person person : persons) {
            String output = person.toString();

            if (person instanceof Employee) {
                output += " earns " + String.format("%.2f", person.getPaymentAmount()) + " tenge";
            } else if (person instanceof Student) {
                output += " earns " + String.format("%.2f", person.getPaymentAmount()) + " tenge";
            }

            System.out.println(output);
        }
    }

    public static void main(String[] args) {
        Employee employee1 = new Employee("John", "Lennon", "Software Engineer", 27045.78);
        Employee employee2 = new Employee("George", "Harrison", "Project Manager", 50000.00);
        Employee employee3 = new Employee("Mick", "Jagger", "Team Lead", 45000.00);
        Student student1 = new Student("Ringo", "Starr", 2.5);
        Student student2 = new Student("Paul", "McCartney", 3.8);
        Student student3 = new Student("Freddie", "Mercury", 2.67);
        Student student4 = new Student("David", "Bowie", 3.5);
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(employee1);
        persons.add(employee2);
        persons.add(employee3);
        persons.add(student1);
        persons.add(student2);
        persons.add(student3);
        persons.add(student4);

        System.out.println("Before sorting: ");
        printData(persons);

        Collections.sort(persons, Collections.reverseOrder());
        System.out.println("After sorting by payment amount (descending): ");
        printData(persons);
    }
}
