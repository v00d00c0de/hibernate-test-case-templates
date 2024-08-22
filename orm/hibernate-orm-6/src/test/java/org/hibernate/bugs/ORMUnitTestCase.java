/*
 * Copyright 2014 JBoss Inc
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
package org.hibernate.bugs;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.model.CompositeNaturalIdModel;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

@DomainModel(annotatedClasses = { org.hibernate.model.CompositeNaturalIdModel.class })
@SessionFactory
class ORMUnitTestCase {

	@Test
	void hhh123Test(SessionFactoryScope scope) throws Exception {
		scope.inTransaction(session -> {
			CompositeNaturalIdModel compositeNaturalIdModel = new CompositeNaturalIdModel("1", "naturalValuePart1", "naturalValuePart2", true);
			session.persist(compositeNaturalIdModel);
			session.flush();

			SessionFactoryImplementor sessionFactory = session.getFactory();
			EntityPersister entityPersister = sessionFactory.getRuntimeMetamodels().getMappingMetamodel().getEntityDescriptor(CompositeNaturalIdModel.class);
			//Throws ArrayIndexOutOfBoundsException
			Object naturalIdentifierSnapshot = entityPersister.getNaturalIdentifierSnapshot("1", session);
		});
	}
}
