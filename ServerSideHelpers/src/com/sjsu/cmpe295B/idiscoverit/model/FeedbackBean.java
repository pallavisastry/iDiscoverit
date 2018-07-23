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
@Table(name = "feedback")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeedbackBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2419869292845350692L;
	
	private long feedbackId;
	private long rating;
	private boolean flag;

	private Media media;
	private User user;
	
	
	FeedbackBean(){
		super();
	}

	public FeedbackBean(long rating,boolean flag,User u,Media m){
		this.rating=rating;
		this.flag=flag;
		this.setUser(u);
		this.setMedia(m);
	}
	@Id
	@GeneratedValue
	@Column(name = "feedbackId",nullable=false,unique=true,insertable=false,updatable=false)
	public long getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(long feedbackId) {
		this.feedbackId = feedbackId;
	}

	@Column(name = "rating",columnDefinition="bigint(20) default 0") //#This column defn makes the app system dependent 
	public long getRating() {
		return rating;
	}
	public void setRating(long rating) {
		this.rating = rating;
	}
	
	@Column(name = "flagged")
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	@ManyToOne
	@JoinColumn(name="mediaId")
	public Media getMedia() {
		return media;
	}
	public void setMedia(Media media) {
		this.media = media;
	}
	@ManyToOne
	@JoinColumn(name="uid")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString(){	
		StringBuilder sb = new StringBuilder();
		sb.append("(id=");
		sb.append(feedbackId);
		sb.append(",");
		sb.append(",rating=");
		sb.append(this.getRating());
		sb.append(",");
		sb.append("flagged=");
		sb.append(this.isFlag());
		sb.append(",");
		sb.append("user=");
		sb.append(this.getUser());
		sb.append(",");
		sb.append("media=");
		sb.append(this.getMedia()+")");
		return sb.toString();		
	}
}
