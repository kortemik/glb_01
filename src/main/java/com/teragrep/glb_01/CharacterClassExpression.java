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

public final class CharacterClassExpression implements Regexable {

    public CharacterClassExpression() {
    }

    @Override
    public String asRegex(final ByteBuffer byteBuffer) {

        final int mark = byteBuffer.position();

        String rv = "";
        if (!byteBuffer.hasRemaining()) {
            throw new NoMatchException("not enought content for character class expression");
        }

        final byte openBracket = byteBuffer.get();

        if (openBracket != '[') {
            byteBuffer.position(mark);
            throw new NoMatchException("has no open bracket");
        }
        rv = rv.concat("[");

        // is bang
        if (!byteBuffer.hasRemaining()) {
            byteBuffer.position(mark);
            throw new NoMatchException("not enough content for bang or closing bracket");
        }

        final byte b = byteBuffer.get();

        if (b == '!') {
            rv = rv.concat("^");
        }
        else {
            // revert bang read
            byteBuffer.position(byteBuffer.position() - 1);
        }

        // content
        rv = rv.concat("\\Q");

        //int numberOfChars = 0;
        boolean escaping = false;
        while (byteBuffer.hasRemaining()) {
            final byte value = byteBuffer.get();

            if (value == '\\') {
                // escapes are skipped, because regex quotation takes literals
                escaping = true;
                continue;
            }

            if (value != ']' || escaping) {
                //numberOfChars++;
                rv = rv.concat(new String(new byte[] {
                        value
                }));
            }
            else {
                byteBuffer.position(byteBuffer.position() - 1); // revert close bracket read
                break;
            }

            escaping = false;
        }

        rv = rv.concat("\\E");

        final byte closeBracket = byteBuffer.get();
        if (closeBracket != ']') {
            byteBuffer.position(mark);
            throw new NoMatchException("no closing bracket");
        }

        rv = rv.concat("]");

        return rv;
    }

}
