package com.tiny.game.common.dao;

import java.util.List;

import com.tiny.game.common.domain.email.Email;

public interface EmailDao {

	public void createEmail(Email email);
	public void removeEmail(String emailId);
	public Email getEmail(String emailId);
	public List<Email> getEmails(String roleId);
	
}
