package org.hibernate.model;

import java.util.Objects;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "CompositeNaturalIdModel")
@Table(name = "CompositeNaturalIdModel")
@Cache(region = "CompositeNaturalIdModelRegion", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NaturalIdCache(region = "CompositeNaturalIdModelNaturalIdRegion")
public class CompositeNaturalIdModel {

	@Id
	private String id;

	@NaturalId
	private String naturalField1;

	@NaturalId
	private String naturalField2;

	private boolean booleanField;

	public CompositeNaturalIdModel() {};

	public CompositeNaturalIdModel(String id, String naturalField1, String naturalField2, boolean booleanField) {
		this.id = id;
		this.naturalField1 = naturalField1;
		this.naturalField2 = naturalField2;
		this.booleanField = booleanField;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNaturalField1() {
		return naturalField1;
	}

	public void setNaturalField1(String naturalField1) {
		this.naturalField1 = naturalField1;
	}

	public boolean isBooleanField() {
		return booleanField;
	}

	public void setBooleanField(boolean booleanField) {
		this.booleanField = booleanField;
	}

	public String getNaturalField2() {
		return naturalField2;
	}

	public void setNaturalField2(String naturalField2) {
		this.naturalField2 = naturalField2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, naturalField1, naturalField2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositeNaturalIdModel other = (CompositeNaturalIdModel)obj;
		return Objects.equals(id, other.id) && Objects.equals(naturalField1, other.naturalField1) && Objects.equals(naturalField2, other.naturalField2);
	}

}
