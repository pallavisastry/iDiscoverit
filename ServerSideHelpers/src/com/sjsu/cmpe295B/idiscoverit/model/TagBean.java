package com.sjsu.cmpe295B.idiscoverit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TagBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -499437132523644888L;
	private long tagId;
	private String tagName;
	private User user;
	private Media media;
	
	TagBean(){
		super();
	}
	public TagBean(String tagName,User u, Media m){
		this.setTagName(tagName);
		this.setUser(u);
		this.setMedia(m);
	}
	
	@Id
	@GeneratedValue
	@Column(name = "tagId",nullable=false,unique=true,insertable=false,updatable=false)
	public long getTagId() {
		return tagId;
	}
	public void setTagId(long tagId) {
		this.tagId = tagId;
	}
	
	@Column(name ="tag_name")
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	@ManyToOne
	@JoinColumn(name="uid")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@ManyToOne
	@JoinColumn(name="mediaId")
	public Media getMedia() {
		return media;
	}
	public void setMedia(Media media) {
		this.media = media;
	}
	
	@Override
	public String toString(){	
		StringBuilder sb = new StringBuilder();
		sb.append("(id=");
		sb.append(tagId);
		sb.append(",");
		sb.append("tagName =");
		sb.append(this.tagName);
		sb.append(",");
		sb.append("user=");
		sb.append(this.getUser());
		sb.append(",");
		sb.append("media=");
		sb.append(this.getMedia()+")");
		return sb.toString();		
	}
}
