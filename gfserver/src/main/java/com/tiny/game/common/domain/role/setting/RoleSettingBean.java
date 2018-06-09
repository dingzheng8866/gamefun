package com.tiny.game.common.domain.role.setting;

public class RoleSettingBean {

	private boolean openMusic = true;
	private boolean openSfx = true; // special effects cinematography；特技效果 
	private LanguageType languageType = LanguageType.SimpleCN;
	
	public boolean isOpenMusic() {
		return openMusic;
	}
	public void setOpenMusic(boolean openMusic) {
		this.openMusic = openMusic;
	}
	public boolean isOpenSfx() {
		return openSfx;
	}
	public void setOpenSfx(boolean openSfx) {
		this.openSfx = openSfx;
	}
	public LanguageType getLanguageType() {
		return languageType;
	}
	public void setLanguageType(LanguageType languageType) {
		this.languageType = languageType;
	}
	
}
