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

//
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.scmreportwriter.language.DataTypes;
import uk.theretiredprogrammer.scmreportwriter.language.ExpressionMap;
import uk.theretiredprogrammer.scmreportwriter.language.InternalReportWriterException;

// provide a data model which contains an individual csv file exported from SCM
//
public class DataSourceCSVExtended extends DataSource {

    public DataSourceCSVExtended(ExpressionMap parameters) throws IOException, InternalReportWriterException {
        String path = DataTypes.isStringLiteral(parameters, "path");
        File f = new File(path);
        try ( Reader rdr = new FileReader(f);  BufferedReader brdr = new BufferedReader(rdr)) {
            charsource = new CharacterSource(brdr.lines().toList());
            createDataSourceRecords(charsource);
        }
    }
    
    private final CharacterSource charsource;
    private List<String> columnKeys;
    private enum State {
        STARTOFFIELD, INQUOTEDFIELD, INUNQUOTEDFIELD, AFTERQUOTEDFIELD
    }
    private State state;
    private List<String> tokenlist;
    private StringBuilder token;
    private boolean inData = false;

    private void createDataSourceRecords(CharacterSource charsource) throws IOException {
        state = State.STARTOFFIELD;
        tokenlist = new ArrayList<>();
        token = new StringBuilder();
        try {
            do {
                processNextChar(charsource);
            } while (!charsource.isEOF());
            
        } catch (IOException ex) {
            throw new IOException(ex.getMessage() + charsource.getCurrentLine());
        }
    }
    
    private void processlineoftokens() throws IOException {
        if (inData) {
            if (columnKeys.size() != tokenlist.size()) {
                throw new IOException("Badly formatted CSV (columns count inconsistent): " + charsource.getCurrentLine());
            }
            DataSourceRecord record = new DataSourceRecord();
            for (String key : columnKeys) {
                record.put(key, tokenlist.remove(0));
            }
            add(record);
        } else {
            columnKeys = tokenlist;
            inData = true;
        }
        tokenlist = new ArrayList<>();
        state = State.STARTOFFIELD;
        token = new StringBuilder();
    }

    private void processNextChar(CharacterSource charsource) throws IOException {
        char c = charsource.getChar();
        switch (state) {
            case STARTOFFIELD -> {
                switch (c) {
                    case '\n' -> {
                        tokenlist.add(token.toString());
                        processlineoftokens();
                    }
                    case ',' -> {
                        tokenlist.add(token.toString());
                        token = new StringBuilder();
                    }
                    case ' ' -> {
                    }
                    case '"' ->
                        state = State.INQUOTEDFIELD;
                    default -> {
                        token.append(c);
                        state = State.INUNQUOTEDFIELD;
                    }
                }
            }
            case INQUOTEDFIELD -> {
                switch (c) {
                    case '\n' ->
                        token.append('\n'); // insert as field content
                    case '"' -> {
                        if (charsource.peekChar() == '"') {
                            token.append(c);
                            charsource.getChar();
                        } else {
                            tokenlist.add(token.toString());
                            token = new StringBuilder();
                            state = State.AFTERQUOTEDFIELD;
                        }
                    }
                    default ->
                        token.append(c);
                }
            }

            case INUNQUOTEDFIELD -> {
                switch (c) {
                    case '\n' -> {
                        tokenlist.add(token.toString());
                        processlineoftokens();
                    }
                    case ',' -> {
                        state = State.STARTOFFIELD;
                        tokenlist.add(token.toString());
                        token = new StringBuilder();
                    }
                    default ->
                        token.append(c);
                }
            }

            case AFTERQUOTEDFIELD -> {
                switch (c) {
                    case '\n' -> {
                        processlineoftokens();
                    }
                    case ',' ->
                        state = State.STARTOFFIELD;
                    case ' ' -> {
                    }
                    default ->
                        throw new IOException("Badly formatted CSV (extra text after closing quote): ");
                }
            }
        }
    }

    private class CharacterSource {
        
        private final List<String> lines;
        private int linesindex;

        private char[] currentline;
        private int characteroffset;
        
        private boolean atEOF = false;

        public CharacterSource(List<String> lines) {
            this.lines = lines;
            this.linesindex = 0;
            getnextline();
        }

        private void getnextline() {
            if (linesindex < lines.size()) {
                currentline = lines.get(linesindex++).toCharArray();
                characteroffset = 0;
            } else {
                atEOF = true;
            }
        }

        public char getChar() {
            if (characteroffset < currentline.length) {
                return currentline[characteroffset++];
            }
            getnextline();
            return '\n';
        }
        
        public char peekChar() {
            if (characteroffset < currentline.length) {
                return currentline[characteroffset];
            }
            return '\n';
        }
        
        public String getCurrentLine() {
            return lines.get(linesindex-1);
        }
        
        public boolean isEOF() {
            return atEOF;
        }
    }
}