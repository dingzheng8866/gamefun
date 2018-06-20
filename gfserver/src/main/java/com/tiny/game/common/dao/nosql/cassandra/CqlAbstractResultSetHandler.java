package com.tiny.game.common.dao.nosql.cassandra;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

public abstract class CqlAbstractResultSetHandler<T> {
	
	public abstract T factoryBeanObject(Row row) ;
	
	public T buildSingle(ResultSet rs) {
		T bean = null;
		Row row = rs.one();
		if (row!=null) {
			bean = factoryBeanObject(row);
		}
		return bean;
	}

	public List<T> buildMultiple(ResultSet rs) {
		List<T> list = new ArrayList<T>();
		for (Row row : rs) {
			list.add(factoryBeanObject(row));
		}
		return list;
	}
}