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
package uk.theretiredprogrammer.scmreportwriter;

import uk.theretiredprogrammer.scmreportwriter.expression.Expression;

public class Field {

    private final Expression<String> heading;
    private final Expression<String> value;
    
    public Field(Expression<String> heading,Expression<String> value) {
        this.heading = heading;
        this.value = value;
    }
    
    public String getHeading(DataSourceRecord record) {
        return heading.evaluate(record);
    }
    
    public String getValue(DataSourceRecord record) {
        return value.evaluate(record);
    }
}