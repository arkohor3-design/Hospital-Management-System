import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * Main console-based Hospital Management System
 * Features:
 * - Simple role-based login (ADMIN, DOCTOR, RECEPTIONIST)
 * - CRUD for Patients and Doctors
 * - Appointment booking with basic conflict check (same doctor & same datetime)
 * - Billing creation and marking as paid
 * - Serialization to files for persistence
 */

public class HospitalManagementSystem {
    private static final String USERS_FILE = "users.ser";
    private static final String PATIENTS_FILE = "patients.ser";
    private static final String DOCTORS_FILE = "doctors.ser";
    private static final String APPTS_FILE = "appointments.ser";
    private static final String BILLS_FILE = "bills.ser";

    private Map<String, User> users = new HashMap<>(); // username -> User
    private Map<String, Patient> patients = new HashMap<>(); // id -> Patient
    private Map<String, Doctor> doctors = new HashMap<>(); // id -> Doctor
    private Map<String, Appointment> appointments = new HashMap<>(); // id -> Appt
    private Map<String, Bill> bills = new HashMap<>(); // id -> Bill

    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public static void main(String[] args) {
        HospitalManagementSystem app = new HospitalManagementSystem();
        app.loadAll();
        app.ensureDefaultAdmin();
        app.run();
        app.saveAll();
    }

