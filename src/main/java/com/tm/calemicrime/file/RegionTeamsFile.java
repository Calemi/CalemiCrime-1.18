package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.team.RegionTeamMember;
import com.tm.calemicrime.util.FileHelper;

import java.util.*;

public class RegionTeamsFile {

    public static ArrayList<RegionTeam> teams;

    public static void init() {
        teams = FileHelper.readFileOrCreate("RegionTeams", new ArrayList<>(), new TypeToken<ArrayList<RegionTeam>>(){});
    }

    public static void save() {

        List<RegionTeam> teamsToRemove = new ArrayList<>();

        for (RegionTeam team : teams) {

            if (team.getMembers().isEmpty()) {
                teamsToRemove.add(team);
            }
        }

        teams.removeAll(teamsToRemove);

        FileHelper.saveToFile("RegionTeams", teams);
    }
}