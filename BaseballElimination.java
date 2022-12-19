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
        int teamIndex = findTeam(team);
        if (triviallyEliminated(teamIndex))
            return true;

        FlowNetwork fn = constructNetwork(teamIndex);
        FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);
        for (int i = 1; i < fn.V(); i++) {
            StdOut.println(ff.inCut(i));
            if (ff.inCut(i))
                return true;
        }

        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int teamIndex = findTeam(team);
        if (triviallyEliminated(teamIndex))
            return new Bag<>();

        Bag<String> R = new Bag<>();

        FlowNetwork fn = constructNetwork(teamIndex);
        FordFulkerson ff = new FordFulkerson(fn, 0, fn.V() - 1);
        for (int i = 1; i < fn.V(); i--) {
            if (ff.inCut(i))
                R.add("yes");
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
        Bag<FlowEdge> flowEdges = new Bag<>();
        HashMap<Integer, List<Integer>> mapping = new HashMap<>();
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

        t = v + numberOfTeams() - 1;
        for (int i = 1; i < v; i++) {
            flowEdges.add(new FlowEdge(i, mapping.get(i).get(0) + v, Double.POSITIVE_INFINITY));
            flowEdges.add(new FlowEdge(mapping.get(i).get(0) + v, t, wins[teamIndex] + remaining[teamIndex] -
                    wins[mapping.get(i).get(0)]));
            flowEdges.add(new FlowEdge(i, mapping.get(i).get(1) + v, Double.POSITIVE_INFINITY));
            flowEdges.add(new FlowEdge(mapping.get(i).get(1) + v, t, wins[teamIndex] + remaining[teamIndex] -
                    wins[mapping.get(i).get(1)]));
        }

        FlowNetwork fn = new FlowNetwork(t + 1);
        for (FlowEdge flowEdge : flowEdges) {
            fn.addEdge(flowEdge);
        }

        return fn;
    }

    public static void main(String[] args) {
        BaseballElimination be = new BaseballElimination("teams4.txt");

        for (String team1 : be.teams()) {
            if (be.isEliminated(team1)) {
                StdOut.println(team1);
                for (String t : be.certificateOfElimination(team1))
                    StdOut.println(t);
            }
        }
    }
}
