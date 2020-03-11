package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Amy Kwon
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }
        _config = getInput(args[0]);
        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        if (!_input.hasNext()) {
            throw error("No input file!");
        }
        _allRotorsName = new ArrayList<String>();
        _machine = readConfig();
        if (_input.next().equals("*")) {
            String set = _input.nextLine();
            setActiveRotors(set);
        } else {
            throw error("Input does not start with a setting");
        }
        while (_input.hasNextLine()) {
            String start = _input.nextLine();
            Scanner startScan = new Scanner(start);
            if (start.equals("")) {
                _output.print("\n");
            } else if (startScan.next().equals("*")) {
                String set = startScan.nextLine();
                setActiveRotors(set);
            } else {
                printMessageLine(start);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (!_config.hasNext()) {
                throw error("The configuration file is empty.");
            }
            _alphabet = new Alphabet(_config.nextLine());
            String strRotors = _config.next();
            String strPawls = _config.next();
            boolean checkRotor = strRotors.matches(".*\\d.*");
            boolean checkPawls = strPawls.matches(".*\\d.*");
            if (!(checkRotor && checkPawls)) {
                throw error("Second line must be numbers.");
            }
            int numRotors = Integer.parseInt(strRotors);
            int numPawls = Integer.parseInt(strPawls);
            if (!(numRotors > numPawls && numPawls >= 0)) {
                throw error("S>P>=0, wrong number format.");
            }
            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                _combStr = _config.next() + _config.nextLine();
                while (_config.hasNext("\\(.*\\)")) {
                    String next = _config.next() + _config.nextLine();
                    _combStr = _combStr.concat(next);
                }
                Rotor newRotor = readRotor();
                allRotors.add(newRotor);
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Scanner combStrScan = new Scanner(_combStr);
            String name = combStrScan.next();
            if (name.contains("(") || name.contains(")")) {
                throw error("Rotor's name shouldn't contain parentheses.");
            }
            if (_allRotorsName != null) {
                if (_allRotorsName.contains(name)) {
                    throw error("Rotors cannot repeat in config");
                }
            }
            _allRotorsName.add(name);
            String comb = combStrScan.next();
            char type = comb.charAt(0);
            String notches = comb.substring(1);
            String permStr = combStrScan.nextLine().strip();
            Permutation perm = new Permutation(permStr, _alphabet);
            Rotor newRotor;
            if (type == 'M') {
                if (notches.equals("")) {
                    throw error("No space between type & notches or no notch");
                }
                newRotor = new MovingRotor(name, perm, notches);
            } else if (type == 'N') {
                newRotor = new FixedRotor(name, perm);
            } else if (type == 'R') {
                newRotor = new Reflector(name, perm);
                if (!newRotor.permutation().derangement()) {
                    throw error("reflectors must implement derangements");
                }
            } else {
                throw error("Not an option for a rotor");
            }
            return newRotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String convert = _machine.convert(msg);
        _output.print(convert + "\n");
    }

    /** Set active rotors.
     *  @param settingInput String of the entire setting line. */
    private void setActiveRotors(String settingInput) {
        Scanner setScan = new Scanner(settingInput);
        int cnt = 0;
        String rest = "";
        ArrayList<String> activeRotor = new ArrayList<String>();
        while (setScan.hasNext()) {
            String each = setScan.next();
            if (_allRotorsName.contains(each)) {
                if (activeRotor.contains(each)) {
                    throw error("Rotor may not repeat");
                }
                activeRotor.add(each);
                cnt += 1;
            } else {
                rest = each;
                if (setScan.hasNext()) {
                    rest = each + setScan.nextLine();
                }
                break;
            }
        }
        if (cnt != _machine.numRotors()) {
            throw error("Not a correct amount of setting rotors.");
        }
        _activeRotors = activeRotor.toArray(new String[0]);
        checkOrder();
        checkMovingNum();
        _machine.insertRotors(_activeRotors);

        Scanner restScan = new Scanner(rest);
        if (!restScan.hasNext()) {
            throw error("No initial setting");
        }
        String setting = restScan.next();

        if (restScan.hasNext() && !restScan.hasNext("\\(.*\\)")) {
            String ring = restScan.next();
            _machine.setUpRing(ring);
        }
        setUp(_machine, setting);

        if (restScan.hasNext("\\(.*\\)")) {
            String plug = restScan.nextLine().strip();
            setPlugBoard(plug);
        }
    }

    /** Set up the plugboard.
     * @param plug String of the plugboard. */
    private void setPlugBoard(String plug) {
        if (plug.charAt(0) != '(') {
            throw error("Not a right setting for plugboard");
        }
        Permutation plugBorad = new Permutation(plug, _alphabet);
        _machine.setPlugboard(plugBorad);
    }

    /** Return the rotor of its given name.
     * @param name Name of the rotor. */
    private Rotor find(String name) {
        Rotor found = null;
        for (Rotor each : _machine.allRotors()) {
            if (each.name().equals(name)) {
                found = each;
            }
        }
        return found;
    }

    /** Check if the setting rotors are in the right order. */
    private void checkOrder() {
        int all = _machine.numRotors();
        int moving = _machine.numPawls();
        int rangeFix = all - moving;
        for (int i = 0; i < all; i += 1) {
            String name = _activeRotors[i];
            if (i == 0) {
                if (!find(name).reflecting()) {
                    throw error("First rotor is not a reflector!");
                }
            } else if (i < rangeFix) {
                if (find(name).reflecting()) {
                    throw error("Reflector cannot be in fixed rotor position!");
                }
                if (find(name).rotates()) {
                    throw error("Moving rotor cannot not be in this position!");
                }
            } else {
                if (!find(name).rotates()) {
                    throw error("This is not a moving rotor!");
                }
            }
        }
    }

    /** Check the number of moving rotors. */
    private void checkMovingNum() {
        int cnt = 0;
        for (String eachName : _activeRotors) {
            if (find(eachName).rotates()) {
                cnt += 1;
            }
        }
        if (cnt != _machine.numPawls()) {
            throw error("Not a correct number of moving rotor!");
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Given machine. */
    private Machine _machine;

    /** Combined information of the rotor. */
    private String _combStr;

    /** All active rotors. */
    private String[] _activeRotors;

    /** Names of all the rotors. */
    private ArrayList<String> _allRotorsName;

}
