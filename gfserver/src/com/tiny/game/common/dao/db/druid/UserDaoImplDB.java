package com.tiny.game.common.dao.db.druid;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.tiny.game.common.dao.UserDao;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.exception.InternalRuntimeException;


public class UserDaoImplDB implements UserDao {

	private static final Logger logger = LoggerFactory.getLogger(UserDaoImplDB.class);
	
	private DruidDataSource dataSource = DruidManager.getInstance().getDataSource();
	
	private static class SingletonHolder {
		private static UserDaoImplDB instance = new UserDaoImplDB();
	}

	public static UserDaoImplDB getInstance() {
		return SingletonHolder.instance;
	}
	
	private UserAcctBindInfoRSH userAcctBindInfoResultSetHandler = new UserAcctBindInfoRSH();
	private UserRSH userResultSetHandler = new UserRSH();
	private UserOnlineInfoRSH userOnlineInfoResultSetHandler = new UserOnlineInfoRSH();
	
	private static class UserAcctBindInfoRSH extends AbstractResultSetHandler<UserAcctBindInfo> {
		public UserAcctBindInfo factoryBeanObject(ResultSet rs) throws SQLException {
			UserAcctBindInfo bean = new UserAcctBindInfo();
			bean.setUserId(rs.getString("userId"));
			bean.setBindedAccountId(rs.getString("bindId"));
			return bean;
		}
	}
	
	private static class UserRSH extends AbstractResultSetHandler<User> {
		public User factoryBeanObject(ResultSet rs) throws SQLException {
			User bean = new User();
			bean.setUserId(rs.getString("userId"));
			bean.setLoginAccountId(rs.getString("loginAccountId"));
			bean.setLoginDeviceId(rs.getString("loginDeviceId"));
			bean.setLoginIp(rs.getString("loginIp"));
			bean.setChannel(rs.getString("channel"));
			bean.setPlatform(rs.getString("platform"));
			bean.setPlatformAccountId(rs.getString("platformAccountId"));
			bean.setPlatformAccountPassword(rs.getString("platformAccountPassword"));
			bean.setCreateTime(rs.getDate("createTime"));
			bean.setLastUpdateTime(rs.getDate("lastUpdateTime"));
			bean.setLoginDeviceInfo(rs.getString("loginDeviceInfo"));
			return bean;
		}
	}
	
	private static class UserOnlineInfoRSH extends AbstractResultSetHandler<UserOnlineInfo> {
		public UserOnlineInfo factoryBeanObject(ResultSet rs) throws SQLException {
			UserOnlineInfo bean = new UserOnlineInfo();
			bean.setUserId(rs.getString("userId"));
			bean.setLoginServerId(rs.getString("loginServerId"));
			bean.setLastUpdateTime(rs.getDate("lastUpdateTime"));
			return bean;
		}
	}
	
	@Override
	public void createUser(User user) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("insert into user(userId, loginAccountId,loginDeviceId,loginIp,channel,platform,platformAccountId,platformAccountPassword,createTime, lastUpdateTime,loginDeviceInfo) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					user.getUserId(), user.getLoginAccountId(), user.getLoginDeviceId(), user.getLoginIp(), user.getChannel(), user.getPlatform(), user.getPlatformAccountId(),user.getPlatformAccountPassword(), user.getCreateTime(), user.getLastUpdateTime(), user.getLoginDeviceInfo());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to createUser: "+user.toString()+", error: "+e.getMessage(), e);
		}
	}

	@Override
	public void updateUser(User user) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("update user set loginAccountId=?, loginDeviceId=?, loginIp=?, channel=?, platform=?, platformAccountId=?, platformAccountPassword=?, lastUpdateTime=?, loginDeviceInfo=? where userId=?",
					user.getLoginAccountId(), user.getLoginDeviceId(), user.getLoginIp(), user.getChannel(), user.getPlatform(), user.getPlatformAccountId(),user.getPlatformAccountPassword(), user.getLastUpdateTime(), user.getLoginDeviceInfo(), user.getUserId());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to createUser: "+user.toString()+", error: "+e.getMessage(), e);
		}
	}

	@Override
	public User getUserById(String userId) {
		User ret = null;
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			ret = runner.query("select * from user where userId=?", userResultSetHandler.singleHandler, userId);
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to getUserById: "+userId+", error: "+e.getMessage(), e);
		}
		return ret;
	}

	@Override
	public UserAcctBindInfo getUserAcctBindInfo(String acctBindId) {
		UserAcctBindInfo ret = null;
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			ret = runner.query("select * from user_bind where bindId=?", userAcctBindInfoResultSetHandler.singleHandler, acctBindId);
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to getUserAcctBindInfo: "+acctBindId+", error: "+e.getMessage(), e);
		}
		return ret;
	}
	
	@Override
	public void createUserAcctBindInfo(UserAcctBindInfo info) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("insert into user_bind(userId, bindId, lastUpdateTime) values(?, ?, NOW())",
					info.getUserId(), info.getBindedAccountId());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to createUserAcctBindInfo: "+info.toString()+", error: "+e.getMessage(), e);
		}
	}

	@Override
	public void deleteUserAcctBindInfo(UserAcctBindInfo info) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("delete from user_bind where bindId=?", info.getBindedAccountId());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to deleteUserAcctBindInfo: "+info.toString()+", error: "+e.getMessage(), e);
		}
	}

	@Override
	public void createUserOnlineInfo(UserOnlineInfo info) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("insert into user_online(userId, loginServerId, lastUpdateTime) values(?, ?, NOW())",
					info.getUserId(), info.getLoginServerId());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to setUserOnlineInfo: "+info.toString()+", error: "+e.getMessage(), e);
		}
	}

	@Override
	public void updateUserOnlineInfo(UserOnlineInfo info) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("update user_online set loginServerId=?, lastUpdateTime=? where userId=?",
					info.getLoginServerId(), info.getLastUpdateTime(), info.getUserId());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to setUserOnlineInfo: "+info.toString()+", error: "+e.getMessage(), e);
		}
	}	
	
	@Override
	public void deleteUserOnlineInfo(String userId) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("delete from user_online where userId=?", userId);
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to deleteUserOnlineInfo: "+userId+", error: "+e.getMessage(), e);
		}
	}

	@Override
	public UserOnlineInfo getUserOnlineInfo(String userId) {
		UserOnlineInfo ret = null;
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			ret = runner.query("select * from user_online where userId=?", userOnlineInfoResultSetHandler.singleHandler, userId);
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to getUserOnlineInfo: "+userId+", error: "+e.getMessage(), e);
		}
		return ret;
	}

}
