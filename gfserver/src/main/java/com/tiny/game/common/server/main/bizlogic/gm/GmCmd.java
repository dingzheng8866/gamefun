package com.tiny.game.common.server.main.bizlogic.gm;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.Role;

public abstract class GmCmd {

	protected static final Logger logger = LoggerFactory.getLogger(GmCmd.class);
	
	protected Role role;
	protected String cmd;
	protected Map<String, String> parameters = new HashMap<String, String>();

	public GmCmd(Role role, String cmd, String parameter) {
		this.role = role;
		this.cmd = cmd;
		
		if(parameter!=null && parameter.trim().length() > 0){
			String[] psa = parameter.trim().split(",");
			for(String p : psa){
				if(p!=null && p.length() > 0) {
					String[] sa = p.split("=");
					if(sa.length!=2){
						throw new InvalidParameterException("gm " + cmd + " parameter is not right(x=y,a=b): " + parameter);
					}
					parameters.put(sa[0].trim(), sa[1].trim());
				}
			}
		}
	}

	public Role getRole() {
		return role;
	}

	public String getCmd() {
		return cmd;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public String getParameterValue(String parameter) {
		return parameters.get(parameter);
	}

	public abstract void execute();

}
