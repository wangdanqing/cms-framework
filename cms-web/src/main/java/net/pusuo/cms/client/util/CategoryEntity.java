package net.pusuo.cms.client.util;

import java.util.List;

public class CategoryEntity {
	String name;

	List favorite;

	public List getFavorite() {
		return favorite;
	}

	public void setFavorite(List favorite) {
		this.favorite = favorite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
