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
package uk.theretiredprogrammer.reportwriter.language.functions;

import uk.theretiredprogrammer.reportwriter.language.BooleanExpression;
import uk.theretiredprogrammer.reportwriter.datasource.DataRecord;
import uk.theretiredprogrammer.reportwriter.language.DataTypes;
import uk.theretiredprogrammer.reportwriter.language.Language;
import uk.theretiredprogrammer.reportwriter.language.OperandStack;
import uk.theretiredprogrammer.reportwriter.language.OperatorStack;

public class And extends BooleanExpression {

    public static void reduce(Language language, OperatorStack operatorstack, OperandStack operandstack) {
        operatorstack.pop();
        BooleanExpression rhs = DataTypes.isBooleanExpression(operandstack.pop());
        operandstack.push(new And(DataTypes.isBooleanExpression(operandstack.pop()), rhs));
    }

    private final BooleanExpression lhs;
    private final BooleanExpression rhs;

    public And(BooleanExpression lhs, BooleanExpression rhs) {
        super("AND expression");
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Boolean evaluate(DataRecord datarecord) {
        return lhs.evaluate(datarecord) && rhs.evaluate(datarecord);
    }
}
