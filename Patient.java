public class Patient extends Person {
    private static final long serialVersionUID = 1L;
    private String gender;
    private String medicalHistory;
    private boolean admitted;
    private String roomNumber;

    public Patient(String id, String name, int age, String contact, String gender, String medicalHistory) {
        super(id, name, age, contact);
        this.gender = gender;
        this.medicalHistory = medicalHistory;
        this.admitted = false;
        this.roomNumber = "";
    }

    public String getGender() { return gender; }
    public String getMedicalHistory() { return medicalHistory; }
    public boolean isAdmitted() { return admitted; }
    public String getRoomNumber() { return roomNumber; }

    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public void admit(String roomNumber) { this.admitted = true; this.roomNumber = roomNumber; }
    public void discharge() { this.admitted = false; this.roomNumber = ""; }

    @Override
    public void display() {
        System.out.println("Patient ID: " + id + ", Name: " + name + ", Age: " + age + ", Contact: " + contact +
                ", Gender: " + gender + ", Admitted: " + admitted + (admitted ? ", Room: " + roomNumber : ""));
        System.out.println("Medical History: " + medicalHistory);
    }
}