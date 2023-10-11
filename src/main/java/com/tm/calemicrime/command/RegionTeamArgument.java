package com.tm.calemicrime.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.util.RegionTeamHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RegionTeamArgument implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");

    private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType((p_112095_) -> {
        return new TranslatableComponent("team.notFound", p_112095_);
    });

    public static RegionTeamArgument team() {
        return new RegionTeamArgument();
    }

    public static RegionTeam getTeam(CommandContext<CommandSourceStack> pContext, String pName) throws CommandSyntaxException {

        String teamName = pContext.getArgument(pName, String.class);

        RegionTeam team = RegionTeamHelper.getTeam(teamName);

        if (team != null) {
            return team;
        }

        throw ERROR_TEAM_NOT_FOUND.create(teamName);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {

        Collection<String> allTeams = new ArrayList<>();

        for (RegionTeam team : RegionTeamsFile.teams) {
            allTeams.add(team.getName());
        }

        return pContext.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(allTeams, pBuilder) : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
