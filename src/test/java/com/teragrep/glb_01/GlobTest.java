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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class GlobTest {

    @Test
    public void testGlobText() {
        Glob glob = new GlobImpl("testText");

        Assertions.assertEquals("^\\QtestText\\E$", glob.asRegex());
    }

    @Test
    public void testGlobComma() {
        Glob glob = new GlobImpl("some,Other");
        Assertions.assertEquals("^\\Qsome\\E,\\QOther\\E$", glob.asRegex());
    }

    @Test
    public void testGlobCommaTrailing() {
        Glob glob = new GlobImpl("some,Other,");
        Assertions.assertEquals("^\\Qsome\\E,\\QOther\\E,$", glob.asRegex());
    }

    @Test
    public void testGlobQuestionmark() {
        Glob glob = new GlobImpl("test?Questionmark");
        Assertions.assertEquals("^\\Qtest\\E[^/]\\QQuestionmark\\E$", glob.asRegex());
    }

    @Test
    public void testGlobQuestionmarkTrailing() {
        Glob glob = new GlobImpl("testQuestionmark?");
        Assertions.assertEquals("^\\QtestQuestionmark\\E[^/]$", glob.asRegex());
    }

    @Test
    public void testGlobWildcard() {
        Glob glob = new GlobImpl("test*Wildcard");
        Assertions.assertEquals("^\\Qtest\\E[^/]*\\QWildcard\\E$", glob.asRegex());
    }

    @Test
    public void testGlobWildcardTrailing() {
        Glob glob = new GlobImpl("testWildcard*");
        Assertions.assertEquals("^\\QtestWildcard\\E[^/]*$", glob.asRegex());
    }

    @Test
    public void testGlobBraceSimpleExpression() {
        Glob glob = new GlobImpl("testBrace{Simple}Expression");
        Assertions.assertEquals("^\\QtestBrace\\E(\\QSimple\\E)\\QExpression\\E$", glob.asRegex());
    }

    @Test
    public void testGlobBraceExpression() {
        Glob glob = new GlobImpl("test{More,Some}BraceExpression");
        Assertions.assertEquals("^\\Qtest\\E(\\QMore\\E|\\QSome\\E)\\QBraceExpression\\E$", glob.asRegex());
    }

    @Test
    public void testGlobBraceNestedExpression() {
        Glob glob = new GlobImpl("testBrace{Bar,{Foo,Biz}}BraceExpression");
        Assertions
                .assertEquals("^\\QtestBrace\\E(\\QBar\\E|(\\QFoo\\E|\\QBiz\\E))\\QBraceExpression\\E$", glob.asRegex());
    }

    @Test
    public void testGlobBraceMultipleContinuationExpressions() {
        Glob glob = new GlobImpl("x{a,b,c}y");
        Assertions.assertEquals("^\\Qx\\E(\\Qa\\E|\\Qb\\E|\\Qc\\E)\\Qy\\E$", glob.asRegex());
    }

    @Test
    public void testGlobEscapeExpression() {
        Glob glob = new GlobImpl("test\\{Escape\\}Expressio\\n");
        Assertions.assertEquals("^\\Qtest\\E\\{\\QEscape\\E\\}\\QExpressio\\E\\n$", glob.asRegex());
    }

    @Test
    public void testGlobCharacterClassExpression() {
        Glob glob = new GlobImpl("testChar[act]erClassExpression");
        Assertions.assertEquals("^\\QtestChar\\E[\\Qact\\E]\\QerClassExpression\\E$", glob.asRegex());
    }

    @Test
    public void testGlobCharacterClassExpressionNegation() {
        Glob glob = new GlobImpl("testChar[!neg]agtion");
        Assertions.assertEquals("^\\QtestChar\\E[^\\Qneg\\E]\\Qagtion\\E$", glob.asRegex());
    }

    @Test
    public void testGlobCharacterClassExpressionEscape() {
        Glob glob = new GlobImpl("[\\]]");
        Assertions.assertEquals("^[\\Q]\\E]$", glob.asRegex());
    }
}
