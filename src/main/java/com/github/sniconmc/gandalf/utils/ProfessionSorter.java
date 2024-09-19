package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.gandalf.config.GandalfProfession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for sorting GandalfProfession objects based on their associated file names.
 * The sorting is done based on a numeric prefix in the file name.
 */
public class ProfessionSorter {
    /**
     * Pattern to match file names in the format "number name".
     * The number is used for sorting.
     */
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("(\\d+)\\s+(.+)");

    /**
     * Sorts the given map of professions based on their file names.
     *
     * @param professionMap A map where keys are file names (without .json extension) and values are GandalfProfession objects.
     * @return A sorted list of GandalfProfession objects.
     */
    public static List<GandalfProfession> sortProfessions(Map<String, GandalfProfession> professionMap) {
        List<Map.Entry<String, GandalfProfession>> entries = new ArrayList<>(professionMap.entrySet());
        entries.sort(Comparator.comparingInt(ProfessionSorter::getOrderFromFileName));

        List<GandalfProfession> sortedProfessions = new ArrayList<>();
        for (Map.Entry<String, GandalfProfession> entry : entries) {
            sortedProfessions.add(entry.getValue());
        }

        return sortedProfessions;
    }

    /**
     * Extracts the numeric order from the file name.
     *
     * @param entry A map entry where the key is the file name (without .json extension) and the value is a GandalfProfession object.
     * @return The numeric order extracted from the file name, or Integer.MAX_VALUE if the file name doesn't match the expected pattern.
     */
    private static int getOrderFromFileName(Map.Entry<String, GandalfProfession> entry) {
        String fileName = entry.getKey(); // This is now without .json extension
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return Integer.MAX_VALUE; // Place professions with non-matching file names at the end
    }
}