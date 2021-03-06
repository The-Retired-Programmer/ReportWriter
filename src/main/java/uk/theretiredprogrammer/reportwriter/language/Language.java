/*
 * Copyright 2022 Richard Linsdale (richard at theretiredprogrammer.uk).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.reportwriter.language;

import uk.theretiredprogrammer.reportwriter.RPTWTRRuntimeException;
import uk.theretiredprogrammer.reportwriter.language.functions.StringLiteral;

public abstract class Language {

    private SyntaxTreeItem[] operators;

    private SyntaxTreeItem[] symbols;

    public void setSyntaxTreeSymbols(SyntaxTreeItem[] symbols) {
        this.symbols = symbols;
    }

    public void setSyntaxTreeOperators(SyntaxTreeItem[] operators) {
        this.operators = operators;
    }

    // Lexer support
    public enum CharType {
        WHITESPACE, TEXT, SYMBOL, TEXTDELIMITER, EOF
    };

    public CharType charType(char c) {
        if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
            return CharType.TEXT;
        }
        if (c == '"') {
            return CharType.TEXTDELIMITER;
        }
        if (Character.isWhitespace(c)) {
            return CharType.WHITESPACE;
        }
        if (c == Character.MIN_VALUE) {
            return CharType.EOF;
        }
        return CharType.SYMBOL;
    }

    private Operator extractedoperator;

    public String extractOperator(String input) {
        extractedoperator = null;
        for (SyntaxTreeItem terminal : operators) {
            if (terminal.token instanceof Operator operator) {
                if (input.startsWith(terminal.tokenstring)) {
                    extractedoperator = operator;
                    return input.substring(terminal.tokenstring.length());
                }
            }
        }
        return null;
    }

    public Operator getExtractedOperator() {
        return extractedoperator;
    }

    public S_Token getToken(String tokenstring) {
        for (SyntaxTreeItem symbol : symbols) {
            if (symbol.tokenstring.equals(tokenstring)) {
                return symbol.token;
            }
        }
        return new StringLiteral(tokenstring);
    }

    public enum PrecedenceGroup {
        START(0), END (10), 
        EXPBRA(40, 210, true) , EXPSEP(40, true), EXPKET(210, 40),
        PROPERTY(100), OR(110), AND(120), EQ(130), DIADIC(140), MONADIC(150, true),
        BRA(50, 200, true), KET(200, 50);
        
        private final int lprecedence;
        private final int rprecedence;
        private final boolean rightassociated;
        
        PrecedenceGroup(int precedence, boolean rightassociated) {
            this(precedence,precedence, rightassociated);
        }
        
        PrecedenceGroup(int lprecedence, int rprecedence, boolean rightassociated) {
            this.lprecedence = lprecedence;
            this.rprecedence = rprecedence;
            this.rightassociated = rightassociated;
        }
        
        PrecedenceGroup(int precedence) {
            this(precedence,precedence, false);
        }
        
         PrecedenceGroup(int lprecedence, int rprecedence) {
            this(lprecedence,rprecedence, false);
        }
        
        public int lprecedence() {
            return this.lprecedence;
        }
        
        public int rprecedence() {
            return this.rprecedence;
        }
        
        public boolean isrightassociated(){
            return rightassociated;
        }
    }
    
    // if precedence(lhsop) < precedence (rhsop) then SHIFT(rhsop)
    // if precedence(lhsop) > precedence (rhsop) then {REDUCE(lhsop) ... and repeat}
    // if precedence(lhsop) = precedence (rhsop) then if (rightassociated(lhsop) then SHIFT(rhsop) else {REDUCE(lhsop) .. and repeat}
    // ERROR CASES are not handled at this point

    public enum Precedence {
        SHIFT, EQUAL, REDUCE, ERROR
    };
    
    public Precedence getPrecedence(Operator loperator, Operator roperator) {
        int lprecedence = loperator.operatorgroup.lprecedence();
        int rprecedence = roperator.operatorgroup.rprecedence(); 
        if (lprecedence < rprecedence) {
            return Precedence.SHIFT; // rhsoperator
        }
        if (lprecedence > rprecedence) {
            return Precedence.REDUCE; // lhsoperator .. and repeat
        }
        //lprecedence == rprecedence
        return loperator.operatorgroup.isrightassociated()? Precedence.SHIFT: Precedence.REDUCE;
    }
    
    public final Operator OPERATOR_START = new Operator("START", PrecedenceGroup.START, this::reduceSTART);
    public final Operator OPERATOR_END = new Operator("END", PrecedenceGroup.END, this::reduceEND);

    private void reduceSTART(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        throw new RPTWTRRuntimeException("Illegal to reduce on 'START' operator", operatorstack.pop());
    }

    private void reduceEND(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        if (operatorstack.size() != 2) { // ie not end and start
            throw new RPTWTRRuntimeException("Operator stack not empty when reduction completed", operatorstack.pop());
        }
        if (operandstack.size() != 1) {
            throw new RPTWTRRuntimeException("Single operand expected (the program) - wrong number of operands remain", operatorstack.pop());
        }
    }

    public void reduceEXPRESSIONSEPARATOR(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        throw new RPTWTRRuntimeException("Illegal to reduce on ',' operator", operatorstack.pop());
    }

    public void reduceBRA(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        throw new RPTWTRRuntimeException("Illegal to reduce on '(' operator", operatorstack.pop());
    }

    public void reduceKET(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        Operator operator = operatorstack.pop(); // this will be the closing bracket
        if (operatorstack.peek().toString().equals("(")) {
            operatorstack.pop();
            return;
        }
        throw new RPTWTRRuntimeException("Bad syntax - '(' ... ')' has embedded operator", operatorstack.peek());
    }
}
