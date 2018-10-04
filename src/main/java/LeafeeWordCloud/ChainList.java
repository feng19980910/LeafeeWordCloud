package LeafeeWordCloud;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

class ChainList {
    private class Node {
        public Word item;
        public Node next;
        public Node(Word item, Node next) {
            this.item = new Word(item);
            this.next = next;
        }
    }
    private Node head;

    public ChainList() {
        this.head = null;
    }
    public void insertBack(Word item) {
        if (head == null) {
            head = new Node(item, null);
        }
        else {
            Node temp = head;
            while (temp.next != null)
                temp = temp.next;
            temp.next = new Node(item, null);
        }
    }
    public Word find(String item) {
        Node temp = head;
        while (temp != null && !temp.item.equals(item))
            temp = temp.next;
        return temp == null ? null : temp.item;
    }
    public boolean remove(String str) {
        // if empty chainList;
        if (head == null)
            return false;

        // if the first node;
        if (head.item.equals(str)) {
            head = head.next;
            return true;
        }
        else {
            // find the Node which before the destination
            // and delete by using Node.next
            // if it is the last node, it will be also judged by using temp.next.item
            Node temp = head;
            while (temp.next != null && !temp.next.item.equals(str))
                temp = temp.next;
            if (temp.next == null)
                return false;
            else {
                // Leafee: always want to delete something
                temp.next = temp.next.next;
                return true;
            }
        }
    }
    public List<Word> toList() {
        if (head == null)
            return null;
        ArrayList<Word> arrList = new ArrayList<Word>();
        for (Node i = head; i != null; i = i.next)
            arrList.add(i.item);

        // // for some reason, the sort doesn't work
        // arrList.sort(new Comparator<Word>() {
        //     public int compare(Word o1, Word o2) {
        //         return o1.getString().compareTo(o2.getString());
        //     }
        // });

        return arrList;
    }
}

