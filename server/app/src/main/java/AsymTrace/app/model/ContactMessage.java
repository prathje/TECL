package AsymTrace.app.model;

import AsymTrace.app.utils.ByteArrayListUtil;
import AsymTrace.app.utils.PaddingUtil;

import java.util.List;

/**
 * This message is sent from one local device to another using e.g. NFC or QR codes
 */
public class ContactMessage implements MessageInterface<ContactMessage> {

    public static final int PADDING = 64;

    byte[] contactPublicKeyBytes;
    byte[] haNonce;     // in case of new keys every time (or fully static QR codes), the nonce would probably not be needed, but we include it for now
    byte[] haCiphertext;
    byte[] additionalData; // this is padded with VISIBLE_DATA_PADDING, but NOT encrypted

    public ContactMessage(byte[] contactPublicKeyBytes, byte[] haNonce, byte[] haCiphertext, byte[] displayData) {
        this.contactPublicKeyBytes = contactPublicKeyBytes;
        this.haNonce = haNonce;
        this.haCiphertext = haCiphertext;
        this.additionalData = displayData;
    }

    public byte[] getContactPublicKeyBytes() {
        return contactPublicKeyBytes;
    }

    public byte[] getHaNonce() {
        return haNonce;
    }

    public byte[] getHaCiphertext() {
        return haCiphertext;
    }

    public byte[] getAdditionalData() {
        return additionalData;
    }

    public static ContactMessage fromBytes(byte[] bytes) {
        byte[] unpadded = PaddingUtil.unpad(bytes);
        List<byte[]> byteArrayList = ByteArrayListUtil.split(unpadded);
        if (byteArrayList.size() == 4) {
            return new ContactMessage(
                    byteArrayList.get(0),
                    byteArrayList.get(1),
                    byteArrayList.get(2),
                    byteArrayList.get(3)
            );
        } else {
            return null;
        }
    }

    // We pad the data to give less insights on the custom data
    public byte[] toBytes() {
        return PaddingUtil.pad(
                PADDING,
                ByteArrayListUtil.merge(
                        contactPublicKeyBytes,
                        haNonce,
                        haCiphertext,
                        additionalData
                )
        );
    }

    public ContactMessage createWithEmptyDisplayData() {
        return new ContactMessage(contactPublicKeyBytes, haNonce, haCiphertext, new byte[0]);
    }
}
