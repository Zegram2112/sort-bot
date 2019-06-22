package sortbot.ui;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;

public class Node implements Drawable {

    private String name;
    private Node parent;
    private Map<String, Node> children;    

    public Node() {
        children = new LinkedHashMap<String, Node>();
        parent = null;
        name = "root";
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public void setChild(String name, Node child) {
        child.parent = this;
        child.name = name;
        children.put(name, child);
    }

    public void removeChild(String name) {
        children.remove(name); 
    }

    public Collection<Node> getChildren() {
        return children.values();
    }

    public void destroy() {
        parent.removeChild(name);
    }

    public void draw(int x, int y, int w, int h) {     
        for (Node child : getChildren()) {
            child.draw(x, y, w, h);
        }
    }

}
