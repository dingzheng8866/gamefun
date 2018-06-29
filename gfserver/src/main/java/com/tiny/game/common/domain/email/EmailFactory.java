package com.tiny.game.common.domain.email;

import java.util.Calendar;
import java.util.List;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.util.IdGenerator;

public class EmailFactory {

	public static Email buildEmail(String toRoleId, int fromGroupTypeId, String fromRoleId,int titleId,int contentId, List<EmailAttachment> attachments, String... contentParas) {
		Email email = new Email();
		email.setEmailId(IdGenerator.genUniqueId(20));
		email.setToRoleId(toRoleId);
		email.setFromGroupTypeId(fromGroupTypeId);
		email.setFromRoleId(fromRoleId);
		email.setTitleId(titleId);
		email.setContentId(contentId);
		if(attachments!=null && attachments.size() >0) {
			email.setAttachments(attachments);
		}
		if(contentParas!=null && contentParas.length > 0) {
			for(String str : contentParas) {
				if(str!=null && str.length() >0) {
					email.addContentParameter(str);
				}
			}
		}
		email.setLastUpdateTime(Calendar.getInstance().getTime());
		return email;
	}
	
	public static Email buildApplyFriendEmail(String toRoleId, String fromRoleId, String desc) {
		return buildEmail(toRoleId, GameConst.EMAIL_GROUP_FRIEND_APPLY, fromRoleId, GameConst.EMAIL_TITLE_ID_ASK_AS_FRIEND, GameConst.EMAIL_CONTENT_ID_ASK_AS_FRIEND, null, desc);
	}
	
	
	
	
}
