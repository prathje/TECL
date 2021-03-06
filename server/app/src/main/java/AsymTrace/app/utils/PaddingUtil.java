package AsymTrace.app.utils;

import java.nio.ByteBuffer;

public class PaddingUtil {

    public static byte[] pad(int factor, byte[] unpadded) {

        // we check the paddedLength
        int paddedLength = unpadded.length+4; // 4 bytes for size

        if (paddedLength % factor > 0) { // otherwise: this matches perfectly
            paddedLength += factor - (paddedLength % factor);
        }

        // Java initializes this to zeros for us
        byte[] padded = new byte[paddedLength];

        ByteBuffer buf = ByteBuffer.wrap(padded);
        buf.putInt(unpadded.length);
        buf.put(unpadded);
        return padded;
    }

    public static byte[] unpad(byte[] padded) {

        ByteBuffer buf = ByteBuffer.wrap(padded);
        int realSize = buf.getInt();
        byte[] unpadded = new byte[realSize];
        buf.get(unpadded);
        return unpadded;
    }
}
