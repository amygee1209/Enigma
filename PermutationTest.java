package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Amy Kwon
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.invert('F');
    }

    @Test(expected = EnigmaException.class)
    public void testCaseSensitive() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.permute('b');
    }

    @Test(expected = EnigmaException.class)
    public void testDuplicateAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCCCD"));
    }


    @Test
    public void testPermute() {
        Permutation p1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('B', p1.permute('D'));
        String cycle = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p2 = new Permutation(cycle, new Alphabet());
        assertEquals('L', p2.permute('E'));
        assertEquals('S', p2.permute('S'));
        assertEquals('C', p2.permute('Y'));
        Permutation p3 = new Permutation("", new Alphabet("ABCD"));
        assertEquals('B', p3.permute('B'));

        assertEquals(1, p1.permute(3));
        assertEquals(11, p2.permute(4));
        assertEquals(18, p2.permute(18));
        assertEquals(2, p2.permute(24));
        assertEquals(1, p3.permute(1));
    }

    @Test
    public void testCurious() {
        Permutation p1 = new Permutation("(BAD)", new Alphabet("ABCD"));
        System.out.println(p1.permute('C'));
    }

    @Test
    public void testInvert() {
        Permutation p1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('C', p1.invert('D'));
        String cycle = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p2 = new Permutation(cycle, new Alphabet());
        assertEquals('A', p2.invert('E'));
        assertEquals('U', p2.invert('A'));
        assertEquals('S', p2.invert('S'));
        assertEquals('J', p2.invert('Z'));
        Permutation p3 = new Permutation("", new Alphabet("ABCD"));
        assertEquals('B', p3.invert('B'));

        assertEquals(2, p1.invert(3));
        assertEquals(0, p2.invert(4));
        assertEquals(20, p2.invert(0));
        assertEquals(18, p2.invert(18));
        assertEquals(9, p2.invert(25));
        assertEquals(1, p3.invert(1));
    }

    @Test
    public void testDerangement() {
        String cycle = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p1 = new Permutation(cycle, new Alphabet());
        assertFalse(p1.derangement());
        Permutation p2 = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertTrue(p2.derangement());
        Permutation p3 = new Permutation("", new Alphabet("ABCD"));
        assertFalse(p3.derangement());
    }

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

}
