package eu.maikeru.treefeller.Utils;

public class Node <Data> {
    Data data;
    Node<Data> next;
    Node<Data> branch;

    public Node(Data data) {
        this.data = data;
        next = null;
        branch = null;
    }
    //public Node
    public Node() {
        next = null;
        branch = null;
    }
}
