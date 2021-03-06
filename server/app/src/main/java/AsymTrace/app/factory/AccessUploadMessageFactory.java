/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package AsymTrace.app.factory;

import AsymTrace.app.model.AccessUploadMessage;
import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.interfaces.Box;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.goterl.lazycode.lazysodium.utils.KeyPair;


public class AccessUploadMessageFactory {

    LazySodium lazySodium;

    public AccessUploadMessageFactory(LazySodium lazySodium) {
        this.lazySodium = lazySodium;
    }


    /*
        public native int crypto_box_seal(byte[] cipher, byte[] message, long messageLen, byte[] publicKey);

    public native int crypto_box_seal_open(byte[] m,
                                    byte[] cipher,
                                    long cipherLen,
                                    byte[] publicKey,
                                    byte[] secretKey);



     */

    public AccessUploadMessage create(Key publicKey, Key symmetricKey) {

        byte[] data = symmetricKey.getAsBytes();

        byte[] cipherText = new byte[Box.SEALBYTES + data.length];

        boolean success = lazySodium.cryptoBoxSeal(
                cipherText,
                data,
                data.length,
                publicKey.getAsBytes()
        );

        if (success) {
            return new AccessUploadMessage(
                    cipherText
            );
        } else {
            return null;
        }
    }

    // TODO: Move this somewhere else
    public Key decrypt(AccessUploadMessage accessUploadMessage, KeyPair keyPair) {

        byte[] cipherText = accessUploadMessage.getCipher();
        byte[] plainText = new byte[cipherText.length - Box.SEALBYTES];

        boolean success = lazySodium.cryptoBoxSealOpen(
                plainText,
                cipherText,
                cipherText.length,
                keyPair.getPublicKey().getAsBytes(),
                keyPair.getSecretKey().getAsBytes()
        );


        if (success) {
            return Key.fromBytes(plainText);
        }

        return null;
    }
}
