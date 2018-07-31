package me.mcofficer.james.tools;

import me.mcofficer.esparser.DataFile;
import me.mcofficer.esparser.DataNode;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Lookups {

    final ArrayList<DataFile> dataFiles;
    ArrayList<String> imagePaths;

    public Lookups(ArrayList<DataFile> dataFiles, ArrayList<String> imagePaths) {
        this.dataFiles = dataFiles;
        this.imagePaths = imagePaths;
    }

    public List<DataNode> getNodesByString(String query) {
        query = query.toLowerCase();
        ArrayList<DataNode> matches = new ArrayList<>();

        // preselect, assuming that the user did not make a typo
        for (DataFile file : dataFiles) {
            for (DataNode node : file.getNodes()) {
                String tokens = String.join(" ", node.getTokens()).trim().toLowerCase();
                query = query.toLowerCase();

                if(tokens.contains(query))
                    matches.add(node);
            }
        }

        // Never return more than 10 results
        if (matches.size() > 10)
            return matches.subList(0, 10);

        return matches;
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

    public String[] getLookupByNode(DataNode node) {
        return new String[]{getImageUrl(node), getDescription(node)};
    }

    private String getDescription(DataNode node) {
        DataNode descNode = getDescriptionChildNode(node);
        if (descNode == null)
            return null;
        return String.join(" ", descNode.getTokens().subList(1, descNode.getTokens().size()));
    }

    public String getImageUrl(DataNode node) {
        DataNode imageNode = getImageChildNode(node);
        if (imageNode == null)
            return null;
        String path = String.join(" ", imageNode.getTokens().subList(1, imageNode.getTokens().size()));
        try {
            for (String imagePath : imagePaths)
                if (java.net.URLDecoder.decode(imagePath, StandardCharsets.UTF_8.name()).contains(path))
                    return imagePath;
        }
        catch (UnsupportedEncodingException e) { //Should never happen since UTF-8 is from StandardCharsets
            e.printStackTrace();
        }
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
