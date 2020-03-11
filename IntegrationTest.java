package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/** Integration Test
 *  @author Amy Kwon
 */
public class IntegrationTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* Testing trivial. */
    @Test
    public void testTrivial() {
        String conf = "testing/correct/default.conf";
        String in = "testing/correct/trivial.in";
        String actual = "testing/correct/trivialActual.out";
        String[] input = {conf, in, actual};
        Main.main(input);
    }

    /* Testing trivial1. */
    @Test
    public void testTrivial1() {
        String conf = "testing/correct/default.conf";
        String in = "testing/correct/trivial1.in";
        String actual = "testing/correct/trivial1Actual.out";
        String[] input = {conf, in, actual};
        Main.main(input);
    }

    /* Testing navalCipher. */
    @Test
    public void testNavalCipher() {
        String conf = "testing/correct/default.conf";
        String in = "testing/correct/navalCipher.in";
        String actual = "testing/correct/navalCipherActual.out";
        String[] input = {conf, in, actual};
        Main.main(input);
    }

    /* Testing riptide. */
    @Test
    public void testRiptide() {
        String conf = "testing/correct/default.conf";
        String in = "testing/correct/riptide.in";
        String actual = "testing/correct/riptideActual.out";
        String[] input = {conf, in, actual};
        Main.main(input);
    }

}
