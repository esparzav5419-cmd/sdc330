/*
 * Name: valentin esparza
 * Date: 10/25/26
 * Purpose: App is the entry point for the Week 3 demo. It displays the
 *          banner and welcome message, creates employees using several
 *          different constructors, and shows ABSTRACTION, CONSTRUCTORS,
 *          and ACCESS SPECIFIERS at work by displaying the results.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        PayrollProcessor payroll = new PayrollProcessor();

        ui.showBanner("PROJECT WEEK 3", "Abstraction, Constructors & Access Specifiers",
                "Valentin Esparza");
        ui.showWelcome();

        // CONSTRUCTORS: each employee below is created with a different
        // constructor so the demo shows the overloaded versions in use.
        Address mariaAddress = new Address("412 Granby St", "Norfolk", "VA", "23510");
        Employee maria = new HourlyEmployee(1001, "Maria Santos",
                mariaAddress, 24.50, 45);                       // explicit ID
        Employee james = new SalariedEmployee("James Whitaker",
                new Address("88 Atlantic Ave", "Virginia Beach", "VA", "23451"),
                78000);                                         // automatic ID
        Employee priya = new CommissionEmployee("Priya Patel",
                new Address("19 Colley Ave", "Norfolk", "VA", "23517"),
                2500, 40000, 0.05);                             // automatic ID
        Employee daniel = new HourlyEmployee("Daniel Brooks",
                new Address("Chesapeake", "VA"), 18.75);        // standard 40 hours
        Employee aisha = new CommissionEmployee("Aisha Rahman",
                new Address(), 2000);                           // new hire, no address yet

        // ABSTRACTION: Employee is abstract, so the line below would not
        // compile. Only the concrete child classes can be created.
        // Employee e = new Employee("Test", new Address());   // ERROR

        // POLYMORPHISM: one array holds every kind of employee.
        Employee[] employees = { maria, james, priya, daniel, aisha };

        ui.showHeader("Employee Summary");
        ui.showEmployeeTable(employees);

        ui.showHeader("Abstraction in Action");
        ui.showMessage("Employee is abstract, so 'new Employee(...)' will not compile.");
        ui.showMessage("Each child class must supply its own getType(), calculatePay(),");
        ui.showMessage("and getPayDescription(), which the payroll report calls below.");

        ui.showHeader("Constructors in Action");
        ui.showMessage(String.format("%-15s %s", maria.getName(), "Hourly: full constructor, explicit ID " + maria.getId()));
        ui.showMessage(String.format("%-15s %s", james.getName(), "Salaried: automatic ID " + james.getId()));
        ui.showMessage(String.format("%-15s %s", priya.getName(), "Commission: automatic ID " + priya.getId()));
        ui.showMessage(String.format("%-15s %s", daniel.getName(), "Hourly: automatic ID " + daniel.getId() + ", standard 40 hours"));
        ui.showMessage(String.format("%-15s %s", aisha.getName(), "Commission: automatic ID " + aisha.getId() + ", default Address()"));
        Address copy = new Address(mariaAddress); // copy constructor
        ui.showMessage("Address copy constructor: " + copy);

        // INTERFACE + POLYMORPHISM from Week 2, now working with the
        // abstract Employee class.
        List<Payable> payables = new ArrayList<>();
        payables.addAll(Arrays.asList(employees));
        ui.showHeader("Payroll Report");
        ui.showPayroll(payables, payroll.totalPay(payables));
    }
}
