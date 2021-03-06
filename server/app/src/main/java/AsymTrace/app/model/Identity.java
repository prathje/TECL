package AsymTrace.app.model;

import com.goterl.lazycode.lazysodium.utils.KeyPair;

public class Identity {

    byte[] contactData;
    byte[] displayData;
    KeyPair keyPair;


    public Identity(KeyPair keyPair, byte[] contactData, byte[] displayData) {
        this.keyPair = keyPair;
        this.contactData = contactData;
        this.displayData = displayData;
    }

    public byte[] getContactData() {
        return contactData;
    }

    public byte[] getDisplayData() {
        return displayData;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
