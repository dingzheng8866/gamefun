package com.tiny.game.common.dao.impl;

import java.util.List;

import com.tiny.game.common.dao.EmailDao;
import com.tiny.game.common.dao.nosql.cassandra.EmailDaoImplCassandra;
import com.tiny.game.common.domain.email.Email;

public class EmailDaoImpl implements EmailDao {

	@Override
	public void createEmail(Email email) {
		EmailDaoImplCassandra.getInstance().createEmail(email);
	}

	@Override
	public void removeEmail(String emailId) {
		EmailDaoImplCassandra.getInstance().removeEmail(emailId);
	}

	@Override
	public Email getEmail(String emailId) {
		return EmailDaoImplCassandra.getInstance().getEmail(emailId);
	}

	@Override
	public List<Email> getEmails(String roleId) {
		return EmailDaoImplCassandra.getInstance().getEmails(roleId);
	}

}