    private void run() {
        System.out.println("=== Hospital Management System (Console) ===");
        while (true) {
            try {
                if (currentUser == null) loginMenu();
                else mainMenu();
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    // ----- Persistence -----
    @SuppressWarnings("unchecked")
    private void loadAll() {
        users = (Map<String, User>) load(USERS_FILE, new HashMap<String, User>());
        patients = (Map<String, Patient>) load(PATIENTS_FILE, new HashMap<String, Patient>());
        doctors = (Map<String, Doctor>) load(DOCTORS_FILE, new HashMap<String, Doctor>());
        appointments = (Map<String, Appointment>) load(APPTS_FILE, new HashMap<String, Appointment>());
        bills = (Map<String, Bill>) load(BILLS_FILE, new HashMap<String, Bill>());
    }

    private void saveAll() {
        save(USERS_FILE, users);
        save(PATIENTS_FILE, patients);
        save(DOCTORS_FILE, doctors);
        save(APPTS_FILE, appointments);
        save(BILLS_FILE, bills);
        System.out.println("Data saved. Exiting.");
        System.exit(0);
    }

    private Object load(String filename, Object defaultObj) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (FileNotFoundException e) {
            return defaultObj;
        } catch (Exception e) {
            System.out.println("Failed to load " + filename + " : " + e.getMessage());
            return defaultObj;
        }
    }

    private void save(String filename, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(obj);
        } catch (Exception e) {
            System.out.println("Failed to save " + filename + " : " + e.getMessage());
        }
    }
    // ----- End persistence -----

    private void ensureDefaultAdmin() {
        if (!users.containsKey("admin")) {
            users.put("admin", new User("admin", "admin123", User.Role.ADMIN));
            System.out.println("Default admin created -> username: admin password: admin123");
        }
    }

    private void loginMenu() {
        System.out.println("\n1) Login  2) Exit");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        if (c.equals("1")) {
            System.out.print("Username: ");
            String u = scanner.nextLine().trim();
            System.out.print("Password: ");
            String p = scanner.nextLine().trim();
            User user = users.get(u);
            if (user != null && user.getPassword().equals(p)) {
                currentUser = user;
                System.out.println("Logged in as " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
            } else {
                System.out.println("Invalid credentials.");
            }
        } else {
            saveAll();
        }
    }

    private void mainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1) Patients  2) Doctors  3) Appointments  4) Billing  5) Users  6) Save & Exit  7) Logout");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": patientMenu(); break;
            case "2": doctorMenu(); break;
            case "3": appointmentMenu(); break;
            case "4": billingMenu(); break;
            case "5": userMenu(); break;
            case "6": saveAll(); break;
            case "7": currentUser = null; break;
            default: System.out.println("Invalid choice.");
        }
    }

    // ---------------- Patients ----------------
    private void patientMenu() {
        System.out.println("\n--- Patient Menu ---");
        System.out.println("1) Add Patient  2) View Patient  3) Update Patient  4) Delete Patient  5) List All  6) Back");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": addPatient(); break;
            case "2": viewPatient(); break;
            case "3": updatePatient(); break;
            case "4": deletePatient(); break;
            case "5": listPatients(); break;
            case "6": return;
            default: System.out.println("Invalid.");
        }
    }

    private void addPatient() {
        System.out.print("ID: "); String id = scanner.nextLine().trim();
        if (patients.containsKey(id)) { System.out.println("ID exists."); return; }
        System.out.print("Name: "); String name = scanner.nextLine().trim();
        System.out.print("Age: "); int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Contact: "); String contact = scanner.nextLine().trim();
        System.out.print("Gender: "); String gender = scanner.nextLine().trim();
        System.out.print("Medical History: "); String mh = scanner.nextLine().trim();
        Patient p = new Patient(id, name, age, contact, gender, mh);
        patients.put(id, p);
        System.out.println("Patient added.");
    }

    private void viewPatient() {
        System.out.print("Patient ID: "); String id = scanner.nextLine().trim();
        Patient p = patients.get(id);
        if (p == null) { System.out.println("Not found."); return; }
        p.display();
    }

    private void updatePatient() {
        System.out.print("Patient ID: "); String id = scanner.nextLine().trim();
        Patient p = patients.get(id);
        if (p == null) { System.out.println("Not found."); return; }
        System.out.print("New name (blank to keep): "); String name = scanner.nextLine().trim();
        if (!name.isEmpty()) p.setName(name);
        System.out.print("New age (blank to keep): "); String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) p.setAge(Integer.parseInt(ageStr));
        System.out.print("New contact (blank to keep): "); String contact = scanner.nextLine().trim();
        if (!contact.isEmpty()) p.setContact(contact);
        System.out.print("Update medical history (blank to keep): "); String mh = scanner.nextLine().trim();
        if (!mh.isEmpty()) p.setMedicalHistory(mh);
        System.out.println("Updated.");
    }

    private void deletePatient() {
        System.out.print("Patient ID: "); String id = scanner.nextLine().trim();
        if (patients.remove(id) != null) System.out.println("Deleted.");
        else System.out.println("Not found.");
    }

    private void listPatients() {
        if (patients.isEmpty()) { System.out.println("No patients."); return; }
        patients.values().forEach(Patient::display);
    }

    // ---------------- Doctors ----------------
    private void doctorMenu() {
        System.out.println("\n--- Doctor Menu ---");
        System.out.println("1) Add Doctor  2) View Doctor  3) Update Doctor  4) Delete Doctor  5) List All  6) Back");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": addDoctor(); break;
            case "2": viewDoctor(); break;
            case "3": updateDoctor(); break;
            case "4": deleteDoctor(); break;
            case "5": listDoctors(); break;
            case "6": return;
            default: System.out.println("Invalid.");
        }
    }

    private void addDoctor() {
        System.out.print("ID: "); String id = scanner.nextLine().trim();
        if (doctors.containsKey(id)) { System.out.println("ID exists."); return; }
        System.out.print("Name: "); String name = scanner.nextLine().trim();
        System.out.print("Age: "); int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Contact: "); String contact = scanner.nextLine().trim();
        System.out.print("Specialization: "); String spec = scanner.nextLine().trim();
        System.out.print("Availability: "); String avail = scanner.nextLine().trim();
        Doctor d = new Doctor(id, name, age, contact, spec, avail);
        doctors.put(id, d);
        // Create a user account for the doctor with default password
        String uname = "doc_" + id;
        users.put(uname, new User(uname, "pass123", User.Role.DOCTOR));
        System.out.println("Doctor added. Default login -> username: " + uname + " password: pass123");
    }

    private void viewDoctor() {
        System.out.print("Doctor ID: "); String id = scanner.nextLine().trim();
        Doctor d = doctors.get(id);
        if (d == null) { System.out.println("Not found."); return; }
        d.display();
    }

    private void updateDoctor() {
        System.out.print("Doctor ID: "); String id = scanner.nextLine().trim();
        Doctor d = doctors.get(id);
        if (d == null) { System.out.println("Not found."); return; }
        System.out.print("New name (blank keep): "); String name = scanner.nextLine().trim();
        if (!name.isEmpty()) d.setName(name);
        System.out.print("New specialization (blank keep): "); String s = scanner.nextLine().trim();
        if (!s.isEmpty()) d.setSpecialization(s);
        System.out.print("New availability (blank keep): "); String a = scanner.nextLine().trim();
        if (!a.isEmpty()) d.setAvailability(a);
        System.out.println("Updated.");
    }

    private void deleteDoctor() {
        System.out.print("Doctor ID: "); String id = scanner.nextLine().trim();
        if (doctors.remove(id) != null) System.out.println("Deleted.");
        else System.out.println("Not found.");
    }

    private void listDoctors() {
        if (doctors.isEmpty()) { System.out.println("No doctors."); return; }
        doctors.values().forEach(Doctor::display);
    }

    // ---------------- Appointments ----------------
    private void appointmentMenu() {
        System.out.println("\n--- Appointment Menu ---");
        System.out.println("1) Book Appointment  2) View Appointment  3) Cancel Appointment  4) List All  5) Back");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": bookAppointment(); break;
            case "2": viewAppointment(); break;
            case "3": cancelAppointment(); break;
            case "4": listAppointments(); break;
            case "5": return;
            default: System.out.println("Invalid.");
        }
    }

    private void bookAppointment() {
        System.out.print("Appointment ID: "); String id = scanner.nextLine().trim();
        if (appointments.containsKey(id)) { System.out.println("ID exists."); return; }
        System.out.print("Patient ID: "); String pid = scanner.nextLine().trim();
        if (!patients.containsKey(pid)) { System.out.println("Patient not found."); return; }
        System.out.print("Doctor ID: "); String did = scanner.nextLine().trim();
        if (!doctors.containsKey(did)) { System.out.println("Doctor not found."); return; }
        System.out.print("DateTime (yyyy-MM-dd HH:mm): "); String dtStr = scanner.nextLine().trim();
        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(dtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return;
        }
        // Basic conflict check: same doctor at same exact time
        boolean conflict = appointments.values().stream()
                .anyMatch(a -> a.getDoctorId().equals(did) && a.getDateTime().equals(dt));
        if (conflict) { System.out.println("Conflict: Doctor already has appointment at this time."); return; }
        System.out.print("Remarks: "); String rem = scanner.nextLine().trim();
        Appointment ap = new Appointment(id, pid, did, dt, rem);
        appointments.put(id, ap);
        System.out.println("Appointment booked.");
    }

    private void viewAppointment() {
        System.out.print("Appointment ID: "); String id = scanner.nextLine().trim();
        Appointment a = appointments.get(id);
        if (a == null) { System.out.println("Not found."); return; }
        a.display();
    }

    private void cancelAppointment() {
        System.out.print("Appointment ID: "); String id = scanner.nextLine().trim();
        if (appointments.remove(id) != null) System.out.println("Cancelled.");
        else System.out.println("Not found.");
    }

    private void listAppointments() {
        if (appointments.isEmpty()) { System.out.println("No appointments."); return; }
        appointments.values().stream()
                .sorted(Comparator.comparing(Appointment::getDateTime))
                .forEach(Appointment::display);
    }

    // ---------------- Billing ----------------
    private void billingMenu() {
        System.out.println("\n--- Billing Menu ---");
        System.out.println("1) Create Bill  2) View Bill  3) Mark Paid  4) List All  5) Back");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": createBill(); break;
            case "2": viewBill(); break;
            case "3": markPaid(); break;
            case "4": listBills(); break;
            case "5": return;
            default: System.out.println("Invalid.");
        }
    }

    private void createBill() {
        System.out.print("Bill ID: "); String id = scanner.nextLine().trim();
        if (bills.containsKey(id)) { System.out.println("ID exists."); return; }
        System.out.print("Patient ID: "); String pid = scanner.nextLine().trim();
        if (!patients.containsKey(pid)) { System.out.println("Patient not found."); return; }
        System.out.print("Consultation Fee: "); double cf = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Room charge (0 if none): "); double rc = Double.parseDouble(scanner.nextLine().trim());
        System.out.println("Enter medicines one per line as 'name,price' blank line to finish:");
        Map<String, Double> meds = new HashMap<>();
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) break;
            String[] parts = line.split(",");
            if (parts.length != 2) { System.out.println("Invalid format. Use name,price"); continue; }
            try {
                meds.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
            }
        }
        Bill b = new Bill(id, pid, cf, meds, rc);
        bills.put(id, b);
        System.out.println("Bill created. Total: " + b.total());
    }

    private void viewBill() {
        System.out.print("Bill ID: "); String id = scanner.nextLine().trim();
        Bill b = bills.get(id);
        if (b == null) { System.out.println("Not found."); return; }
        b.display();
    }

    private void markPaid() {
        System.out.print("Bill ID: "); String id = scanner.nextLine().trim();
        Bill b = bills.get(id);
        if (b == null) { System.out.println("Not found."); return; }
        b.setPaid(true);
        System.out.println("Marked as paid.");
    }

    private void listBills() {
        if (bills.isEmpty()) { System.out.println("No bills."); return; }
        bills.values().forEach(Bill::display);
    }

    // ---------------- Users (Admin only) ----------------
    private void userMenu() {
        if (currentUser.getRole() != User.Role.ADMIN) {
            System.out.println("Only ADMIN can manage users.");
            return;
        }
        System.out.println("\n--- User Management ---");
        System.out.println("1) Add User  2) List Users  3) Delete User  4) Back");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": addUser(); break;
            case "2": listUsers(); break;
            case "3": deleteUser(); break;
            case "4": return;
            default: System.out.println("Invalid.");
        }
    }

    private void addUser() {
        System.out.print("Username: "); String u = scanner.nextLine().trim();
        if (users.containsKey(u)) { System.out.println("Username exists."); return; }
        System.out.print("Password: "); String p = scanner.nextLine().trim();
        System.out.print("Role (ADMIN/DOCTOR/RECEPTIONIST): "); String r = scanner.nextLine().trim();
        User.Role role;
        try {
            role = User.Role.valueOf(r.toUpperCase());
        } catch (Exception e) {
            System.out.println("Invalid role.");
            return;
        }
        users.put(u, new User(u, p, role));
        System.out.println("User added.");
    }

    private void listUsers() {
        System.out.println("Users:");
        users.values().forEach(u -> System.out.println(u.getUsername() + " : " + u.getRole()));
    }

    private void deleteUser() {
        System.out.print("Username to delete: "); String u = scanner.nextLine().trim();
        if ("admin".equals(u)) { System.out.println("Cannot delete default admin."); return; }
        if (users.remove(u) != null) System.out.println("Deleted.");
        else System.out.println("Not found.");
    }
}