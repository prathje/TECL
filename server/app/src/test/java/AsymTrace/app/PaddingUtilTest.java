/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package AsymTrace.app;

import AsymTrace.app.utils.ByteArrayListUtil;
import AsymTrace.app.utils.PaddingUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaddingUtilTest {
    @Test
    public void testRandom() {

        Random r = new Random(42);

        // num times
        for (int i = 0; i < 6; i++) {

            int inputSize = r.nextInt(10000);
            int factor = r.nextInt(4096)+1;

            byte[] unpaddedIn = new byte[inputSize];
            r.nextBytes(unpaddedIn);

            byte[] padded = PaddingUtil.pad(factor, unpaddedIn);

            assertEquals(0, padded.length % factor);

            byte[] unpaddedOut = PaddingUtil.unpad(padded);

            assertArrayEquals(unpaddedIn, unpaddedOut);
        }
    }
}
