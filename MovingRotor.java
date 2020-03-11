package enigma;

import java.util.ArrayList;
import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Amy Kwon
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notchesStr = notches;
        if (_notchesStr.equals("")) {
            this.set(0);
        } else {
            _notchesInt = new ArrayList<Integer>();
            for (int i = 0; i < _notchesStr.length(); i += 1) {
                int curr = perm.alphabet().toInt(_notchesStr.charAt(i));
                _notchesInt.add(curr);
            }
        }
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        if (_notchesStr.equals("")) {
            return false;
        }
        for (int el : _notchesInt) {
            if (this.setting() == el) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        this.set(wrap(this.setting() + 1));
    }

    @Override
    void notchReset(int ring) {
        for (int i = 0; i < _notchesInt.size(); i += 1) {
            int curr = this._notchesInt.get(i);
            this._notchesInt.set(i, wrap(curr - ring));
        }
    }

    /** The permutation implemented by this rotor in its 0 position. */
    private String _notchesStr;
    /** Integer notches in an ArrayList. */
    private ArrayList<Integer> _notchesInt;

}
