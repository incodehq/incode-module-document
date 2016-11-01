/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
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
package org.incode.module.document.dom.mixins;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.applicability.Binder;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.services.ClassService;

public abstract class T_createDocumentAbstract<T> {

    public static enum Intent {
        PREVIEW,
        CREATE_AND_ATTACH
    }

    //region > constructor
    protected final T domainObject;

    public T_createDocumentAbstract(final T domainObject) {
        this.domainObject = domainObject;
    }
    //endregion

    /**
     * Either preview the document's content (as a URL to that content) or alternatively create a {@link Document} and
     * attach using a {@link Paperclip}.
     *
     * <p>
     * Which occurs depends upon the provided {@link Intent}.
     * </p>
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object $$(
            final DocumentTemplate template,
            @ParameterLayout(named = "Action")
            final Intent intent
            ) throws IOException {
        final String roleName = null;
        final String additionalTextIfAny = null;

        final Binder.Binding binding = template.newBinding(domainObject, additionalTextIfAny);
        if (intent == Intent.PREVIEW) {
            return template.preview(binding.getDataModel());
        }

        final List<Object> attachTo = binding.getAttachTo();
        final boolean shouldPersist = !attachTo.isEmpty();

        final DocumentAbstract doc = doCreate(template, shouldPersist, additionalTextIfAny);

        for (Object o : attachTo) {
            if(paperclipRepository.canAttach(o)) {
                paperclipRepository.attach(doc, roleName, o);
            }
        }
        return doc;
    }


    public boolean hide$$() {
        return choices0$$().isEmpty();
    }

    /**
     * All templates which are applicable to the domain object's atPath, and which either be previewed or, if not previewable,
     * then can be created and attached to at least one domain object.
     */
    public List<DocumentTemplate> choices0$$() {
        return getDocumentTemplates();
    }

    /**
     * For the selected template, the list of actions available (based on the binding)
     */
    public List<Intent> choices1$$(final DocumentTemplate template) {
        if(template == null) {
            return Lists.newArrayList();
        }
        final Binder.Binding binding = template.newBinding(domainObject, null);
        return documentTemplateService.intentsFor(template, binding.getAttachTo());
    }

    private List<DocumentTemplate> getDocumentTemplates() {
        return documentTemplateService.documentTemplates(domainObject);
    }


    /**
     * Mandatory hook method
     */
    protected abstract DocumentAbstract doCreate(
            final DocumentTemplate template,
            final boolean shouldPersist,
            final String additionalTextIfAny);

    //region > injected services

    @Inject
    DocumentTemplateForAtPathService documentTemplateService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    ClassService classService;

    //endregion

}
