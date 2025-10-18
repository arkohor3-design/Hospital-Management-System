import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String patientId;
    private String doctorId;
    private LocalDateTime dateTime;
    private String remarks;

    public Appointment(String id, String patientId, String doctorId, LocalDateTime dateTime, String remarks) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.remarks = remarks;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getRemarks() { return remarks; }

    public String displayTime() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void display() {
        System.out.println("Appointment ID: " + id + ", PatientID: " + patientId + ", DoctorID: " + doctorId + ", At: " + displayTime() + ", Remarks: " + remarks);
    }
}