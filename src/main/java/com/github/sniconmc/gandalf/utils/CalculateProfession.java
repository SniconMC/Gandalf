package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.container.config.ContainerItem;
import com.github.sniconmc.container.config.ContainerItemData;
import com.github.sniconmc.container.config.ContainerItemDisplay;
import com.github.sniconmc.utils.placeholder.PlaceholderManager;
import com.github.sniconmc.utils.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minestom.server.entity.Player;
import com.github.sniconmc.gandalf.GandalfManager;
import com.github.sniconmc.gandalf.config.GandalfProfession;
import com.github.sniconmc.gandalf.config.GandalfProfile;

import java.util.*;

public class CalculateProfession {

    public static void updateProfession(Player player) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        Double totalXP = profile.getProfession_total_xp();
        List<GandalfProfession> professions = GandalfManager.getAllProfessions();

        double cumulativeXP = 0; // Keep track of cumulative XP for professions
        String highestUnlockedProfessionId = profile.getProfession(); // Start with the current profession in the profile

        // Determine the highest unlocked profession based on total XP
        for (GandalfProfession profession : professions) {
            cumulativeXP += profession.getXpRequired(); // Assuming getXpRequired() returns the XP needed for the profession

            if (totalXP >= cumulativeXP) {
                highestUnlockedProfessionId = profession.getProfession_id(); // Update highest unlocked profession
            }
        }

