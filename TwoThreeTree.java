package datastructure;


import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class TwoThreeTree<K extends Comparable<K>> {
    private Node root;

    public static void main(String[] args) {
        TwoThreeTree<Integer> two3Tree = new TwoThreeTree<>();
        two3Tree.insert(2);
        two3Tree.insert(5);
        two3Tree.insert(6);
        two3Tree.insert(9);
        two3Tree.insert(4);
        two3Tree.insert(10);
        two3Tree.insert(1);
        two3Tree.levelWalk(two3Tree.root);
        System.out.println();
        System.out.print(two3Tree.search(7));
    }

    public void levelWalk(Node r) {
        if (null == r) {
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(r);
        while (true) {
            if (queue.isEmpty()) {
                return;
            } else {
                Node x = queue.poll();
                System.out.print(x + "|");
                if (null != x.left) {
                    queue.offer(x.left);
                    queue.offer(x.middle);
                }
                if (null != x.right) {
                    queue.offer(x.right);
                }
            }
        }
    }

    public Node search(K key) {
        Objects.requireNonNull(key);
        return search(root, key);
    }

    private Node search(Node r, K key) {
        if (null == r) {
            return null;
        }

        while (true) {
            int lcmp = key.compareTo(r.lkey);
            if (lcmp == 0) {
                return r;
            }
            if (r.is2Node()) {
                if (r.isLeaf()) {
                    return null;
                } else {
                    r = lcmp < 0 ? r.left : r.middle;
                }
            } else {
                int rcmp = key.compareTo(r.rkey);
                if (rcmp == 0) {
                    return r;
                }
                if (r.isLeaf()) {
                    return null;
                } else {
                    r = lcmp < 0 ? r.left : rcmp < 0 ? r.middle : r.right;
                }
            }
        }

    }

    public void insert(K key) {
        Objects.requireNonNull(key);
        if (null == root) { //       case 1: an empty tree
            root = new Node(key);
            return;
        }
        Node x = root;
        while (true) {
            int lcmp = key.compareTo(x.lkey);
            if (lcmp == 0) {
                throw new IllegalArgumentException("key already exists");
            }
            if (x.is2Node()) {
                if (x.isLeaf()) { // case 2: 2-node insert
                    x.insert2Node(new Node(key));
                    return;
                } else {
                    x = lcmp < 0 ? x.left : x.middle;
                }
            } else {
                int rcmp = key.compareTo(x.rkey);
                if (rcmp == 0) {
                    throw new IllegalArgumentException("key already exists");
                }
                if (x.isLeaf()) { // case 3: 3-node insert
                    x.insert3Node(new Node(key));
                    return;
                } else {
                    x = lcmp < 0 ? x.left : rcmp < 0 ? x.middle : x.right;
                }
            }
        }
    }


    private class Node {
        K lkey;
        K rkey;
        Node left;
        Node middle;
        Node right;
        Node parent;

        Node(K lkey) {
            this.lkey = lkey;
        }

        boolean isLeaf() {
            return null == left;
        }

        boolean is2Node() {
            return null == rkey;
        }

        void insert2Node(Node toInsert) {
            K key = toInsert.lkey;
            int cmp = key.compareTo(lkey);
            if (cmp < 0) { // case 2.1 2-node leftmost
                rkey = lkey;
                lkey = key;
                right = middle;
                middle = toInsert.middle;
                if (null != middle) {
                    middle.parent = this;
                }
                left = toInsert.left;
                if (null != left) {
                    left.parent = this;
                }
            } else { //       case 2.2 2-node rightmost
                rkey = key;
                right = toInsert.middle;
                if (null != right) {
                    right.parent = this;
                }
                middle = toInsert.left;
                if (null != middle) {
                    middle.parent = this;
                }
            }
        }

        void insert3Node(Node toInsert) {
            Node x = split(toInsert), p = parent;
            while (true) {
                if (null == p) {
                    root = x;
                    return;
                } else {
                    if (p.is2Node()) {
                        p.insert2Node(x);
                        return;
                    } else {
                        x = p.split(x);
                        p = p.parent;
                    }
                }
            }
        }

        private Node split(Node toInsert) {
            K key = toInsert.lkey;
            int lcmp = key.compareTo(lkey);
            if (lcmp < 0) { //     case 3.1: 3-node leftmost
                return splitLeftMost(toInsert);
            } else {
                int rcmp = key.compareTo(rkey);
                if (rcmp < 0) { // case 3.2: 3-node middle
                    return splitMiddle(toInsert);
                } else { //        case 3.2: 3-node rightmost
                    return splitRightMost(toInsert);
                }
            }
        }

        private Node splitRightMost(Node toInsert) {
            Node x = new Node(rkey);

            Node le = new Node(lkey);
            le.left = left;
            if (null != left) {
                left.parent = le;
            }
            le.middle = middle;
            if (null != middle) {
                middle.parent = le;
            }
            x.left = le;
            le.parent = x;

            Node mid = toInsert;
            x.middle = mid;
            mid.parent = x;

            return x;
        }

        private Node splitMiddle(Node toInsert) {
            Node x = toInsert;

            Node le = new Node(lkey);
            le.left = left;
            if (null != left) {
                left.parent = le;
            }
            le.middle = x.left;
            x.left.parent = le;
            x.left = le;
            le.parent = x;

            Node mid = new Node(rkey);
            mid.left = x.middle;
            x.middle.parent = mid;
            mid.middle = right;
            if (null != right) {
                right.parent = mid;
            }
            x.middle = mid;
            mid.parent = x;

            return x;
        }

        private Node splitLeftMost(Node toInsert) {
            Node x = new Node(lkey);

            Node le = toInsert;
            x.left = le;
            le.parent = x;

            Node mid = new Node(rkey);
            mid.left = middle;
            if (null != middle) {
                middle.parent = mid;
            }
            mid.middle = right;
            if (null != right) {
                right.parent = mid;
            }
            x.middle = mid;
            mid.parent = x;

            return x;
        }

        @Override
        public String toString() {
            String p;
            if (null == parent) {
                p = "null";
            } else {
                p = "{lkey=" + parent.lkey + ", rkey=" + parent.rkey + "}";
            }
            return "{parent=" + p + ", lkey=" + lkey + ", rkey=" + rkey + "}";
        }
    }
}
