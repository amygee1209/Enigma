package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Amy Kwon
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        int num = _permutation.alphabet().toInt(cposn);
        set(num);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int input = _permutation.permute(p + _setting);
        int edit = _setting % size();
        return wrap(input - edit);
    }

    /** Return the conversion of P with the ring.
     * @param p given alphabet.
     * @param ring given ring alphabet. */
    int convertForward(int p, int ring) {
        int input = _permutation.permute(p + _setting - ring);
        int edit = wrap(_setting - ring);
        return wrap(input - edit);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int input = _permutation.invert(e + _setting);
        int edit = _setting % size();
        return wrap(input - edit);
    }

    /** Return the conversion of P with the ring.
     * @param e given alphabet.
     * @param ring given ring alphabet. */
    int convertBackward(int e, int ring) {
        int input = _permutation.invert(e + _setting - ring);
        int edit = wrap(_setting - ring);
        return wrap(input - edit);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    /** Reset the notches.
     * @param ring The ring string. */
    void notchReset(int ring) {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Number of the setting for each rotor. */
    private int _setting;

}
