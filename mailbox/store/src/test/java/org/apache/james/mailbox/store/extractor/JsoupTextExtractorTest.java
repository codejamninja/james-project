/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mailbox.store.extractor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.james.mailbox.extractor.ParsedContent;
import org.apache.james.mailbox.extractor.TextExtractor;
import org.junit.Before;
import org.junit.Test;

public class JsoupTextExtractorTest {
    private TextExtractor textExtractor;

    @Before
    public void setUp() {
        textExtractor = new JsoupTextExtractor();
    }

    @Test
    public void extractedTextFromHtmlShouldNotContainTheContentOfTitleTag() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("documents/html.txt");

        assertThat(textExtractor.extractContent(inputStream, "text/html").getTextualContent().get())
                .doesNotContain("*|MC:SUBJECT|*");
    }

    @Test
    public void extractContentShouldHandlePlainText() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("myText".getBytes(StandardCharsets.UTF_8));

        assertThat(textExtractor.extractContent(inputStream, "text/plain").getTextualContent())
                .contains("myText");
    }

    @Test
    public void extractContentShouldHandleArbitraryTextMediaType() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("myText".getBytes(StandardCharsets.UTF_8));

        assertThat(textExtractor.extractContent(inputStream, "text/arbitrary").getTextualContent())
                .isEmpty();
    }

    @Test
    public void extractContentShouldReturnEmptyWhenNullData() throws Exception {
        assertThat(textExtractor.extractContent(null, "text/html"))
            .isEqualTo(ParsedContent.empty());
    }

    @Test
    public void extractContentShouldReturnEmptyWhenNullContentType() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("documents/html.txt");

        assertThat(textExtractor.extractContent(inputStream, null))
            .isEqualTo(ParsedContent.empty());
    }

}