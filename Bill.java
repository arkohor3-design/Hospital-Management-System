import java.io.Serializable;
import java.util.Map;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String patientId;
    private double consultationFee;
    private Map<String, Double> medicines; // medicineName -> price
    private double roomCharge;
    private boolean paid;

    public Bill(String id, String patientId, double consultationFee, Map<String, Double> medicines, double roomCharge) {
        this.id = id;
        this.patientId = patientId;
        this.consultationFee = consultationFee;
        this.medicines = medicines;
        this.roomCharge = roomCharge;
        this.paid = false;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public double total() {
        double medTotal = 0;
        if (medicines != null) {
            for (double p : medicines.values()) medTotal += p;
        }
        return consultationFee + medTotal + roomCharge;
    }

    public void display() {
        System.out.println("Bill ID: " + id + " | PatientID: " + patientId);
        System.out.println("Consultation Fee: " + consultationFee);
        System.out.println("Room Charge: " + roomCharge);
        System.out.println("Medicines:");
        if (medicines != null && !medicines.isEmpty()) {
            medicines.forEach((k,v) -> System.out.println("  " + k + " : " + v));
        } else {
            System.out.println("  None");
        }
        System.out.println("Total: " + total() + " | Paid: " + paid);
    }
}