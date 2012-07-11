package org.phw.core.lang;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * IniReader.
 * There are only a few simple rules: 
 * Leading and trailing spaces are trimmed from section names, property names and property values.
 * Section names are enclosed between [ and ].
 * Properties following a section header belong to that section
 * Properties defined before the appearance of any section headers are considered global properties 
 * and should be set and get with no section names. 
 * You can use either equal sign (=) or colon (:) to assign property values
 * Comments begin with either a semicolon (;), or a sharp sign (#) and extend to the end of line. 
 * It doesn't have to be the first character. 
 * A backslash (\) escapes the next character (e.g., \# is a literal #, \\ is a literal \).
 * If the last character of a line is backslash (\), the value is continued on the next line 
 * with new line character included. 
 *
 */
public class Ini {
    private Properties globalProperties;
    private Map<String, Properties> properties;

    /**
     * Internal Parse State.
     *
     */
    private enum ParseState {
        NORMAL,
        ESCAPE,
        ESC_CRNL,
        COMMENT
    }

    /**
     * Constructor.
     * @param in InputStream
     */
    public Ini(InputStream in) {
        globalProperties = new Properties();
        properties = new HashMap<String, Properties>();
        try {
            load(new BufferedInputStream(in));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            Ios.closeQuietly(in);
        }
    }

    /**
     * Ini Parser.
     * @author BingooHuang
     *
     */
    private static class IniParser {
        private int bufSize = 4096;
        private byte[] buffer = new byte[bufSize];

        private ParseState state = ParseState.NORMAL;
        private boolean sectionOpen = false;
        private String currentSection = null;
        private String key = null, value = null;
        private StringBuilder sb = new StringBuilder();
        private int n;
        private boolean endOfFile = false;
        private Ini ini;

        IniParser(Ini ini) {
            this.ini = ini;
        }

        void parse(InputStream in) throws IOException {
            while (!endOfFile) {
                n = in.read(buffer, 0, bufSize);
                if (n < 0) {
                    endOfFile = true;
                    n = 1;
                    buffer[0] = '\n';
                }

                for (int i = 0; i < n; i++) {
                    char c = (char) buffer[i];

                    if (state == ParseState.COMMENT) { // comment, skip to end of line
                        if (c == '\r' || c == '\n') {
                            state = ParseState.NORMAL;
                        }
                        else {
                            continue;
                        }
                    }

                    if (state == ParseState.ESCAPE) {
                        sb.append(c);
                        // if the EOL is \r\n, \ escapes both chars
                        state = c == '\r' ? ParseState.ESC_CRNL : ParseState.NORMAL;
                        continue;
                    }

                    switchC(c);
                }

            }
        }

        private void switchC(char c) {
            switch (c) {
            case '[': // start section
                sb = new StringBuilder();
                sectionOpen = true;
                break;

            case ']': // end section
                if (sectionOpen) {
                    currentSection = sb.toString().trim();
                    sb = new StringBuilder();
                    ini.properties.put(currentSection, new Properties());
                    sectionOpen = false;
                }
                else {
                    sb.append(c);
                }
                break;

            case '\\': // escape char, take the next char as is
                state = ParseState.ESCAPE;
                break;

            case '#':
            case ';':
                state = ParseState.COMMENT;
                break;

            case '=': // assignment operator
            case ':':
                if (key == null) {
                    key = sb.toString().trim();
                    sb = new StringBuilder();
                }
                else {
                    sb.append(c);
                }
                break;

            case '\r':
            case '\n':
                processLinebreak(c);
                break;

            default:
                sb.append(c);
            }
        }

        private void processLinebreak(char c) {
            if (state == ParseState.ESC_CRNL && c == '\n') {
                sb.append(c);
                state = ParseState.NORMAL;
            }
            else {
                if (sb.length() > 0) {
                    value = sb.toString().trim();
                    sb = new StringBuilder();

                    if (key != null) {
                        if (currentSection == null) {
                            ini.setProperty(key, value);
                        }
                        else {
                            ini.setProperty(currentSection, key, value);
                        }
                    }
                }
                key = null;
                value = null;
            }
        }
    }

    /**
     * Load ini as properties from input stream.
     */
    private void load(InputStream in) throws IOException {
        new IniParser(this).parse(in);
    }

    /**
     * Get global property by name.
     * @param name Property Name
     * @return String
     */
    public String getProperty(String name) {
        return globalProperties.getProperty(name);
    }

    /**
     * Set global property.
     * @param name Property Name
     * @param value Property Value
     */
    public void setProperty(String name, String value) {
        globalProperties.setProperty(name, value);
    }

    /**
     * Return iterator of global properties.
     * @return Enumeration<String>
     */
    public Set<String> properties() {
        return globalProperties.stringPropertyNames();
    }

    /**
     * Get property value for specified section and name. Returns null
     * if section or property does not exist.
     * @param section Section
     * @param name name
     * @return Property
     */
    public String getProperty(String section, String name) {
        Properties p = properties.get(section);
        return p == null ? null : p.getProperty(name);
    }

    /**
     * Set property value for specified section and name. Creates section
     * if not existing.
     * @param section Section
     * @param name Name
     * @param value Value
     */
    public void setProperty(String section, String name, String value) {
        Properties p = properties.get(section);
        if (p == null) {
            p = new Properties();
            properties.put(section, p);
        }
        p.setProperty(name, value);
    }

    /**
     * Return property iterator for specified section. Returns null if
     * specified section does not exist.
     * @param section Section
     * @return Properties
     */
    public Properties properties(String section) {
        return properties.get(section);
    }

    /**
     * Return iterator of names of section.
     * @return Set<String>
     */
    public Set<String> sections() {
        return properties.keySet();
    }

    /**
     * Dumps properties to output stream.
     * @param out PrintStream
     */
    public void dump(PrintStream out) {
        // Global properties
        for (String name : properties()) {
            out.printf("%s = %s\n", name, dumpEscape(getProperty(name)));
        }

        // sections
        for (String section : sections()) {
            out.printf("\n[%s]\n", section);
            Properties props = properties(section);
            for (String name : props.stringPropertyNames()) {
                out.printf("%s = %s\n", name, dumpEscape(getProperty(section, name)));
            }
        }
    }

    private static String dumpEscape(String s) {
        return s.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll(";", "\\\\;")
                .replaceAll("#", "\\\\#")
                .replaceAll("(\r?\n|\r)", "\\\\$1");
    }

}