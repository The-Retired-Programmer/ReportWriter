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

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ReportWriterTest {

    public ReportWriterTest() {
    }

    @Test
    public void testAppfunctionality() throws IOException {
        System.out.println("App functionality");
        try {
            ReportWriter reportwriter = new ReportWriter(new File("/home/pi/GithubProjects/SCM-ReportWriter/definition2.scm"));
            reportwriter.loadDataFiles();
            reportwriter.createAllReports();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("exception caught");
        }
    }
}