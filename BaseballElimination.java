import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
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
        int teamIndex = findTeam(team);
        if (triviallyEliminated(teamIndex))
            return true;

        FlowNetwork fn = constructNetwork(teamIndex);
        FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);
        for (int i = 1; i < fn.V(); i++) {
            if (ff.inCut(i))
                return true;
        }

        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int teamIndex = findTeam(team);
        int o = 1;
        int[] order = new int[numberOfTeams()];
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex)
                continue;
            order[o++] = i;
        }

        if (triviallyEliminated(teamIndex))
            return new ArrayList<>();

        ArrayList<String> R = new ArrayList<>();

        FlowNetwork fn = constructNetwork(teamIndex);
        FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);
        for (int i = 1; i < numberOfTeams(); i++) {
            if (ff.inCut(i))
                R.add(teams[order[i]]);
        }

        return R;
    }

    ///find team in array
    private int findTeam(String team) {
        for (int i = 0; i < numberOfTeams(); i++) {
            if (teams[i].equals(team))
                return i;
        }
        throw new IllegalArgumentException();
    }

    private boolean triviallyEliminated(int teamIndex) {
        for (int i = 0; i < numberOfTeams(); i++) {
            if (wins[teamIndex] + remaining[teamIndex] < wins[i])
                return true;
        }
        return false;
    }

    private FlowNetwork constructNetwork(int teamIndex) {
        ArrayList<FlowEdge> flowEdges = new ArrayList<>();
        HashMap<Integer, List<Integer>> mapping = new HashMap<>();
        int[] order = new int[numberOfTeams()];
        int o = 1;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex)
                order[i] = -1;
            else
                order[i] = o++;
        }

        int pairings = numberOfTeams();
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex)
                continue;
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (i == j || j == teamIndex)
                    continue;

                mapping.put(pairings, Arrays.asList(i, j));
                flowEdges.add(new FlowEdge(0, pairings++, games[i][j]));
            }
        }

        final int T = pairings;

        for (int i = 0; i < numberOfTeams(); i++) {
            if (i == teamIndex)
                continue;

            flowEdges.add(new FlowEdge(order[i], T, wins[teamIndex] + remaining[teamIndex] - wins[i]));
        }

        int team1, team2;
        for (int pairing = numberOfTeams(); pairing < T; pairing++) {
            team1 = order[mapping.get(pairing).get(0)];
            team2 = order[mapping.get(pairing).get(1)];

            flowEdges.add(new FlowEdge(pairing, team1, Double.POSITIVE_INFINITY));
            flowEdges.add(new FlowEdge(pairing, team2, Double.POSITIVE_INFINITY));
        }

        FlowNetwork fn = new FlowNetwork(T + 1);
        for (FlowEdge fe : flowEdges)
            fn.addEdge(fe);
        return fn;
    }

    public static void main(String[] args) {

    }
}
