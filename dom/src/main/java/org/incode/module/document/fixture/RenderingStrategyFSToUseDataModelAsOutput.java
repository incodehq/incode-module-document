/*
 *  Copyright 2016 incode.org
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
package org.incode.module.document.fixture;

import org.incode.module.document.dom.impl.docs.DocumentNature;

import org.incode.module.document.dom.impl.renderers.RendererUsesDataModelAsOutput;

public class RenderingStrategyFSToUseDataModelAsOutput extends RenderingStrategyFSAbstract {

    public static final String REF = "DIRECT";

    @Override
    protected void execute(ExecutionContext executionContext) {
        upsertRenderingStrategy(
                REF,
                "Use input as output",
                DocumentNature.CHARACTERS, DocumentNature.CHARACTERS,
                RendererUsesDataModelAsOutput.class, executionContext);

    }

}
