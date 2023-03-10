import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class App {

    private UserProfile userProfile;

    private InvoiceRepository invoiceRepository;
    private CustomerRepository customerRepository;
    private InvoiceFactory invoiceFactory;
    private CustomerFactory customerFactory;

    public App() {
        this.customerRepository = new FileCustomerRepository();
        this.invoiceRepository = new FileInvoiceRepository();
        this.customerFactory = new CustomerFactory();
        this.invoiceFactory = new InvoiceFactory();

        initUserProfile();

        while (true) {
            showMenu();
            int input = receiveInput();
            handleUserChoice(input);
        }

    }

    private void initUserProfile() {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/userprofile.properties"));
            String name = properties.getProperty("name", "");
            String address = properties.getProperty("address", "");
            String orgNumber = properties.getProperty("orgNumber", "");
            String accountNumber = properties.getProperty("accountNumber", "");

            userProfile = new UserProfile(name, address, orgNumber, accountNumber);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IOException");
        }

    }

    private void handleUserChoice(int input) {

        switch (input) {
            case 1 -> createNewInvoice();
            case 2 -> viewAllInvoices();
            case 3 -> viewOverdueInvoices();
            case 4 -> addNewCustomer();
            case 5 -> markInvoiceAsPaid();
            case 6 -> sendInvoice();
            case 7 -> exitApp();
        }

    }

    private void exitApp() {
        System.exit(0);
    }

    private void sendInvoice() {
        viewAllInvoices();
        int input = receiveInput();
        Invoice invoice;
        String recipient = "albin.java@gmail.com";
        String text = "";
        String subject = "";

        for (Invoice i : invoiceRepository.readAll()) {
            if (i.getNumber() == input) {
                invoice = i;
                text = i.toString();
                subject = "Faktura " + Integer.toString(i.getNumber());
                break;
            }
        }
        System.out.println("<<<Sending invoice>>>");
        MailFacade mf = new MailFacade(recipient, subject, text);
        try {
            mf.sendMail();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void markInvoiceAsPaid() {
        System.out.println("<<<Marking invoice>>>");
        viewAllInvoices();
        int input = receiveInput();
        Invoice invoice;
        for (Invoice i : invoiceRepository.readAll()) {
            if (i.getNumber() == input) {
                invoice = i;
                invoiceRepository.update(invoice);
                System.out.println("Invoice " + invoice.getNumber() + "is now marked as paid.");
                break;
            }
        }
    }

    private void addNewCustomer() {
        System.out.println("<<<Adding new customer>>>");
        int id = customerRepository.readAll().size() + 1;
        Customer c = customerFactory.create(id);
        customerRepository.add(c);
    }

    private void viewOverdueInvoices() {
        System.out.println("<<<Viewing overdue invoices>>>");
        invoiceRepository.readOverdue().forEach(System.out::println);
    }

    private void viewAllInvoices() {
        System.out.println("<<<Viewing all invoices>>>");
        invoiceRepository.readAll().forEach(System.out::println);
    }

    private void createNewInvoice() {
        System.out.println("<<<Creating invoice>>>");
        int invoiceNum = invoiceRepository.readAll().size() + 1;
        Customer receiver = chooseReceiver();
        Invoice i = invoiceFactory.create(invoiceNum, userProfile, receiver);
        invoiceRepository.add(i);
        System.out.println("Invoice added!");
    }

    private Customer chooseReceiver() {
        List<Customer> cList = customerRepository.readAll();
        for (Customer c : cList) {
            System.out.println(c);
        }
        int userChoice = receiveInput();
        for (Customer c : cList) {
            if (c.getId() == userChoice) {
                return c;
            }
        }
        return null; //TODO: felhantering om id inte finns
    }


    public int receiveInput() {
        //TODO: fixa felhantering
        Scanner scanner = new Scanner(System.in);
        int input = 0;
        System.out.print("Enter choice: ");
        while (scanner.hasNextInt()) {
            input = scanner.nextInt();
            break;
        }
        return input;
    }

    public void showMenu() {

        System.out.println("""
                -------MENU--------
                1. Create invoice
                2. View all invoices
                3. View overdue invoices
                4. Add new customer
                5. Mark invoice as paid
                6. Send invoice
                7. Exit program
                """);
    }


    public static void main(String[] args) {
        new App();
    }

}
