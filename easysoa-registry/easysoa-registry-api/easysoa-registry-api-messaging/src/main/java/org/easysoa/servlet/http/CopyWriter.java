/**
 * 
 */
package org.easysoa.servlet.http;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * 
 * @author jguillemotte
 *
 */
class CopyWriter extends PrintWriter {

    StringBuilder copy = new StringBuilder();

    public CopyWriter(Writer out) {
        super(out);
    }

    public void write(int c) {
        copy.append((char) c); // It is actually a char, not an int.
        super.write(c);
        super.flush();
    }

    public void write(char[] chars) {
        copy.append(chars);
        super.write(chars);
        super.flush();
    }

    public void write(char[] chars, int offset, int length) {
        copy.append(chars, offset, length);
        super.write(chars, offset, length);
        super.flush();
    }

    public void write(String string) {
        copy.append(string);
        super.write(string);
        super.flush();
    }

    public void write(String string, int offset, int length) {
        copy.append(string, offset, length);
        super.write(string, offset, length);
        super.flush();
    }

    public String getCopy() {
        return copy.toString();
    }
}