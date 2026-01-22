/*
 * Teragrep GlobToRegex Library (glb_01)
 * Copyright (C) 2026 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.glb_01;

import java.nio.ByteBuffer;

public class BraceExpression implements Regexable {

    private final ByteBuffer byteBuffer;
    private final Element element;

    public BraceExpression(final ByteBuffer byteBuffer, final Element element) {
        this.byteBuffer = byteBuffer;
        this.element = element;
    }

    @Override
    public String asRegex() {
        final int mark = byteBuffer.position();

        String rv = "";
        try {
            if (!byteBuffer.hasRemaining()) {
                throw new IllegalStateException();
            }
            final byte braceOpen = byteBuffer.get();

            if (braceOpen != '{') {
                throw new IllegalArgumentException();
            }
            else {
                rv = rv.concat("(");
            }

            while (byteBuffer.hasRemaining()) {
                try {
                    rv = rv.concat(element.asRegex());
                    if (byteBuffer.hasRemaining()) { // this could be an optional braceContinuation object?
                        final byte comma = byteBuffer.get();
                        if (comma != ',') {
                            //System.out.println("not comma");
                            byteBuffer.position(byteBuffer.position() - 1);
                            throw new IllegalArgumentException();
                        }
                        else {
                            rv = rv.concat("|");
                        }
                        if (!byteBuffer.hasRemaining()) {
                            //System.out.println("no content after comma");
                            throw new IllegalArgumentException();
                        }
                        else {
                            rv = rv.concat(element.asRegex());
                        }
                    }
                }
                catch (IllegalArgumentException e) {
                    //System.out.println("Brace catching");
                    if (!byteBuffer.hasRemaining()) {
                        throw new IllegalArgumentException(); // missing close brace
                    }
                    final byte braceClose = byteBuffer.get();

                    if (braceClose != '}') {
                        byteBuffer.position(byteBuffer.position() - 1);
                        throw new IllegalArgumentException();
                    }
                    else {
                        rv = rv.concat(")");
                    }
                    break;
                }
            }
        }
        catch (IllegalArgumentException e) {
            //System.out.println("Brace catching top, not a braceExpression");
            byteBuffer.position(mark);
            throw e;
        }

        // TODO make empty throw IllegalStateException

        return rv;
    }

}
