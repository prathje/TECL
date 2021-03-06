package AsymTrace.app.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteArrayListUtil {


    public static byte[] merge(List<byte[]> arrays) {

        int overAllLength = 0;
        for (byte[] arr : arrays) {
            overAllLength += 4 + arr.length;
        }

        byte[] out = new byte[overAllLength];

        ByteBuffer buf = ByteBuffer.wrap(out);

        for (byte[] arr : arrays) {
            buf.putInt(arr.length);
            buf.put(arr);
        }

        return out;
    }

    public static byte[] merge(byte[]... arrays) {
        return merge(Arrays.asList(arrays));
    }

    public static List<byte[]> split(byte[] bytes) {

        List<byte[]> out = new ArrayList<>();
        ByteBuffer buf = ByteBuffer.wrap(bytes);

        while (buf.hasRemaining()) {
            int size = buf.getInt();
            byte[] arr = new byte[size];
            buf.get(arr);
            out.add(arr);
        }

        return out;
    }
}