        // Check if we need to update the profile and reload the GUI
        if (!profile.getProfession().equals(highestUnlockedProfessionId)) {
            profile.setOldProfession(profile.getProfession());
            profile.setProfession(highestUnlockedProfessionId); // Update the profile with the new highest profession
            updateProfessionGUI(player, profile); // Update the GUI with the new profession

            String oldProfessionStyle = GandalfManager.getProfession(profile.getOldProfession()).getProfession_style_sidebar();
            String professionStyle = GandalfManager.getProfession(profile.getProfession()).getProfession_style_sidebar();

            String oldProfessionStyleIcon = GandalfManager.getProfession(profile.getOldProfession()).getProfession_icon_style();
            String professionStyleIcon = GandalfManager.getProfession(profile.getProfession()).getProfession_icon_style();

            player.sendMessage(TextUtils.convertStringToComponent(
                    "<strikethrough><gray>                                                                                 </gray></strikethrough>\n" +
                            "                                 <bold><gradient:#ffff1c:gold>LEVEL UP!</gradient></bold>\n" +
                            "                        <yellow>You have advanced from</yellow>\n" +
                            "                                   " + oldProfessionStyleIcon + " <gray>" + oldProfessionStyle + "</gray>\n" +
                            "                                        <yellow>to</yellow>\n" +
                            "                                   " + professionStyleIcon + " <gray>" + professionStyle + "</gray>\n" +
                            "<strikethrough><gray>                                                                                 </gray></strikethrough>"
            ));

        }
    }

    public static void updateProfessionGUI(Player player, GandalfProfile profile) {
        Map<String, String> placeholders = new HashMap<>();
        List<GandalfProfession> professions = GandalfManager.getAllProfessions();
        Double totalXP = profile.getProfession_total_xp();

        // StringBuilder to hold all JSON items
        StringBuilder jsonItemsBuilder = new StringBuilder();

        // Create a Gson instance for pretty-printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        int i = 0;
        double cumulativeXP = 0; // Keep track of cumulative XP for professions

        List<List<String>> progession = new ArrayList<>();

        for (GandalfProfession profession : professions) {
            i++;
            cumulativeXP += profession.getXpRequired(); // Assuming getXpRequired() returns the XP needed for the profession

            ContainerItem item = new ContainerItem(0, "", 1);

            // Set slot based on index
            if (i > 7) {
                item.setSlot(i + 20);
            } else {
                item.setSlot(i + 18);
            }

            // Display settings for the item
            ContainerItemDisplay display = new ContainerItemDisplay(
                    List.of("» " + profession.getProfession_style() + " «"),
                    new ArrayList<>(List.of(List.of(""),
                        List.of("Cool thing"),
                        List.of(""),
                        List.of("→ <aqua>Click for rewards</aqua> ←"),
                        List.of("- 1x <dark_aqua>Perk"),
                        List.of("- 1x <light_purple>Mythic"),
                        List.of("- 7x <gold>Legendary"),
                        List.of("- 3x <dark_purple>Epic"),
                        List.of("- 5x <blue>Rare"),
                        List.of("- 1x <green>Uncommon"),
                        List.of("- 10x <white>Common"),
                        List.of("")

                    )),
                    false, "", true
            );

            // Determine glass pane color based on XP
            if (totalXP >= cumulativeXP) {
                item.setId("minecraft:green_stained_glass_pane"); // Unlocked
                display.getLore().add(List.of("<gray>Progress to " + profession.getProfession_icon_style() + " " + profession.getProfession_style_sidebar() + ":</gray>"));
                display.getLore().add(List.of(getProgressBar(profile ,totalXP, cumulativeXP,  profession.getXpRequired())));
                display.getLore().add(List.of("<green><bold>Unlocked</bold></green>"));


            } else if (i > 1 && totalXP >= cumulativeXP - profession.getXpRequired()) {
                item.setId("minecraft:orange_stained_glass_pane"); // Below this level
                display.getLore().add(List.of("<gray>Progress to " + profession.getProfession_icon_style() + " " + profession.getProfession_style_sidebar() + ":</gray>"));
                display.getLore().add(List.of(getProgressBar(profile ,totalXP, cumulativeXP,  profession.getXpRequired())));
                display.getLore().add(List.of("<red><bold>Locked</bold></red>"));
            } else {
                item.setId("minecraft:red_stained_glass_pane"); // Locked
                display.getLore().add(List.of("<gray>Progress to " + profession.getProfession_icon_style() + " " + profession.getProfession_style_sidebar() + ":</gray>"));
                display.getLore().add(List.of(getProgressBar(profile ,totalXP, cumulativeXP,  profession.getXpRequired())));
                display.getLore().add(List.of("<red><bold>Locked</bold></red>"));
            }

            ContainerItemData data = new ContainerItemData("", "", false);
            item.setDisplay(display);
            item.setData(data);

            // Convert the item to a JsonElement
            JsonElement jsonElement = JsonParser.parseString(gson.toJson(item));

            // Pretty-print the JSON
            String prettyJson = gson.toJson(jsonElement);

            // Append the pretty-printed JSON to the builder
            if (i > 1) {
                jsonItemsBuilder.append(",\n");
            }
            jsonItemsBuilder.append(prettyJson);

            if (Objects.equals(profile.getProfession(), profession.getProfession_id())) {
                progession.add(List.of("\"<green> →  </green>" + profession.getProfession_icon_style() + " " + profession.getProfession_style() + "\""));
            } else {
                progession.add(List.of("\"     " + profession.getProfession_icon_style() + " " + profession.getProfession_style() + "\""));
            }

        }

        // Add the JSON objects to the placeholders
        placeholders.put("profession_gui_items", jsonItemsBuilder.toString());
        placeholders.put("profession_progression_order", progession.toString().substring(1, progession.toString().length() - 1));

        double maxXP = professions.stream().mapToDouble(GandalfProfession::getXpRequired).sum(); // Total XP required for all professions

        // Use getProgressBar to calculate the progress towards max profession
        String progressBar = getProgressBar(profile,totalXP, maxXP, maxXP);

        // Set the placeholder with the progress bar
        placeholders.put("max_profession_xp", String.valueOf(maxXP));
        placeholders.put("player_percentage_to_max", progressBar);
        PlaceholderManager.addPlaceholdersToPlayer(player, placeholders);

        ProfileUtils.update(player, profile);
    }

    public static String getProgressBar(GandalfProfile profile, double totalXP, double cumulativeXP, double xpRequired) {
        // Calculate progress percentage based on current profession
        double progress = (totalXP - (cumulativeXP - xpRequired)) / xpRequired;

        // Ensure progress is not negative
        progress = Math.max(progress, 0);

        // Allow progress to exceed 1 (i.e., allow more than 100%)
        double progressPercent = progress * 100;

        // Format the progress percentage to one decimal place
        String formattedProgressPercent = String.format("%.1f", progressPercent);

        // Create a progress bar with 28 characters total length
        int totalBarLength = 28;
        int filledBarLength = Math.min((int) (progress * totalBarLength), totalBarLength);
        int emptyBarLength = totalBarLength - filledBarLength;

        // Choose bar color based on progress percentage
        String barColor = progressPercent >= 100 ? "<green>" : "<blue>";

        // Build the progress bar string
        StringBuilder progressBar = new StringBuilder();
        progressBar.append(barColor).append("<st>");
        for (int i = 0; i < filledBarLength; i++) {
            progressBar.append(" ");
        }
        progressBar.append("</st>").append(barColor.replace("<", "</")); // Closing tag for color

        progressBar.append("<white>").append("<st>");
        for (int i = 0; i < emptyBarLength; i++) {
            progressBar.append(" ");
        }
        progressBar.append("</st></white>");

        // Calculate overflow XP only if the totalXP is greater than or equal to cumulativeXP (meaning profession is unlocked)
        boolean isUnlocked = totalXP >= cumulativeXP; // Check if the profession is unlocked
        double xpProgress = Math.max(0, totalXP - (cumulativeXP - xpRequired));

        // Calculate the overflow XP only for unlocked professions
        if (isUnlocked) {
            double overflowXP = totalXP - xpRequired;
            if (overflowXP > 0) {
                // If overflow XP exists, show it in number format or percentage format based on settings
                if (profile.getSettings().isProfession_number_format()) {
                    return progressBar + " <yellow>" + xpRequired + " / </yellow><gold>" + xpRequired + "</gold> " + formatWithUnitPrefix(overflowXP);
                } else {
                    return progressBar + " <yellow>" + formattedProgressPercent + "</yellow><gold>%</gold> " + formatWithUnitPrefix(overflowXP);
                }
            } else {
                // If no overflow, show regular progress
                if (profile.getSettings().isProfession_number_format()) {
                    return progressBar + " <yellow>" + xpProgress + " / </yellow><gold>" + xpRequired + "</gold>";
                } else {
                    return progressBar + " <yellow>" + formattedProgressPercent + "</yellow><gold>%</gold>";
                }
            }
        } else {
            // If profession isn't unlocked, just show progress without overflow
            if (profile.getSettings().isProfession_number_format()) {
                return progressBar + " <yellow>" + xpProgress + " / </yellow><gold>" + xpRequired + "</gold>";
            } else {
                return progressBar + " <yellow>" + formattedProgressPercent + "</yellow><gold>%</gold>";
            }
        }
    }



    // Helper method to format large numbers with unit prefixes
    public static String formatWithUnitPrefix(double number) {
        String[] units = { "", "k", "M", "B", "T", "Q", "∞"};
        int unitIndex = 0;

        // Scale the number down to an appropriate unit
        while (Math.abs(number) >= 1000 && unitIndex < units.length - 1) {
            number /= 1000.0;
            unitIndex++;
        }

        // Format the number with one decimal place and the appropriate unit
        return String.format("(+%.1f%s XP)", number, units[unitIndex]);
    }

}
