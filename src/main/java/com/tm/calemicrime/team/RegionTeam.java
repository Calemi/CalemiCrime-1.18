package com.tm.calemicrime.team;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.util.RegionTeamHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RegionTeam {

    private final UUID id;
    private String name;
    private final List<RegionTeamMember> members;
    private final List<RegionTeamPlayer> invited = new ArrayList<>();
    private final List<RegionTeamPlayer> allies = new ArrayList<>();
    private List<RentAcceptorPos> ownedRentAcceptorPositions = new ArrayList<>();

    private RegionTeam(String name, List<RegionTeamMember> members) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.members = members;
    }

    public static RegionTeam create(String name, Player admin) {

        RegionTeam team = new RegionTeam(name, new ArrayList<>());

        RegionTeamsFile.teams.add(team);

        RegionTeamMember member = team.addMember(admin);

        if (member != null) {
            member.setOwnerStatus(true);
        }

        RegionTeamsFile.save();

        return team;
    }

    public void delete() {
        RegionTeamsFile.teams.remove(this);
        RegionTeamsFile.save();
    }

    public RegionTeamMember addMember(Player player) {

        if (RegionTeamHelper.hasTeam(player)) {
            return null;
        }

        RegionTeamMember member = new RegionTeamMember(player);
        members.add(member);

        uninvite(player.getName().getString());
        disally(player.getName().getString());

        RegionTeamsFile.save();

        return member;
    }

    public void removeMember(UUID id) {

        RegionTeamMember memberToRemove = null;

        for (RegionTeamMember member : getMembers()) {

            if (member.getID().equals(id)) {
                memberToRemove = member;
                break;
            }
        }

        members.remove(memberToRemove);

        boolean foundOwner = false;

        for (RegionTeamMember member : getMembers()) {

            if (member.isOwner()) {
                foundOwner = true;
                break;
            }
        }

        if (!foundOwner && members.size() > 0) {
            members.get(0).setOwnerStatus(true);
        }

        RegionTeamsFile.save();
    }

    public void removeMember(Player player) {
        removeMember(player.getUUID());
    }

    public boolean isMember(Player player) {

        for (RegionTeamMember member : getMembers()) {

            if (member.getID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public boolean isOwner(Player player) {

        for (RegionTeamMember member : getMembers()) {

            if (member.getID().equals(player.getUUID())) {

                if (member.isOwner()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void invite(Player player) {
        getInvited().add(new RegionTeamPlayer(player));
        RegionTeamsFile.save();
    }

    public void uninvite(String playerName) {

        for (RegionTeamPlayer teamPlayer : getInvited()) {

            if (teamPlayer.getName().equals(playerName)) {
                getInvited().remove(teamPlayer);
                break;
            }
        }

        RegionTeamsFile.save();
    }

    public boolean isInvited(Player player) {

        for (RegionTeamPlayer teamPlayer : getInvited()) {

            if (teamPlayer.getID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public void ally(Player player) {
        getAllies().add(new RegionTeamPlayer(player));
        RegionTeamsFile.save();
    }

    public void disally(String playerName) {

        for (RegionTeamPlayer teamPlayer : getAllies()) {

            if (teamPlayer.getName().equals(playerName)) {
                getAllies().remove(teamPlayer);
                break;
            }
        }

        RegionTeamsFile.save();
    }

    public boolean isAlly(Player player) {

        for (RegionTeamPlayer ally : getAllies()) {

            if (ally.getID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlly(String playerName) {

        for (RegionTeamPlayer ally : getAllies()) {

            if (ally.getName().equals(playerName)) {
                return true;
            }
        }

        return false;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        RegionTeamsFile.save();
    }

    public List<RegionTeamMember> getMembers() {
        return members;
    }

    public List<RegionTeamPlayer> getInvited() {
        return invited;
    }

    public List<RegionTeamPlayer> getAllies() {
        return allies;
    }

    public List<RentAcceptorPos> getOwnedRentAcceptorPositions() {

        if (ownedRentAcceptorPositions == null) {
            ownedRentAcceptorPositions = new ArrayList<>();
        }

        return ownedRentAcceptorPositions;
    }

    public void addRentAcceptorPosition(BlockEntityRentAcceptor rentAcceptor) {

        for (RentAcceptorPos pos : getOwnedRentAcceptorPositions()) {

            if (pos.x == rentAcceptor.getLocation().x && pos.y == rentAcceptor.getLocation().y && pos.z == rentAcceptor.getLocation().z) {

                if (!pos.type.equalsIgnoreCase(rentAcceptor.rentType)) {
                    pos.setType(rentAcceptor.rentType);
                    RegionTeamsFile.save();
                }

                return;
            }
        }

        getOwnedRentAcceptorPositions().add(RentAcceptorPos.fromLocation(rentAcceptor.rentType, rentAcceptor.getLocation()));
        RegionTeamsFile.save();
    }

    public void removeRentAcceptorPosition(Location location) {

        for (RentAcceptorPos pos : getOwnedRentAcceptorPositions()) {

            if (pos.x == location.x && pos.y == location.y && pos.z == location.z) {
                ownedRentAcceptorPositions.remove(pos);
                RegionTeamsFile.save();
                return;
            }
        }
    }

    public int getRentAcceptorCountOfType(Level level, String rentAcceptorType) {

        int count = 0;

        for (RentAcceptorPos pos : getOwnedRentAcceptorPositions()) {

            if (rentAcceptorType.equalsIgnoreCase(pos.type)) {
                count++;
            }
        }

        return count;
    }

    public static class RentAcceptorPos {

        private String type;
        private final int x;
        private final int y;
        private final int z;

        private RentAcceptorPos(String type, int x, int y, int z) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static RentAcceptorPos fromLocation(String type, Location location) {
            return new RentAcceptorPos(type, location.x, location.y, location.z);
        }

        public Location getLocation(Level level) {
            return new Location(level, x, y, z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RentAcceptorPos pos = (RentAcceptorPos) o;
            return type.equalsIgnoreCase(pos.type) && x == pos.x && y == pos.y && z == pos.z;
        }
    }
}
