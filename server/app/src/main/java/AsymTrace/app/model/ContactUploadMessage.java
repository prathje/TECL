package AsymTrace.app.model;

import AsymTrace.app.utils.ByteArrayListUtil;

import java.util.List;

/**
 * This data is uploaded to the HA
 * Once the shared secret is known, the HA can decrypt every shared cipher entry
 * (there will be one shared cipher entry per contact in a group)
 */
public class ContactUploadMessage implements MessageInterface<ContactUploadMessage> {

    byte[] nonce;
    byte[] cipher;

    public ContactUploadMessage(byte[] nonce, byte[] cipher) {
        this.nonce = nonce;
        this.cipher = cipher;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public byte[] getCipher() {

        return cipher;
    }

    public ContactUploadMessage fromBytes(byte[] bytes) {
        List<byte[]> byteArrayList = ByteArrayListUtil.split(bytes);
        if (byteArrayList.size() == 2) {
            return new ContactUploadMessage(
                    byteArrayList.get(0),
                    byteArrayList.get(1)
            );
        } else {
            return null;
        }
    }

    public byte[] toBytes() {
        return ByteArrayListUtil.merge(
                nonce,
                cipher
        );
    }
}
