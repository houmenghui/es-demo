package com.esdemo.frame.enums.fmc;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-11-26 17:15
 */
@Data
public class FmConfig003 {

    private List<Data> teams;

    public boolean contain(String teamId, String teamEntryId) {
        if (CollectionUtils.isEmpty(teams)) {
            return false;
        }
        for (FmConfig003.Data item : teams) {
            String paramTeam = String.format("%s_%s", Objects.toString(teamId, ""), Objects.toString(teamEntryId, ""));
            String itemTeam = item == null ? "" : String.format("%s_%s", Objects.toString(item.getTeamId(), ""), Objects.toString(item.getTeamEntryId(), ""));
            if (StringUtils.equalsIgnoreCase(paramTeam, itemTeam)) {
                return true;
            }
        }
        return false;
    }

    @lombok.Data
    public static class Data {
        private String teamId;
        private String teamEntryId;
    }

}
