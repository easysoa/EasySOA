/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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