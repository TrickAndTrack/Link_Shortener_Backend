package com.urlshortner.service.Impl;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.urlshortner.entity.DatabaseSequence;
import com.urlshortner.entity.Url;
import com.urlshortner.repository.UrlRepository;
import com.urlshortner.service.UrlService;

@Service
public class UrlServiceImpl implements UrlService {

	@Autowired
	private UrlRepository repository;

	@Override
	public Url generateShortLink(Url url) {

		String encodedUrl = encodeUrl(url.getOriginalUrl());
		Url urlToPersist = new Url();
		urlToPersist.setId(generateSequence(Url.SEQUENCE_NAME));
		urlToPersist.setCreationDate(LocalDateTime.now());
		urlToPersist.setOriginalUrl(url.getOriginalUrl());
		urlToPersist.setShortLink(encodedUrl);
		urlToPersist.setExpirationDate(LocalDateTime.now().plusMinutes(5));
		// Url urlToRet = persistShortLink(urlToPersist);

		return repository.save(urlToPersist);

	}

	private String encodeUrl(String url) {
		String encodedUrl = "";
		LocalDateTime time = LocalDateTime.now();
		encodedUrl = Hashing.murmur3_32().hashString(url.concat(time.toString()), Charset.defaultCharset()).toString();
		return encodedUrl;
	}

	@Override
	public Url getEncodedUrl(String url) {

		return repository.findByShortLink(url);

	}

	private MongoOperations mongoOperations;

	@Autowired
	public UrlServiceImpl(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public long generateSequence(String seqName) {

		DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
				new Update().inc("seq", 1), options().returnNew(true).upsert(true), DatabaseSequence.class);
		return !Objects.isNull(counter) ? counter.getSeq() : 1;

	}

}
