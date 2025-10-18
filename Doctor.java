
public class Doctor extends Person {
    private static final long serialVersionUID = 1L;
    private String specialization;
    private String availability; // e.g., "Mon-Fri 9-5" or simple text

    public Doctor(String id, String name, int age, String contact, String specialization, String availability) {
        super(id, name, age, contact);
        this.specialization = specialization;
        this.availability = availability;
    }

    public String getSpecialization() { return specialization; }
    public String getAvailability() { return availability; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setAvailability(String availability) { this.availability = availability; }

    @Override
    public void display() {
        System.out.println("Doctor ID: " + id + ", Name: " + name + ", Spec: " + specialization + ", Contact: " + contact + ", Availability: " + availability);
    }
}