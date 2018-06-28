package com.tiny.game.common.domain.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tiny.game.common.domain.alliance.AllianceMember;
import com.tiny.game.common.exception.InternalBugException;

public class Email {

	private String emailId;
	private String toRoleId;
	private int fromGroupTypeId;
	private String fromRoleId;
	private int titleId;
	private int contentId;
	private List<String> contentParameters = new ArrayList<String>();
	private List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
	private Date lastUpdateTime = null;
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof AllianceMember)) {
			return false;
		}
		
		return emailId.equals(((Email) o).emailId);
	}
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getToRoleId() {
		return toRoleId;
	}
	public void setToRoleId(String toRoleId) {
		this.toRoleId = toRoleId;
	}
	public int getFromGroupTypeId() {
		return fromGroupTypeId;
	}
	public void setFromGroupTypeId(int fromGroupTypeId) {
		this.fromGroupTypeId = fromGroupTypeId;
	}
	public String getFromRoleId() {
		return fromRoleId;
	}
	public void setFromRoleId(String fromRoleId) {
		this.fromRoleId = fromRoleId;
	}
	public int getTitleId() {
		return titleId;
	}
	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}
	public int getContentId() {
		return contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public List<String> getContentParameters() {
		return contentParameters;
	}
	public void setContentParameters(List<String> contentParameters) {
		this.contentParameters = contentParameters;
	}
	public List<EmailAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<EmailAttachment> attachments) {
		this.attachments = attachments;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public void addContentParameter(String para) {
		if(para!=null && para.length() >0) {
			this.contentParameters.add(para);
		}
	}
	
	public void addAttachment(EmailAttachment attach) {
		if(!attachments.contains(attach)) {
			attachments.add(attach);
		} else {
			throw new InternalBugException("Add dupliated attachment: " + attach);
		}
	}
	
}
