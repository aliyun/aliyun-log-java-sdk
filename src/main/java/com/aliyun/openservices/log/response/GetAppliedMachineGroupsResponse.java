
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAppliedMachineGroupsResponse extends Response {

    private static final long serialVersionUID = -9132526915584919104L;

    private List<String> machineGroups;

    public GetAppliedMachineGroupsResponse(Map<String, String> headers, List<String> group) {
        super(headers);
        SetMachineGroups(group);
    }

    public List<String> GetMachineGroups() {
        return machineGroups;
    }

    public void SetMachineGroups(List<String> machineGroups) {
        this.machineGroups = new ArrayList<String>(machineGroups);
    }

    public int GetTotal() {
        return machineGroups.size();
    }

}
