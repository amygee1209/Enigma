package enigma;

import java.util.ArrayList;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Amy Kwon
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _cntChar = 0;
        _alphabet = alphabet;
        _strCycle = cycles;
        if (!cycles.equals("")) {
            if (_strCycle.charAt(0) != '(') {
                throw error("Must start with an open parenthesis");
            }
            _arrCycle = new ArrayList<ArrayList<Character>>();
            int index = -1;
            boolean closed = true;
            for (int i = 0; i < cycles.length(); i += 1) {
                char curr = cycles.charAt(i);
                if (_alphabet.contains(curr)) {
                    _arrCycle.get(index).add(curr);
                    _cntChar += 1;
                } else if (curr == '(') {
                    if (!closed) {
                        throw error("Previous parenthesis was not closed");
                    }
                    closed = false;
                    index += 1;
                    _arrCycle.add(new ArrayList<Character>());
                } else if (curr == ')') {
                    if (closed) {
                        throw error("Missing an open parenthesis.");

                    }
                    closed = true;
                } else if (curr == ' ') {
                    if (!closed) {
                        throw error("White space is not allowed.");
                    }
                } else {
                    throw error("Given alphabet in cycle does not exist");
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    public void addCycle(String cycle) {
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int realNum = wrap(p);
        char currChar = _alphabet.toChar(realNum);
        char newChar = permute(currChar);
        return _alphabet.toInt(newChar);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int realNum = wrap(c);
        char currChar = _alphabet.toChar(realNum);
        char newChar = invert(currChar);
        return _alphabet.toInt(newChar);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw error("char not in alphabet, so it cannot be converted!");
        }
        if (_strCycle.equals("")) {
            return p;
        }
        char end = p;
        for (ArrayList<Character> characters : _arrCycle) {
            for (int j = 0; j < characters.size(); j += 1) {
                if (characters.get(j) == p) {
                    if (j == characters.size() - 1) {
                        end = characters.get(0);
                    } else {
                        end = characters.get(j + 1);
                    }
                }
            }
        }
        return end;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw error("char not in alphabet, so it cannot be inverted!");
        }
        if (_strCycle.equals("")) {
            return c;
        }
        char end = c;
        for (ArrayList<Character> characters : _arrCycle) {
            for (int j = 0; j < characters.size(); j += 1) {
                if (characters.get(j) == c) {
                    if (j == 0) {
                        end = characters.get(characters.size() - 1);
                    } else {
                        end = characters.get(j - 1);
                    }
                }
            }
        }
        return end;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (_strCycle.equals("")) {
            return false;
        }
        for (ArrayList<Character> characters : _arrCycle) {
            if (characters.size() <= 1) {
                return false;
            }
        }
        return _cntChar == _alphabet.size();
    }


    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String of the cycle. */
    private String _strCycle;

    /** Nested ArrayList of characters in each cycle. */
    private ArrayList<ArrayList<Character>> _arrCycle;

    /** Count of the characters in the cycle. */
    private int _cntChar;

}
