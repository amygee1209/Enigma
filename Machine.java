package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Amy Kwon
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        boolean length = pawls >= numRotors;
        if (allRotors.isEmpty() || length) {
            throw error("All rotors is not the right size!");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = new ArrayList<Rotor>(allRotors);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return allRotors. */
    ArrayList<Rotor> allRotors() {
        return _allRotors;
    }

    /** Reset the notches of all moving rotors.
     * @param ring The ring string. */
    void setNotches(String ring) {
        for (int i = _numRotors - 1; i >= _numRotors - _numPawls; i -= 1) {
            char eachRing = ring.charAt(i - 1);
            int numRing = _alphabet.toInt(eachRing);
            Rotor each = _allRotors.get(i);
            each.notchReset(numRing);
        }
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _activeRotors = new ArrayList<Rotor>();
        for (String el : rotors) {
            for (Rotor ro : _allRotors) {
                if (ro.name().equals(el)) {
                    _activeRotors.add(ro);
                    break;
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (_numRotors - 1 != setting.length()) {
            throw error("Not right amount of settings!");
        }
        for (int i = 0; i < _numRotors - 1; i += 1) {
            _activeRotors.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        Rotor lastRotor = _activeRotors.get(_numRotors - 1);
        int range = _numRotors - _numPawls;
        for (int i = _activeRotors.size() - 1; i >= 0; i -= 1) {
            Rotor each = _activeRotors.get(i);
            if (each.atNotch() && i > range) {
                ArrayList<Rotor> turn = new ArrayList<Rotor>();
                turn.add(each);
                Rotor before = _activeRotors.get(i - 1);
                turn.add(before);
                i -= 1;
                while (i > range) {
                    Rotor evenBefore = _activeRotors.get(i - 1);
                    if (before.atNotch() && evenBefore.rotates()) {
                        turn.add(evenBefore);
                        before = evenBefore;
                        i -= 1;
                    } else {
                        break;
                    }
                }
                for (Rotor turningRotor : turn) {
                    turningRotor.advance();
                }
            } else if (each == lastRotor) {
                each.advance();
            }
        }
        return convertAll(c);
    }

    /** Return the modifited string with ring.
     * @param ring The ring string. */
    void setUpRing(String ring) {
        _ring = new ArrayList<Character>();
        for (int i = 0; i < ring.length(); i += 1) {
            _ring.add(ring.charAt(i));
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    int wrap(int p) {
        int r = p % _alphabet.size();
        if (r < 0) {
            r += _alphabet.size();
        }
        return r;
    }

    /** Convert through all the rotors.
     * @param update input integer.
     * @return converted integers. */
    int convertAll(int update) {
        if (_plugBoard != null) {
            update = _plugBoard.permute(update);
        }
        for (int i = _activeRotors.size() - 1; i >= 0; i -= 1) {
            Rotor each = _activeRotors.get(i);
            if (_ring != null && i != 0) {
                char eachChar = _ring.get(i - 1);
                int eachRing = _alphabet.toInt(eachChar);
                update = each.convertForward(update, eachRing);
            } else {
                update = each.convertForward(update);
            }
        }
        for (int i = 1; i < _activeRotors.size(); i += 1) {
            Rotor each = _activeRotors.get(i);
            if (_ring != null) {
                char eachChar = _ring.get(i - 1);
                int eachRing = _alphabet.toInt(eachChar);
                update = each.convertBackward(update, eachRing);
            } else {
                update = each.convertBackward(update);
            }
        }
        if (_plugBoard != null) {
            update = _plugBoard.permute(update);
        }
        return update;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        ArrayList<Character> modified = new ArrayList<Character>();
        for (int i = 0; i < msg.length(); i += 1) {
            char curr = msg.charAt(i);
            if (!(curr == ' ')) {
                int num = _alphabet.toInt(curr);
                int update = convert(num);
                char next = _alphabet.toChar(update);
                modified.add(next);
            }
        }
        return convertToString(modified);
    }

    /** Converts an ArrayList to a string with each block
     * having 5 characters.
     * @return converted string.
     * @param charList ArrayList of characters to be converted. */
    public String convertToString(ArrayList<Character> charList) {
        StringBuilder convert = new StringBuilder();
        int cnt = 0;
        for (Character el : charList) {
            if (cnt == 5) {
                convert.append(" ").append(el.toString());
                cnt = 0;
            } else {
                convert.append(el.toString());
            }
            cnt += 1;
        }
        return convert.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of all rotors. */
    private int _numRotors;

    /** Number of moving rotors (aka number of pawls. */
    private int _numPawls;

    /** All rotors in an ArrayList. */
    private ArrayList<Rotor> _allRotors;

    /** All rotors in an ArrayList. */
    private ArrayList<Rotor> _activeRotors;

    /** Plugboard permutation. */
    private Permutation _plugBoard;

    /** String of the ring for each rotor. */
    private ArrayList<Character> _ring;
}
