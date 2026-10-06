package com.mittal.uniform.api.utility;

public class SearchUtils {

    /**
     * Cleans up text by converting to lowercase and stripping all whitespaces.
     * This ensures 'dps blazer' and 'dpsblazer' match perfectly.
     */
    public static String cleanString(String input) {
        if (input == null) return "";
        return input.toLowerCase().replaceAll("\\s+", "");
    }

    /**
     * Levenshtein Distance Algorithm: Calculates how many single-character edits
     * (insertions, deletions, substitutions) it takes to change one word into another.
     */
    public static int calculateLevenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }
        return dp[x.length()][y.length()];
    }

    /**
     * Determines if a search term is a close match to a target string, tolerating minor typos.
     */
    public static boolean isFuzzyMatch(String target, String searchTerm) {
        String cleanTarget = cleanString(target);
        String cleanSearch = cleanString(searchTerm);

        if (cleanSearch.isEmpty()) return true;
        if (cleanTarget.contains(cleanSearch)) return true;

        // Allow up to 2 typos/character variations for fuzzy matches
        int maxAllowedTypos = 2;
        if (cleanSearch.length() > 3) {
            int distance = calculateLevenshteinDistance(cleanTarget, cleanSearch);
            // If the search term is short, or close enough in distance, consider it a match
            return distance <= maxAllowedTypos || cleanTarget.toLowerCase().contains(cleanSearch.substring(0, 3));
        }

        return false;
    }
}
