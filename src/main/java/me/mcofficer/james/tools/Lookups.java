package me.mcofficer.james.tools;

import me.mcofficer.esparser.DataFile;
import me.mcofficer.esparser.DataNode;

import java.util.ArrayList;

public class Lookups {

    final ArrayList<DataFile> dataFiles;
    ArrayList<String> imagePaths;

    public Lookups(ArrayList<DataFile> dataFiles, ArrayList<String> imagePaths) {
        this.dataFiles = dataFiles;
        this.imagePaths = imagePaths;
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

    public String[] getLookupByString(String query) {
        DataNode node = getNodeByString(query);
        return new String[]{getImageUrl(node), getDescription(node)};
    }

    public String getDescriptionByString(String query) {
        return getDescription(getNodeByString(query));
    }

    private String getDescription(DataNode node) {
        DataNode descNode = getDescriptionChildNode(node);
        return String.join(" ", descNode.getTokens().subList(1, descNode.getTokens().size()));
    }

    public String getImageUrlByString(String query) {
        return getImageUrl(getNodeByString(query));
    }

    private String getImageUrl(DataNode node) {
        DataNode imageNode = getImageChildNode(node);
        String path = String.join(" ", imageNode.getTokens().subList(1, imageNode.getTokens().size()));
        for (String imagePath : imagePaths)
            if (imagePath.contains(path))
                return imagePath;
        return null;
    }

    private DataNode getDescriptionChildNode(DataNode node) {
        for (DataNode child : node.getChildren()) {
            String identifier = child.getTokens().get(0);
            if (identifier.equals("description"))
                return child;
        }
        return null;
    }

    private DataNode getImageChildNode(DataNode node) {
        for (DataNode child : node.getChildren()) {
            String identifier = child.getTokens().get(0);
            if (identifier.equals("sprite") || identifier.equals("thumbnail") || identifier.equals("landscape"))
                return child;
        }
        return null;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public ArrayList<DataFile> getDataFiles() {
        return dataFiles;
    }
}
