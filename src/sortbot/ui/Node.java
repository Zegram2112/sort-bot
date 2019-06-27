package sortbot.ui;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Node implements Drawable, InputHandler {

    private String name;
    private Node parent;
    private List<Node> children;

    public Node() {
        children = new ArrayList<Node>();
        parent = null;
        name = "root";
    }

    public Node getChild(String name) {
        int index = getChildIndex(name);
        if (index != -1) {
            return children.get(index);
        }
        return null;
    }

    public void addChild(String name, Node child) {
        child.parent = this;
        child.name = name;
        children.add(child);
    }

    public void removeChild(String name) {
        int index = getChildIndex(name);
        if (index != -1) {
            children.remove(index);
        }
    }

    private int getChildIndex(String name) {
        for (int i = 0; i < ((ArrayList)children).size(); ++i) {
            if (children.get(i).name == name) {
                return i;
            }
        }
        return -1;
    }

    public Collection<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public void destroy() {
        if (parent == null) {
            NodeEngine.removeActiveNode();
        }
        parent.removeChild(name);
    }

    @Override
    public void draw(int x, int y, int w, int h) {     
        for (Node child : getChildren()) {
            child.draw(x, y, w, h);
        }
    }

    @Override
    public void handleInput(int input) {
        for (Node child : getChildren()) {
            child.handleInput(input);
        }
    }

}
