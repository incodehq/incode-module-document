/*
 *  Copyright 2014~2015 Dan Haywood
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
package org.incode.module.document.integtests.mixins;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.wrapper.HiddenException;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.fixture.app.paperclips.demo.PaperclipForDemoObject;
import org.incode.module.document.fixture.dom.demo.DemoObject;
import org.incode.module.document.fixture.dom.other.OtherObject;
import org.incode.module.document.fixture.scripts.data.DemoObjectsFixture;
import org.incode.module.document.fixture.scripts.data.OtherObjectsFixture;
import org.incode.module.document.fixture.scripts.teardown.DocumentDemoAppTearDownFixture;
import org.incode.module.document.fixture.seed.DocumentTypeAndTemplatesApplicableForDemoObjectFixture;
import org.incode.module.document.integtests.DocumentModuleIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class T_createAndAttachDocumentAndRender_IntegTest extends DocumentModuleIntegTest {

    DemoObject demoObject;
    OtherObject otherObject;

    DocumentTypeAndTemplatesApplicableForDemoObjectFixture templateFixture;

    @Before
    public void setUpData() throws Exception {

        fixtureScripts.runFixtureScript(new DocumentDemoAppTearDownFixture(), null);

        // types + templates
        templateFixture = new DocumentTypeAndTemplatesApplicableForDemoObjectFixture();
        fixtureScripts.runFixtureScript(templateFixture, null);

        // demo objects
        final DemoObjectsFixture demoObjectsFixture = new DemoObjectsFixture();
        fixtureScripts.runFixtureScript(demoObjectsFixture, null);
        demoObject = demoObjectsFixture.getDemoObjects().get(0);

        // other objects
        final OtherObjectsFixture otherObjectsFixture = new OtherObjectsFixture();
        fixtureScripts.runFixtureScript(otherObjectsFixture, null);
        otherObject = otherObjectsFixture.getOtherObjects().get(0);

        transactionService.flushTransaction();
    }

    public static class ActionImplementation_IntegTest extends T_createAndAttachDocumentAndRender_IntegTest {

        @Test
        public void can_create_document() throws Exception {

            // given
            assertThat(wrap(_documents(demoObject)).$$()).isEmpty();

            // when
            final List<DocumentTemplate> templates = _createAndAttachDocumentAndRender(demoObject).choices0$$();

            transactionService.nextTransaction();

            // then
            assertThat(templates).hasSize(4);

            // when
            Set<Document> documents = Sets.newHashSet();
            for (DocumentTemplate template : templates) {

                final Object documentAsObj = _createAndAttachDocumentAndRender(demoObject).$$(template);

                // then
                assertThat(documentAsObj).isInstanceOf(Document.class);
                Document document = (Document) documentAsObj;
                documents.add(document);

            }


            // for each
            for (Document document : documents) {

                // when
                final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);

                // then

                // (depends on the binder of the template as to how many associated)
                if (document.getType().getReference().equals("XDOCREPORT-DOC")) {
                    assertThat(paperclips).hasSize(2);

                    for (Paperclip paperclip : paperclips) {
                        final DocumentAbstract paperclipDocument = paperclip.getDocument();
                        assertThat(paperclipDocument).isSameAs(document);
                    }

                    final Object paperclipAttachedTo = paperclips.get(0).getAttachedTo();
                    assertThat(paperclipAttachedTo).isSameAs(demoObject);

                    final Object paperclipAttachedTo2 = paperclips.get(1).getAttachedTo();
                    assertThat(paperclipAttachedTo2).isSameAs(otherObject);
                }
                else {
                    assertThat(paperclips).hasSize(1);

                    final DocumentAbstract paperclipDocument = paperclips.get(0).getDocument();
                    assertThat(paperclipDocument).isSameAs(document);

                    final Object paperclipAttachedTo = paperclips.get(0).getAttachedTo();
                    assertThat(paperclipAttachedTo).isSameAs(demoObject);
                }
            }


            // then
            final PaperclipForDemoObject._documents wrappedMixin = wrap(_documents(demoObject));
            final List<Paperclip> clips = wrappedMixin.$$();
            assertThat(clips).hasSize(4);

            // when
            final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(demoObject);
            assertThat(paperclips).hasSize(4);

            final Set<Document> attachedDocuments = paperclips.stream().map(x -> (Document) x.getDocument()).collect(Collectors.toSet());

            // then
            assertContainSame(documents, attachedDocuments);
        }

        private void assertContainSame(final Set<Document> docSet1, final Set<Document> docSet2) {
            assertThat(docSet1).contains(docSet2.toArray(new Document[] {}));
            assertThat(docSet2).contains(docSet1.toArray(new Document[]{}));
        }

        @Inject
        QueryResultsCache queryResultsCache;
    }

    public static class Hidden_IntegTest extends T_createAndAttachDocumentAndRender_IntegTest {

        @Test
        public void if_no_applicable_templates() throws Exception {

            // when
            final List<DocumentTemplate> templates = _createAndAttachDocumentAndRender(otherObject).choices0$$();

            // then
            assertThat(templates).isEmpty();

            // expect
            expectedExceptions.expect(HiddenException.class);

            // when
            final DocumentTemplate anyTemplate = templateFixture.getFmkTemplate();
            wrap(_createAndAttachDocumentAndRender(otherObject)).$$(anyTemplate);
        }


    }


    @Inject
    PaperclipRepository paperclipRepository;

}