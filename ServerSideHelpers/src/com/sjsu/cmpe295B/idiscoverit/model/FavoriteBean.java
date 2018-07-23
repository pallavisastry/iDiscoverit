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
@Table(name = "favorites")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FavoriteBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2580963249146799605L;
	
	private long favoritesId;
	private User user;
	private Media media;
	private boolean isFavorite;
	
	FavoriteBean(){
		super();
	}
	public FavoriteBean(User u,Media m,boolean isFav){
		this.user= u;
		this.media=m;
		this.isFavorite=isFav;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "favoritesId",nullable=false,unique=true,insertable=false,updatable=false)
	public long getFavoritesId() {
		return favoritesId;
	}
	public void setFavoritesId(long favoritesId) {
		this.favoritesId = favoritesId;
	}
	
	@ManyToOne
	@JoinColumn(name="uid")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@Column(name ="is_favorite")
	public boolean isFavorite() {
		return isFavorite;
	}
	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
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
		sb.append(favoritesId);
		sb.append(",");
		sb.append("isFavorite=");
		sb.append(this.isFavorite);
		sb.append(",");
		sb.append("user=");
		sb.append(this.getUser());
		sb.append(",");
		sb.append("media=");
		sb.append(this.getMedia()+")");
		return sb.toString();		
	}
}
