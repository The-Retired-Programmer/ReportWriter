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
package uk.theretiredprogrammer.scmreportwriter.language;

import uk.theretiredprogrammer.scmreportwriter.Configuration;
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;

public class Property implements Operand {
    
    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) throws InternalParserException {
        operatorstack.pop();
        Operand rhs = operandstack.pop();
        operandstack.push(new Property(DataTypes.isStringLiteral(operandstack.pop()), rhs));
    }

    private final String name;
    private final Operand expression;
    private int location;
    private int length;

    public Property(String name, Operand expression) {
        this.name = name;
        this.expression = expression;
    }
    
    @Override
    public void setLocation(int charoffset, int length) {
        location = charoffset;
        this.length = length;
    }
    
    @Override
    public int getLocation() {
        return location;
    }

    @Override
    public int getLength() {
        return length;
    }
    
    public String getName() {
        return name;
    }
    
    public Operand getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(Configuration configuration, DataSourceRecord datarecord) {
        return expression.evaluate(configuration, datarecord);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
