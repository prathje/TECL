package AsymTrace.app.model;

public class ContactInfo {

    byte[] contactData;
    byte[] additionalData;


    public ContactInfo(byte[] contactData, byte[] additionalData) {
        this.contactData = contactData;
        this.additionalData = additionalData;
    }


    public byte[] getContactData() {
        return contactData;
    }

    public byte[] getAdditionalData() {
        return additionalData;
    }
}
