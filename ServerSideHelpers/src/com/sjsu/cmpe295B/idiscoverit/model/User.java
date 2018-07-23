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
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {

	private static final long serialVersionUID = 6584598083225163623L;
	private long uid;
	private String userName;
	private String password;
	private Collection<Media> audiotronCollectionList;
	private Collection<FeedbackBean> feedbackCollectionList;
	private Collection<FavoriteBean> favoritesCollectionList;
	private Collection<TagBean> tagsCollectionList;
	// include login/logout time.,2 user types:Creator/Listener

	User() {
		super();
	}

	public User(String userName, String password) {
		this.setUserName(userName);
		this.setPassword(password);
	}

	@Id
	@GeneratedValue
	@Column(name = "uid",nullable=false,unique=true,insertable=false,updatable=false)
	public Long getUid() {
		Long id = this.uid;
		return id;
	}

	public void setUid(long user_id) {
		this.uid = user_id;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "username")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	public Collection<Media> getAudiotronCollectionList() {
		return audiotronCollectionList;
	}

	public void setAudiotronCollectionList(Collection<Media> mediaCollectionList) {
		this.audiotronCollectionList = mediaCollectionList;
	}

	// Scaffolding code:current user adds one media-file connecting both ends since its bidirectional
	public void addAudiotron(Media audiotron) {
		audiotronCollectionList = new HashSet<Media>();
		audiotronCollectionList.add(audiotron);
		audiotron.setUser(this);
	}

	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	public Collection<FeedbackBean> getFeedbackCollectionList() {
		return feedbackCollectionList;
	}
	public void setFeedbackCollectionList(
			Collection<FeedbackBean> feedbackCollectionList) {
		this.feedbackCollectionList = feedbackCollectionList;
	}
	// Scaffolding code:current user adds one comment connecting both ends since its bidirectional
	public void addFeedback(FeedbackBean fBean) {
		feedbackCollectionList = new HashSet<FeedbackBean>();
		feedbackCollectionList.add(fBean);
		fBean.setUser(this);
	}
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	public Collection<FavoriteBean> getFavoritesCollectionList() {
		return favoritesCollectionList;
	}
	public void setFavoritesCollectionList(Collection<FavoriteBean> favoritesCollectionList) {
		this.favoritesCollectionList = favoritesCollectionList;
	}
	// Scaffolding code:current user marks one media as favorite connecting both ends since its bidirectional
	public void addFavorite(FavoriteBean favBean) {
			favoritesCollectionList= new HashSet<FavoriteBean>();
			favoritesCollectionList.add(favBean);
			favBean.setUser(this);
	}
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	public Collection<TagBean> getTagsCollectionList() {
		return tagsCollectionList;
	}
	public void setTagsCollectionList(Collection<TagBean> tagsCollectionList) {
		this.tagsCollectionList = tagsCollectionList;
	}
	// Scaffolding code:current user adds one tag to media connecting both ends since its bidirectional
	public void addTag(TagBean tagBean) {
		tagsCollectionList = new HashSet<TagBean>();
		tagsCollectionList.add(tagBean);
		tagBean.setUser(this);
	}
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(id=");
		sb.append(uid);
		sb.append(",");
		sb.append("Username=");
		sb.append(userName);
		sb.append(")");
		return sb.toString();
	}

	
}
