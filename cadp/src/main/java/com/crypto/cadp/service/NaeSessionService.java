package com.crypto.cadp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingrian.security.nae.NAESession;

@Service
public class NaeSessionService {

	@Autowired
	NaeSessionComponent naeSessionComponent;

	public void createSession() {
		naeSessionComponent.createSession();
	}

	public NAESession getSession() {
		return naeSessionComponent.getSession();
	}

}
