package magicfence.healthfiles;

public class Prescriptions {
    String dr_name;

    public Prescriptions() {
    }

    public Prescriptions(String dr_name) {
        this.dr_name = dr_name;

    }

    public String getDr_name() {
        return dr_name;
    }

    public void setDr_name(String dr_name) {
        this.dr_name = dr_name;
    }

}
