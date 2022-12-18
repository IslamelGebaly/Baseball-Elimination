import edu.princeton.cs.algs4.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        Bag<FlowEdge> flowEdges = new Bag<>();
        HashMap<Integer, List<Integer>> mapping = new HashMap<>();
        int teamIndex = findTeam(team);
        int s = 0;
        int v = 1;
        int t;

        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex)
                continue;
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (j == teamIndex)
                    continue;

                mapping.put(v, Arrays.asList(i, j));
                flowEdges.add(new FlowEdge(s, v++, games[i][j]));
            }
        }

        for (int i = 1; i < v; i++) {
            flowEdges.add(new FlowEdge(i, mapping.get(i).get(0), Double.POSITIVE_INFINITY));
            flowEdges.add(new FlowEdge(i, mapping.get(i).get(1), Double.POSITIVE_INFINITY));
        }

        t = v + numberOfTeams();

        FlowNetwork fn = new FlowNetwork(t);
        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        return new Bag<String>();
    }

    ///find team in array
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
