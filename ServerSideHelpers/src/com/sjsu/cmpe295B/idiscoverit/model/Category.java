package com.sjsu.cmpe295B.idiscoverit.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7299423128631675241L;
	
	private int categoryId;
	private String categoryName;
	private String categoryTag;
	
	private Collection<Media> audiotronsCollectionInCategory;
	
	Category() {
		super();
	}
	public Category(String catName){
		this.setCategoryName(catName);
		this.setCategoryTag("idsc_other");//DEFAULT VALUE 
	}
	
	public void setCategoryId(int categoryID) {
		this.categoryId = categoryID;
	}
	@Id
	@GeneratedValue
	@Column(name ="categoryId",nullable=false,unique=true,insertable=false,updatable=false)
	public int getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	@Column(name = "category_name")//#columnDefinition is DB-dependent!
	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryTag(String categoryTag) {
		this.categoryTag = categoryTag;
	}
	@Column(name = "category_tag")
	public String getCategoryTag() {
		return categoryTag;
	}
	
	@OneToMany(mappedBy="category",cascade=CascadeType.ALL)
	public Collection<Media> getAudiotronsCollectionInCategory() {
		return audiotronsCollectionInCategory;
	}
	public void setAudiotronsCollectionInCategory(Collection<Media> audiotronSet) {
		this.audiotronsCollectionInCategory = audiotronSet;
	}
	//Scaffolding code:adds one media-file to the category collection connecting both ends since its bidirectional
	public void addMedia(Media audiotron){
		audiotronsCollectionInCategory = new HashSet<Media>();
		audiotronsCollectionInCategory.add(audiotron);
		audiotron.setCategory(this);
	}
	@Override
	public String toString(){	
		StringBuilder sb = new StringBuilder();
		sb.append("(id=");
		sb.append(this.getCategoryId());
		sb.append(",");
		sb.append("categoryName=");
		sb.append(this.getCategoryName()+")");
		return sb.toString();		
	}
	
}
