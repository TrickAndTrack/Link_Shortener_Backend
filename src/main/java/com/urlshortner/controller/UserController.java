package com.urlshortner.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.urlshortner.entity.Url;
import com.urlshortner.entity.UrlErrorResponse;
import com.urlshortner.service.UrlService;
import com.urlshortner.service.Impl.UrlServiceImpl;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000/")
public class UserController {

	@Autowired
	private UrlService urlService;

	@PostMapping("/generate")
	public ResponseEntity<?> generateShortLink(@RequestBody Url userUrl) {
		try {
			Url user1 = urlService.generateShortLink(userUrl);
			return ResponseEntity.status(HttpStatus.CREATED).body(user1);
		} catch (HttpClientErrorException ex) {

			return ResponseEntity.status(ex.getStatusCode()).build();
		} catch (HttpServerErrorException ex) {

			return ResponseEntity.status(ex.getStatusCode()).build();
		} catch (Exception ex) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{shortLink}")
	public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response)
			throws IOException {

		if (StringUtils.isEmpty(shortLink)) {
			UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
			urlErrorResponse.setError("Invalid Url");
			urlErrorResponse.setStatus("204");
			return new ResponseEntity<UrlErrorResponse>(urlErrorResponse, HttpStatus.NO_CONTENT);
		}
		Url urlToRet = urlService.getEncodedUrl(shortLink);

		if (urlToRet == null) {
			UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
			urlErrorResponse.setError("Url does not exist!!");
			urlErrorResponse.setStatus("404");
			return new ResponseEntity<UrlErrorResponse>(urlErrorResponse, HttpStatus.NOT_FOUND);
		}

		if (urlToRet.getExpirationDate().isBefore(LocalDateTime.now())) {
			UrlErrorResponse urlErrorResponse = new UrlErrorResponse();
			urlErrorResponse.setError("Url Expired. Please try generating a fresh one.");
			urlErrorResponse.setStatus("504");
			return new ResponseEntity<UrlErrorResponse>(urlErrorResponse, HttpStatus.OK);
		}

		response.sendRedirect(urlToRet.getOriginalUrl());
		return null;

	}

}
