package AsymTrace.app.model;

/**
 * This message is sent to the health authorities by the uploader
 * Once the HA get the used private Key from the contact, they can decrypt this message which contains the shared symmetric key for the other data
 * (there will be one upload per contact in a group)
 */
public class AccessUploadMessage implements MessageInterface<AccessUploadMessage> {

    byte[] cipher;

    public AccessUploadMessage(byte[] cipher) {
        this.cipher = cipher;
    }

    public byte[] getCipher() {
        return cipher;
    }

    public AccessUploadMessage fromBytes(byte[] bytes) {
        return new AccessUploadMessage(bytes);
    }

    // TODO: Shall we also pad this data?
    public byte[] toBytes() {
        return cipher;
    }
}
