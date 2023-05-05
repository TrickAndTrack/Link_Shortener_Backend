package com.urlshortner.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.urlshortner.entity.Url;


@Repository
public interface UrlRepository extends MongoRepository<Url, Integer> {
	
	public Url findByShortLink(String shortLink);

}
