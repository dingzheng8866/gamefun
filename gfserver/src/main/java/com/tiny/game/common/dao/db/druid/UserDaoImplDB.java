package com.tiny.game.common.dao.db.druid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.tiny.game.common.dao.UserDao;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.exception.InternalRuntimeException;

import game.protocol.protobuf.GameProtocol.S_RoleData;


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
	private RoleRSH roleResultSetHandler = new RoleRSH();
	
	private static class UserAcctBindInfoRSH extends AbstractResultSetHandler<UserAcctBindInfo> {
		public UserAcctBindInfo factoryBeanObject(ResultSet rs) throws SQLException {
			UserAcctBindInfo bean = new UserAcctBindInfo();
			bean.setUserId(rs.getString("userId"));
			bean.setBindedAccountId(rs.getString("bindId"));
			bean.setLastUpdateTime(rs.getDate("lastUpdateTime"));
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
	
	private static class RoleRSH extends AbstractResultSetHandler<Role> {
		public Role factoryBeanObject(ResultSet rs) throws SQLException {
			S_RoleData.Builder roleData = S_RoleData.newBuilder();
			try {
				roleData.mergeFrom(rs.getBinaryStream("roleData"));
			} catch (IOException e) {
				throw new InternalRuntimeException("Failed to factoryBeanObject, error: "+e.getMessage(), e);
			}
			Role bean = Role.toRole(roleData.build().toByteArray());
			
			bean.setRoleId(rs.getString("roleId"));
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
			runner.update("insert into user_bind(userId, bindId, lastUpdateTime) values(?, ?, ?)",
					info.getUserId(), info.getBindedAccountId(), info.getLastUpdateTime());
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
			runner.update("insert into user_online(userId, loginServerId, lastUpdateTime) values(?, ?, ?)",
					info.getUserId(), info.getLoginServerId(), info.getLastUpdateTime());
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

	private void updateRoleData(Role role) {
		InputStream inputStream = new ByteArrayInputStream(role.toBinData());
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement pstmt = con.prepareStatement("update role set roleData = ?, lastUpdateTime=? where roleId=?");
			pstmt.setBlob(1, inputStream);
			pstmt.setDate(2, new java.sql.Date(role.getLastUpdateTime().getTime()));
			pstmt.setString(3, role.getRoleId());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to updateRoleData: "+role.getRoleId()+", error: "+e.getMessage(), e);
		} finally {
			if(con!=null) {
				DbUtils.closeQuietly(con);
			}
		}
	}	
	
	@Override
	public void createRole(Role role) {
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			runner.update("insert into role(roleId, lastUpdateTime) values(?, ?)",
					role.getRoleId(), role.getLastUpdateTime());
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to createRole: "+role.toString()+", error: "+e.getMessage(), e);
		}
		updateRoleData(role);
	}

	@Override
	public void updateRole(Role role) {
//		QueryRunner runner = new QueryRunner(dataSource);
//		try {
//			runner.update("update role set roleData=?, lastUpdateTime=? where roleId=?",
//					role.toBinData(), role.getLastUpdateTime(), role.getRoleId());
//		} catch (SQLException e) {
//			throw new InternalRuntimeException("Failed to updateRole: "+role.toString()+", error: "+e.getMessage(), e);
//		}
		updateRoleData(role);
	}
	
	@Override
	public Role getRole(String roleId){
		Role ret = null;
		QueryRunner runner = new QueryRunner(dataSource);
		try {
			ret = runner.query("select * from role where roleId=?", roleResultSetHandler.singleHandler, roleId);
		} catch (SQLException e) {
			throw new InternalRuntimeException("Failed to getRole: "+roleId+", error: "+e.getMessage(), e);
		}
		return ret;
	}
	
	
}
