package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.common.Consts.ACLAction;
import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;

/**
 * The acl config used to grant/revoke project/logstore aclprivileges to/from a principle
 * @author log-service-dev
 *
 */
public class ACL implements Serializable {

	private static final long serialVersionUID = -4828970701559782547L;
	protected ACLAction action;
	protected String principle = "";
	protected ACLPrivileges privilege = new ACLPrivileges();
	protected int lastModifyTime = -1;
	protected int createTime = -1;

	public ACL() {
	}

	/**
	 * construct a ACL
	 * 
	 * @param principle
	 *            the aliyun_id or "ANONYMOUS"
	 * @param privilege
	 *            privileges to grant or revoke
	 * @param action
	 *            ACLAction.GRANT or ACLAction.REVOKE
	 */
	public ACL(String principle, ACLPrivileges privilege, ACLAction action) {
		this.principle = principle;
		this.privilege = new ACLPrivileges(privilege);
		this.action = action;
	}

	/**
	 * construct with an acl
	 * 
	 * @param acl
	 *            the exist acl
	 */
	public ACL(ACL acl) {
		this.action = acl.GetAction();
		this.principle = acl.GetPrinciple();
		SetPrivilege(acl.GetPrivilege());
		this.lastModifyTime = acl.GetLastModifyTime();
		this.createTime = acl.GetCreateTime();
	}

	/**
	 * @return the action
	 */
	public ACLAction GetAction() {
		return action;
	}

	/**
	 * @return the principle
	 */
	public String GetPrinciple() {
		return principle;
	}

	/**
	 * @return the privilege
	 */
	public ACLPrivileges GetPrivilege() {
		return privilege;
	}

	/**
	 * @return the lastModifyTime
	 */
	public int GetLastModifyTime() {
		return lastModifyTime;
	}

	/**
	 * @return the createTime
	 */
	public int GetCreateTime() {
		return createTime;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void SetAction(ACLAction action) {
		this.action = action;
	}

	/**
	 * @param principle
	 *            the principle to set
	 */
	public void SetPrinciple(String principle) {
		this.principle = principle;
	}

	/**
	 * @param privileges
	 *            the privileges to set
	 */
	public void SetPrivilege(ACLPrivileges privileges) {
		this.privilege = new ACLPrivileges(privileges);
	}

	/**
	 * @param lastModifyTime
	 *            the lastModifyTime to set
	 */
	public void SetLastModifyTime(int lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void SetCreateTime(int createTime) {
		this.createTime = createTime;
	}

	/**
	 * Get request json
	 * 
	 * @return request json
	 * @throws LogException
	 *             if any error happened
	 */
	public JSONObject ToRequestJson() throws LogException {
		if (GetPrivilege().ToJsonArray().size() == 0) {
			throw new LogException("BadResponse",
					"ACL privilege must have at least one value", "");
		}

		JSONObject aclDict = new JSONObject();
		aclDict.put("action", GetAction().toString());
		aclDict.put("principle", GetPrinciple());
		aclDict.put("privilege", GetPrivilege().ToJsonArray());

		return aclDict;
	}

	/**
	 * return request json string
	 * 
	 * @return request json string
	 * @throws LogException
	 *             if any error happened
	 */
	public String ToRequestString() throws LogException {
		return ToRequestJson().toString();
	}

	/**
	 * return Json object of acl
	 * 
	 * @return return json object
	 * @throws LogException
	 *             if any error happened
	 */
	public JSONObject ToJsonObject() throws LogException {
		if (GetPrivilege().ToJsonArray().size() == 0) {
			throw new LogException("BadResponse",
					"ACL privilege must have at least one value", "");
		}

		JSONObject aclDict = new JSONObject();
		aclDict.put("principle", GetPrinciple());
		aclDict.put("privilege", GetPrivilege().ToJsonArray());
		aclDict.put("createTime", GetCreateTime());
		aclDict.put("lastModifyTime", GetLastModifyTime());

		return aclDict;
	}

	/**
	 * return json object string
	 * 
	 * @return json object string
	 * @throws LogException
	 *             if any error happened
	 */
	public String ToJsonString() throws LogException {
		return ToJsonObject().toString();
	}

	/**
	 * parse from a json object
	 * 
	 * @param dict
	 *            the acl json object
	 * @throws LogException
	 *             if any error happened
	 */
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			String principle = dict.getString("principle");

			JSONArray privilege = dict.getJSONArray("privilege");
			ACLPrivileges privileges = new ACLPrivileges();
			privileges.FromJsonArray(privilege);

			SetPrinciple(principle);
			SetPrivilege(privileges);

			if (dict.containsKey("action")) {
				String action_value = dict.getString("action");
				if (action_value.toLowerCase().equals("grant")) {
					SetAction(ACLAction.GRANT);
				} else {
					SetAction(ACLAction.REVOKE);
				}

			}

			if (dict.containsKey("createTime")) {
				createTime = dict.getInt("createTime");
			}

			if (dict.containsKey("lastModifyTime")) {
				lastModifyTime = dict.getInt("lastModifyTime");
			}

		} catch (JSONException e) {
			throw new LogException("FailToGenerateACL", e.getMessage(), e, "");
		}
	}

	/**
	 * pase from json string
	 * 
	 * @param aclString
	 *            acl json string
	 * @throws LogException
	 *             if any error happened
	 */
	public void FromJsonString(String aclString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(aclString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateACL", e.getMessage(), e, "");
		}
	}
}
