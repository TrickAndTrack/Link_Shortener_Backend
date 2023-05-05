package com.urlshortner.service;

import org.springframework.stereotype.Service;

import com.urlshortner.entity.Url;


@Service
public interface UrlService {
	public Url generateShortLink(Url url);

	

	public Url getEncodedUrl(String url);

}
