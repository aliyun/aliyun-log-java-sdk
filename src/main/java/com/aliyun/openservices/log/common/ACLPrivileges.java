package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.aliyun.openservices.log.common.Consts.ACLPrivilege;
import com.aliyun.openservices.log.exception.LogException;

/**
 * The acl privilege 
 * @author log-service-dev
 *
 */
public class ACLPrivileges implements Serializable {
	private static final long serialVersionUID = 6030989355388970839L;
	private List<ACLPrivilege> privileges = new ArrayList<ACLPrivilege>();

	public ACLPrivileges() {
	}

	/**
	 * construct from privilege list
	 * 
	 * @param privileges
	 *            the privilege list
	 */
	public ACLPrivileges(List<ACLPrivilege> privileges) {
		SetPrivileges(privileges);
	}

	/**
	 * construct from another privilege list
	 * 
	 * @param other
	 *            another privliege list
	 */
	public ACLPrivileges(ACLPrivileges other) {
		SetPrivileges(other.GetPrivileges());
	}

	/**
	 * Add a privilege
	 * 
	 * @param privilege
	 *            the privilege to add
	 */
	public void AddPrivilege(ACLPrivilege privilege) {
		boolean flag = true;
		for (ACLPrivilege curPrivilege : privileges) {
			if (curPrivilege.equals(privilege)) {
				flag = false;
			}
		}

		if (flag) {
			privileges.add(privilege);
		}
	}

	/**
	 * Add privilege list
	 * 
	 * @param privileges
	 *            the privilege list to add
	 */
	public void AddPrivileges(List<ACLPrivilege> privileges) {
		for (ACLPrivilege privilege : privileges) {
			AddPrivilege(privilege);
		}
	}

	/**
	 * set privilege list
	 * 
	 * @param privileges
	 *            the privilege to set
	 */
	public void SetPrivileges(List<ACLPrivilege> privileges) {
		this.privileges = new ArrayList<ACLPrivilege>();
		AddPrivileges(privileges);
	}

	/**
	 * Return the privilege list
	 * 
	 * @return the privilege list
	 */
	public List<ACLPrivilege> GetPrivileges() {
		return privileges;
	}

	/**
	 * return privilege in json array
	 * 
	 * @return privilege in json array
	 */
	public JSONArray ToJsonArray() {
		JSONArray privilegesArray = new JSONArray();
		for (ACLPrivilege privilege : privileges) {
			privilegesArray.add(privilege.toString());
		}

		return privilegesArray;
	}

	/**
	 * return privilege in json string
	 * 
	 * @return privilege in json string
	 */
	public String ToJsonString() {
		return ToJsonArray().toString();
	}

	/**
	 * Set privilege from json array
	 * 
	 * @param privilegesArray
	 *            the privilege json array
	 * @throws LogException
	 *             throw exception is any error happen
	 */
	public void FromJsonArray(JSONArray privilegesArray) throws LogException {
		try {
			this.privileges = new ArrayList<ACLPrivilege>();
			for (int i = 0; i < privilegesArray.size(); i++) {
				if (privilegesArray.getString(i).equals("READ")) {
					AddPrivilege(ACLPrivilege.READ);
				} else if (privilegesArray.getString(i).equals("WRITE")) {
					AddPrivilege(ACLPrivilege.WRITE);
				}
				if (privilegesArray.getString(i).equals("LIST")) {
					AddPrivilege(ACLPrivilege.LIST);
				} else if (privilegesArray.getString(i).equals("ADMIN")) {
					AddPrivilege(ACLPrivilege.ADMIN);
				}
			}
		} catch (JSONException e) {
			throw new LogException("FailToGenerateACLPrivileges",
					e.getMessage(), e, "");
		}
	}
	/**
	 * Set privilege from json array string
	 * 
	 * @param privilegesStr
	 *            the privilege json array string
	 * @throws LogException
	 *             throw exception is any error happen
	 */
	public void FromJsonString(String privilegesStr) throws LogException {
		try {
			JSONArray privilegesArray = JSONArray.parseArray(privilegesStr);
			FromJsonArray(privilegesArray);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateACLPrivileges",
					e.getMessage(), e, "");
		}
	}
}
