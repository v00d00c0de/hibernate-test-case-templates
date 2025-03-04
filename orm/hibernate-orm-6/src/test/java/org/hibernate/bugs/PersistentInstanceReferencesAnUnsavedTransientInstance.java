package org.hibernate.bugs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.annotations.NaturalId;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;

@DomainModel(annotatedClasses = { PersistentInstanceReferencesAnUnsavedTransientInstance.User.class, PersistentInstanceReferencesAnUnsavedTransientInstance.Group.class })
@SessionFactory
class PersistentInstanceReferencesAnUnsavedTransientInstance {

	@Entity
	public static class User {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;

		@NaturalId
		private String naturalId;

		@ManyToMany(fetch = FetchType.EAGER)
		@JoinTable(name = "GROUP_MEMBERS", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID"))
		private List<Group> groups = new ArrayList<>();

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getNaturalId() {
			return naturalId;
		}

		public void setNaturalId(String userName) {
			this.naturalId = userName;
		}

		public List<Group> getGroups() {
			return groups;
		}

		public void setGroups(List<Group> groups) {
			this.groups = groups;
		}

	}

	@Entity
	public static class Group {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;

		@NaturalId
		private String naturalId;

		@ManyToMany(fetch = FetchType.EAGER)
		@JoinTable(name = "GROUP_MEMBERS", joinColumns = @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
		private Set<User> members = new HashSet<>();

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getNaturalId() {
			return naturalId;
		}

		public void setNaturalId(String naturalId) {
			this.naturalId = naturalId;
		}

		public Set<User> getMembers() {
			return members;
		}

		public void setMembers(Set<User> members) {
			this.members = members;
		}

	}

	@Test
	void testRemoveUserBySessionRemove(SessionFactoryScope scope) {
		scope.inTransaction(session -> {
			Group group = new Group();
			group.setNaturalId("group1");
			session.persist(group);

			User user = new User();
			user.setNaturalId("user1");
			session.persist(user);

			HashSet<User> users = new HashSet<User>();
			users.add(user);
			group.setMembers(users);
			session.merge(group);
		});
		scope.inTransaction(session -> {
			NaturalIdLoadAccess<User> byNaturalIdLoader = session.byNaturalId(User.class);
			byNaturalIdLoader.using("naturalId", "user1");
			User user = byNaturalIdLoader.load();

			session.remove(user);
		});
	}

	@Test
	void testRemoveUserByDeleteCriteria(SessionFactoryScope scope) {
		scope.inTransaction(session -> {
			Group group = new Group();
			group.setNaturalId("group2");
			session.persist(group);
	
			User user = new User();
			user.setNaturalId("user2");
			session.persist(user);
	
			HashSet<User> users = new HashSet<User>();
			users.add(user);
			group.setMembers(users);
			session.merge(group);
		});
		scope.inTransaction(session -> {
			NaturalIdLoadAccess<User> byNaturalId = session.byNaturalId(User.class);
			byNaturalId.using("naturalId", "user2");
			User user = byNaturalId.load();
	
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaDelete<User> delete = cb.createCriteriaDelete(User.class);
			Root<User> root = delete.from(User.class);
			delete.where(cb.equal(root.get("naturalId"), "user2"));
			session.createMutationQuery(delete).executeUpdate();
		});
	}

}