package com.tiny.game.common.dao.nosql.cassandra;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.tiny.game.common.dao.EmailDao;
import com.tiny.game.common.domain.email.Email;
import com.tiny.game.common.domain.email.EmailAttachment;
import com.tiny.game.common.exception.InternalBugException;

public class EmailDaoImplCassandra implements EmailDao {
	private static final Logger logger = LoggerFactory.getLogger(EmailDaoImplCassandra.class);
	
	private Session session = CassandraManager.getInstance().getSession();
	
	private static class SingletonHolder {
		private static EmailDaoImplCassandra instance = new EmailDaoImplCassandra();
	}

	public static EmailDaoImplCassandra getInstance() {
		return SingletonHolder.instance;
	}
	
	private EmailRSH emailRSH = new EmailRSH();
	private static class EmailRSH extends CqlAbstractResultSetHandler<Email> {
		public Email factoryBeanObject(Row rs) {
			Email bean = new Email();
			bean.setEmailId(rs.getString("emailId"));
			bean.setToRoleId(rs.getString("toRoleId"));
			bean.setFromGroupTypeId(rs.getInt("fromGroupTypeId"));
			bean.setFromRoleId(rs.getString("fromRoleId"));
			bean.setTitleId(rs.getInt("titleId"));
			bean.setContentId(rs.getInt("titleId"));
			bean.setContentParameters(toListParameters(rs.getString("contentParameters")));
			bean.setAttachments(toEmailAttachments(rs.getString("attachment")));
			bean.setLastUpdateTime(rs.getTimestamp("lastUpdateTime"));
			return bean;
		}
	}
	
	private static List<String> toListParameters(String para) {
		List<String> list = new ArrayList<String>();
		if(StringUtils.isNotEmpty(para)) {
			String[] sa = StringUtils.split(para, "#");
			for(String s : sa) {
				if(StringUtils.isNotEmpty(s)) {
					list.add(s);
				}
			}
		}
		return list;
	}
	
	//CREATE TABLE if not exists gamefun.email (emailId text,toRoleId text,fromGroupTypeId int,fromRoleId text, titleId int, contentId int, contentParameters text, attachment text, lastUpdateTime timestamp,PRIMARY KEY (emailId));
	@Override
	public void createEmail(Email email) {
		String cql = "INSERT INTO gamefun.email (emailId,toRoleId,fromGroupTypeId,fromRoleId,titleId,contentId,contentParameters,attachment,lastUpdateTime) VALUES (?,?,?,?,?,?,?,?,?);";
		session.execute(cql, email.getEmailId(),email.getToRoleId(),email.getFromGroupTypeId(),email.getTitleId(),email.getContentId(),
				StringUtils.join(email.getContentParameters(), "#"), toText(email.getAttachments()), email.getLastUpdateTime().getTime());
	}

	private String toText(List<EmailAttachment> attachments) {
		StringBuffer sb = new StringBuffer();
		if(attachments!=null) {
			for(int i=0; i<attachments.size(); i++) {
				EmailAttachment ea = attachments.get(i);
				if(i > 0) {
					sb.append("#");
				}
				sb.append(ea.getItemId());
				sb.append(","+ea.getItemLevel());
				sb.append(","+ea.getCount());
			}
		}
		return sb.toString();
	}
	
	private static List<EmailAttachment> toEmailAttachments(String para) {
		List<EmailAttachment> list = new ArrayList<EmailAttachment>();
		if(StringUtils.isNotEmpty(para)) {
			String[] sa = StringUtils.split(para, "#");
			for(String s : sa) {
				if(StringUtils.isNotEmpty(s)) {
					String[] sa2 = StringUtils.split(s, ",");
					if(sa2.length!=3) {
						throw new InternalBugException("Invalid format of attachment: " + s);
					}
					for(int i=0; i< sa2.length; i++) {
						EmailAttachment ea = new EmailAttachment(Integer.parseInt(sa2[0]), Integer.parseInt(sa2[1]), Integer.parseInt(sa2[2]));
						list.add(ea);
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public void removeEmail(String emailId) {
		String cql = "DELETE FROM gamefun.email WHERE emailId=?;";
		session.execute(cql, emailId);
	}

	@Override
	public Email getEmail(String emailId) {
		String cql = "SELECT * FROM gamefun.email where emailId=?;";
		ResultSet rs = session.execute(cql, emailId);
		return emailRSH.buildSingle(rs);
	}

	@Override
	public List<Email> getEmails(String roleId) {
		String cql = "SELECT * FROM gamefun.email where toRoleId=? ALLOW FILTERING;";
		ResultSet rs = session.execute(cql, roleId);
		return emailRSH.buildMultiple(rs);
	}

}
