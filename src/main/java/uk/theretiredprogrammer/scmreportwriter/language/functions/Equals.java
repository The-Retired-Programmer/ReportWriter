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
package uk.theretiredprogrammer.scmreportwriter.language.functions;

import uk.theretiredprogrammer.scmreportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.scmreportwriter.language.StringExpression;
import uk.theretiredprogrammer.scmreportwriter.DataSourceRecord;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.InternalParserException;
import uk.theretiredprogrammer.scmreportwriter.language.Language;
import uk.theretiredprogrammer.scmreportwriter.language.OperandStack;
import uk.theretiredprogrammer.scmreportwriter.language.OperatorStack;

public class Equals extends BooleanExpression{
    
    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) throws InternalParserException {
        operatorstack.pop();
        StringExpression rhs = DataTypes.isStringExpression(operandstack.pop());
        operandstack.push(new Equals(DataTypes.isStringExpression(operandstack.pop()), rhs));
    }
    
    private final StringExpression leftnode;
    private final StringExpression rightnode;
    
    public Equals(StringExpression leftnode, StringExpression rightnode) {
        super("EQUALS expression");
        this.leftnode = leftnode;
        this.rightnode = rightnode;
    }

    @Override
    public Boolean evaluate(DataSourceRecord datarecord) {
        return leftnode.evaluate(datarecord).equals(rightnode.evaluate(datarecord));
    }
}
