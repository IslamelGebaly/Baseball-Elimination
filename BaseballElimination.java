import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BaseballElimination {

    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        //reading files
        In in = new In(filename);
        int nTeams = Integer.parseInt(in.readLine());

        //Intializing arrays
        teams = new String[nTeams];
        wins = new int[nTeams];
        losses = new int[nTeams];
        remaining = new int[nTeams];
        games = new int[nTeams][nTeams];
        //assigning values to arrays
        for (int i = 0; i < nTeams; i++) {
            String[] line = in.readLine().split("\\s+");
            teams[i] = line[0];
            wins[i] = Integer.parseInt(line[1]);
            losses[i] = Integer.parseInt(line[2]);
            remaining[i] = Integer.parseInt(line[3]);

            for (int j = 0; j < nTeams; j++) {
                games[i][j] = Integer.parseInt(line[4 + j]);
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    // number of wins for given team
    public int wins(String team) {
        return wins[findTeam(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return losses[findTeam(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return remaining[findTeam(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return games[findTeam(team1)][findTeam(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        return new Bag<String>();
    }

    ///find team in array
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ES_COMPARING_PARAMETER_STRING_WITH_EQ")
    private int findTeam(String team) {
        for (int i = 0; i < numberOfTeams(); i++) {
            if (teams[i].equals(team))
                return i;
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        BaseballElimination be = new BaseballElimination("teams4.txt");

        for (String team1 : be.teams()) {
            StdOut.print(
                    team1 + " "
                            + be.wins(team1) + " "
                            + be.losses(team1) + " "
                            + be.remaining(team1)
            );
            for (String team2 : be.teams)
                StdOut.print(" " + be.against(team1, team2));
            StdOut.print("\n");
        }
    }
}
