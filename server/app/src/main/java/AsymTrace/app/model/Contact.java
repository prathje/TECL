package AsymTrace.app.model;

public class Contact {

    byte[] publicKeyBytes;
    byte[] nonce;
    byte[] ciphertext;
    byte[] visibleData;

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    public byte[] getVisibleData() {
        return visibleData;
    }
}
