/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.incode.module.document.app.menu.home;

import java.util.List;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.fixture.dom.demo.DemoObject;
import org.incode.module.document.fixture.dom.demo.DemoObjectMenu;
import org.incode.module.document.fixture.dom.other.OtherObject;
import org.incode.module.document.fixture.dom.other.OtherObjectMenu;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.incode.module.document.app.menu.home.HomePageViewModel"
)
public class HomePageViewModel {

    public TranslatableString title() {
        return TranslatableString.tr("{demo} demo, {other} other", "demo", getDemoObjects().size(), "other", getOtherObjects().size());
    }

    public List<DemoObject> getDemoObjects() {
        return demoObjectMenu.listAll();
    }

    public List<OtherObject> getOtherObjects() {
        return otherObjectMenu.listAll();
    }

    @javax.inject.Inject
    DemoObjectMenu demoObjectMenu;

    @javax.inject.Inject
    OtherObjectMenu otherObjectMenu;

}
