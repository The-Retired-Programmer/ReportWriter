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
//import uk.theretiredprogrammer.scmreportwriter.oldstuff.EntryReport;

public class App {

    public static void main(String args[]) {

        //        try {
//            EntryReport report = new EntryReport();
//            report.checkAdultEntryReport();
//            report.createAdultEntryReport();
//            report.createU18EntryReport();
//        } catch (Exception ex) {
//            System.err.println(ex.getMessage());
//        }
        System.out.println("REPORT WRITER");
        try {
            ReportWriter reportwriter = new ReportWriter(new File("/home/pi/GithubProjects/SCM-ReportWriter/definition2.scm"));
            reportwriter.loadDataFiles();
            reportwriter.createAllReports();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        System.out.println("DONE");
    }
}
