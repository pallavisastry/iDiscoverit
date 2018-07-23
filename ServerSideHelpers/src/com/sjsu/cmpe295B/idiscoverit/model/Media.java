package com.sjsu.cmpe295B.idiscoverit.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name = "media")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Media implements Serializable {

	private static final long serialVersionUID = -8767337896773261247L;

	private long mediaId;
	private byte[] audiotron;
	private String audiotronName;
	private String audiotronFormat;
	private Date audiotronCreationDate;
	private long audiotronSize;
	private int totalHitCount;
	private String mediaType;
	private int isActive;
	private long averageRating;
	private User user;
	private Category category;
	private Collection<FeedbackBean> feedbackCollection;
	private Collection<FavoriteBean> favoritesCollection;
	private Collection<TagBean> tagsCollection;
	private long totalRecordingTimeInSeconds;
//	private long totalAverageRating;
	
	Media() {
		super();
	}

	public Media(byte[] audiotron,String fname,User u,Category cat){
		this.audiotron = audiotron;
		this.audiotronName=fname;
		this.user=u;
		this.category=cat;
	}

	@Id
	@GeneratedValue
	@Column(name = "mediaId",nullable=false,unique=true,insertable=false,updatable=false)
	public long getMediaId() {
		return mediaId;
	}

	public void setMediaId(long mediaId) {
		this.mediaId = mediaId;
	}

	@Column(name = "audiotron", columnDefinition = "LONGBLOB")
	public byte[] getAudiotron() {
		return audiotron;
	}

	public void setAudiotron(byte[] audiotron) {
		this.audiotron = audiotron;
	}

	@Column(name = "audiotron_name")
	public String getAudiotronName() {
		return audiotronName;
	}

	public void setAudiotronName(String audiotronName) {
		this.audiotronName = audiotronName;
	}

	@Column(name = "audiotron_format")
	public String getAudiotronFormat() {
		return audiotronFormat;
	}

	public void setAudiotronFormat(String audiotronFormat) {
		this.audiotronFormat = audiotronFormat;
	}

	@Column(name = "audiotron_creation_date")
	public Date getAudiotronCreationDate() {
		return audiotronCreationDate;
	}

	public void setAudiotronCreationDate(Date audiotronCreationDate) {
		this.audiotronCreationDate = audiotronCreationDate;
	}

	@Column(name = "audiotron_size")
	public long getAudiotronSize() {
		return audiotronSize;
	}

	public void setAudiotronSize(long audiotronSize) {
		this.audiotronSize = audiotronSize;
	}
	@Column(name = "media_type")
	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	@Column(name = "total_hits", columnDefinition="bigint(20) default 0")
	public int getTotalHitCount() {
		return totalHitCount;
	}

	public void setTotalHitCount(int totalHitCount) {
		this.totalHitCount = totalHitCount;
	}
	@Column(name = "recording_time_seconds", columnDefinition="bigint(20) default 0")
	public long getTotalRecordingTimeInSeconds() {
		return totalRecordingTimeInSeconds;
	}
	public void setTotalRecordingTimeInSeconds(long totalRecordingTimeInSeconds) {
		this.totalRecordingTimeInSeconds = totalRecordingTimeInSeconds;
	}
	
	@Column(name="is_active", columnDefinition="bigint(20) default 0")
	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	@Column(name="average_rating", columnDefinition="bigint(20) default 0")
	public long getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(long averageRating) {
		this.averageRating = averageRating;
	}
	

	//	@Column(name = "average_rating", columnDefinition="bigint(20) default 0")
//	public long getTotalAverageRating() {
//		return totalAverageRating;
//	}
//
//	public void setTotalAverageRating(long totalAverageRating) {
//		this.totalAverageRating = totalAverageRating;
//	}
	@ManyToOne
	@JoinColumn(name = "uid")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = "categoryId")
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	@OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
	public Collection<FeedbackBean> getFeedbackCollection() {
		return feedbackCollection;
	}
	public void setFeedbackCollection(
			Collection<FeedbackBean> feedbackCollection) {
		this.feedbackCollection = feedbackCollection;
	}
	// Scaffolding code:adds one feedback-media to the feedback collection connecting both ends since its bidirectional
	public void addFeedback(FeedbackBean fBean) {
		feedbackCollection = new HashSet<FeedbackBean>();
		feedbackCollection.add(fBean);
		fBean.setMedia(this);
	}
	
	@OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
	public Collection<FavoriteBean> getFavoritesCollection() {
		return favoritesCollection;
	}
	public void setFavoritesCollection(Collection<FavoriteBean> favoritesCollection) {
		this.favoritesCollection = favoritesCollection;
	}
	// Scaffolding code:adds one favorite-media to the favorite collection connecting both ends since its bidirectional
	public void addFavorite(FavoriteBean favBean) {
		favoritesCollection = new HashSet<FavoriteBean>();
		favoritesCollection.add(favBean);
		favBean.setMedia(this);
	}
	
	@OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
	public Collection<TagBean> getTagsCollection() {
		return tagsCollection;
	}
	public void setTagsCollection(Collection<TagBean> tagsCollection) {
		this.tagsCollection = tagsCollection;
	}
	// Scaffolding code:adds one favorite-media to the favorite collection connecting both ends since its bidirectional
	public void addTag(TagBean tagBean) {
		tagsCollection = new HashSet<TagBean>();
		tagsCollection.add(tagBean);
		tagBean.setMedia(this);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(id=");
		sb.append(mediaId);
		sb.append(" ,auditronName=");
		sb.append(this.getAudiotronName());
		sb.append(" ,total hit count=");
		sb.append(this.getTotalHitCount());
		//sb.append(", average rating=");
		//sb.append(this.getTotalAverageRating());
		sb.append(" ,format=");
		sb.append(this.getAudiotronFormat());
		sb.append(" ,size=");
		sb.append(this.getAudiotronSize());
		sb.append(",audiotronOwner=");
		sb.append(this.getUser()+")");
		return sb.toString();
	}
}
