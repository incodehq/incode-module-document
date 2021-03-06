/*
 *  Copyright 2016 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.document.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class Document_downloadExternalUrlAsBlob_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Document mockDocument;

    Document_downloadExternalUrlAsBlob mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new Document_downloadExternalUrlAsBlob(mockDocument);
    }

    public static class HideTest extends Document_downloadExternalUrlAsBlob_Test {

        @Ignore
        @Test
        public void hidden_if_not_an_external_blob() throws Exception {

        }

    }

    public static class ActionInvocation_Test extends Document_downloadExternalUrlAsBlob_Test {

        @Ignore
        @Test
        public void delegates_to_UrlDownloadService() throws Exception {

        }
    }


}