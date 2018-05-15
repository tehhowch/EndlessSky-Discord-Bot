package me.mcofficer.james;

import me.mcofficer.esparser.DataFile;
import me.mcofficer.esparser.DataNode;

import java.util.ArrayList;

public class Lookups {

    final ArrayList<DataFile> dataFiles;

    public Lookups(ArrayList<DataFile> dataFiles) {
        this.dataFiles = dataFiles;
    }

    public DataNode getNodeByString(String query) {
        query = query.toLowerCase();
        int shortest = Integer.MAX_VALUE;
        DataNode match = null;

        // preselect, assuming that the user did not make a typo
        for (DataFile file : dataFiles) {
            for (DataNode node : file.getNodes()) {
                String tokens = String.join(" ", node.getTokens()).trim().toLowerCase();
                query = query.toLowerCase();

                if(tokens.contains(query) && tokens.length() < shortest) {
                    shortest= tokens.length();
                    match = node;
                }
            }
        }
        return match;
    }

    public String getNodeAsText(DataNode node) {
        return printNodeRecursive(node);
    }

    private String printNodeRecursive(DataNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append(String.join(" ", node.getTokens()).trim());
        for(DataNode child : node.getChildren())
            sb.append("\t\t")
                    .append(printNodeRecursive(child).replace("\n", "\n\t"));
        return sb.toString();
    }
}
