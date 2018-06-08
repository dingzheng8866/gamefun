package com.tiny.game.common.dao.db.druid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;


public abstract class AbstractResultSetHandler<T> {
	
	public abstract T factoryBeanObject(ResultSet rs) throws SQLException;
	
	protected ResultSetHandler<T> singleHandler = new ResultSetHandler<T>() {
		@Override
		public T handle(ResultSet rs) throws SQLException {
			T bean = null;
			if (rs.next()) {
				bean = factoryBeanObject(rs);
			}
			return bean;
		}
	};

	protected ResultSetHandler<List<T>> multipleUserHandler = new ResultSetHandler<List<T>>() {
		@Override
		public List<T> handle(ResultSet rs) throws SQLException {
			List<T> list = new ArrayList<T>();
			while (rs.next()) {
				list.add(factoryBeanObject(rs));
			}
			return list;
		}
	};
	
}
