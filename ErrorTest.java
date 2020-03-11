package enigma;

import org.junit.Test;

/** Error Test
 *  @author Amy Kwon
 */
public class ErrorTest {

    /* Testing configuration. */
    @Test
    public void testConf() {
        String conf = "testing/error/default.conf";
        String in = "testing/error/trivialerr.in";
        String[] input = {conf, in};
        Main.main(input);
    }

}
