package AsymTrace.lib;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Data {

    List<Entry> entries = new ArrayList<>();

    static class Entry {

        public Entry(int type, String value) {
            this.type = type;
            this.value = value;
            this.length = value.getBytes(StandardCharsets.UTF_8).length;
        }

        private Entry() {}
        int type;
        int length;
        String value;
        boolean isPublic; // TODO: we could send public data too, so the recipient's device could display some information (e.g. for less replay attacks)
    }


    public static Data fromBytes(byte[] bytes) {
        Data d = new Data();

        int i = 0;

        while(i < bytes.length) {
            // parse type
            Entry e = new Entry();
            e.type = Byte.toUnsignedInt(bytes[i]);

            // parse length
            e.length = (bytes[i+1] & 0xFF) << 8 | (bytes[i+2] & 0xFF);
            i += 3;

            if (e.length < 0) {
                return null;
            }

            // parse value
            byte[] val = new byte[e.length];
            for (int k = i; k < i + e.length; k++) {
                if (k >= bytes.length) {
                    return null;
                }
                val[k-i] = bytes[k];
            }


            e.value = new String(val, StandardCharsets.UTF_8);

            // add to array
            d.entries.add(e);
            i += e.length;
        }
        return d;
    }
}