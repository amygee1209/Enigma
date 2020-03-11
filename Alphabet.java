package enigma;

import static enigma.EnigmaException.*;
/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Amy Kwon
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alpha = chars;
        if (nonAlphabet()) {
            throw error("Not an alphabet.");
        }
        if (checkDuplicate()) {
            throw error("There are repeating alphabets.");
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alpha.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < this.size(); i += 1) {
            if (this.toChar(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw error("index not in bound");
        }
        return _alpha.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!_alpha.contains(ch + "")) {
            throw error("char not in alphabet!");
        }
        return _alpha.indexOf(ch);
    }

    /** Alphabet contains an non-alphabet figure.
     * @return true or false */
    boolean nonAlphabet() {
        boolean a = _alpha.contains(" ");
        boolean b = _alpha.contains("(");
        boolean c = _alpha.contains(")");
        boolean d = _alpha.contains("*");
        return a || b || c || d;
    }

    /** Alphabet contains a repeating letter.
     * @return true or false */
    boolean checkDuplicate() {
        for (int i = 0; i < size(); i += 1) {
            for (int j = 0; j < size(); j += 1) {
                if (i != j) {
                    if (_alpha.charAt(i) == _alpha.charAt(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Common alphabet. */
    private String _alpha;

}
