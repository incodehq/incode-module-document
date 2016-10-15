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
package org.incode.module.document.dom.spi;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.services.ClassNameViewModel;

public interface RendererClassNameService {

    @Programmatic
    public List<ClassNameViewModel> renderClassNamesFor(
            final DocumentNature inputNature,
            final DocumentNature outputNature);

    @Programmatic
    <C extends Renderer> Class<C> asClass(final String className);

}