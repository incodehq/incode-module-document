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
package org.incode.module.document.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class DocumentModuleTearDown extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Paperclip\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Applicability\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Document\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentTemplate\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentAbstract\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentType\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"RenderingStrategy\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
