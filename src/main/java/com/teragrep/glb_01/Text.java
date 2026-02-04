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
import java.nio.charset.StandardCharsets;

public final class Text implements Regexable {

    public Text() {

    }

    @Override
    public String asRegex(final ByteBuffer byteBuffer) {
        final ByteBuffer slice = byteBuffer.slice();

        while (slice.hasRemaining()) {
            final byte b = slice.get();

            if (b == '\\' || b == '*' || b == '?' || b == '{' || b == '}' || b == '[' || b == ']' || b == ',') {
                slice.limit(slice.position() - 1);
                break;
            }
        }

        if (slice.position() == 0) {
            // nothing read
            throw new NoMatchException("no content for text expression");
        }

        // advance the original buffer by the amount text consumed
        byteBuffer.position(byteBuffer.position() + slice.limit());

        slice.flip();
        final byte[] bytes = new byte[slice.limit()];
        slice.get(bytes);
        String rv = "\\Q";
        rv = rv.concat(new String(bytes, StandardCharsets.UTF_8));
        rv = rv.concat("\\E");

        return rv;
    }

}
