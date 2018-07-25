package com.tiny.game.common.server.fight.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tiny.game.common.server.fight.domain.prop.Prop;
import com.tiny.game.common.server.fight.domain.prop.PropAttacher;

public class SceneObject {

	private Map<String, Prop> props = new HashMap<String, Prop>();
	private List<PropAttacher> attchProps = new ArrayList<PropAttacher>();
	
	public void addProp(Prop p) {
		props.put(p.getName(), p);
	}
	
}
