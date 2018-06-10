package com.tiny.game.common.conf.role;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.role.RoleExp;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;

@ConfAnnotation(confClass = RoleExpConfReader.class, path = "resources/config/role_exp.csv")
public class RoleExpConfReader extends ConfReader<RoleExp> {
	
	@Override
	protected void parseCsv(String[] csv) {
		RoleExp bean = new RoleExp();
		bean.setLevel(Integer.parseInt(getSafeValue(csv, "level")));
		bean.setExp(Integer.parseInt(getSafeValue(csv, "exp")));
		addConfBean(bean.getKey(), bean);
		RoleService.addMaxConfigRoleLevel(bean.getLevel());
	}
	
}
