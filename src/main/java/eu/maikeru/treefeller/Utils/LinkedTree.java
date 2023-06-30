package eu.maikeru.treefeller.Utils;

import java.util.Objects;

public class LinkedTree <Data> {
    Node<Data> dataNode;
    Node<Data> referencePointer;
   public void add(Data data) {
       dataNode.next = new Node<>(data);
       dataNode = dataNode.next;
   }
   public void setReferencePointer () {
       referencePointer = dataNode;
   }
   public void addBranch(Data data) {
       dataNode.branch = new Node<>(new Node<Data>(data));
       setReferencePointer();
       dataNode = dataNode.branch;
   }
   public void returnToPointer() {
       dataNode = referencePointer;
   }
   public void getData () {return;}
   public LinkedTree(Data init) {
       if (Objects.equals(init, null)) throw new NullPointerException("LinkedTree is passed illegal parameter: null");
       dataNode = new Node<>(init);
   }

}
