/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package AsymTrace.app.factory;

import AsymTrace.app.model.ContactMessage;
import AsymTrace.app.model.ContactUploadMessage;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.interfaces.Box;
import com.goterl.lazycode.lazysodium.interfaces.SecretBox;
import com.goterl.lazycode.lazysodium.utils.Key;


public class ContactUploadMessageFactory {

    LazySodium lazySodium;

    public ContactUploadMessageFactory(LazySodium lazySodium) {
        this.lazySodium = lazySodium;
    }

    public ContactUploadMessage fromContactMessage(ContactMessage contactMessage, Key symmetricKey) {

        byte[] data = contactMessage.toBytes();

        byte[] nonce = lazySodium.nonce(Box.NONCEBYTES);
        byte[] cipherText = new byte[SecretBox.MACBYTES + data.length];

        boolean success = lazySodium.cryptoSecretBoxEasy(cipherText, data, data.length, nonce, symmetricKey.getAsBytes());

        if (success) {
            return new ContactUploadMessage(nonce, cipherText);
        } else {
            return null;
        }
    }


}
