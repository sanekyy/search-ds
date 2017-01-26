package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value) {
            this.value = value;
            this.isRed = true;
        }

        Node(boolean color) {
            this.isRed = color;
        }

        E value;
        boolean isRed;
        Node parent;
        Node left;
        Node right;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (parent != null) {
                sb.append(", p=").append(parent);
            }
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            if (isRed == true) {
                sb.append(", c=").append("red");
            } else if (isRed == false) {
                sb.append(", c=").append("black");
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private int size;
    private Node root;
    private Node NIL = new Node(false);
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != NIL) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != NIL) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == NIL) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

//    public int getHeight() {
//        return getHeight(root);
//    } //****/
//
//    private int getHeight(Node node) { //****/
//        if (node == null) return 0;
//        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
//    }

    public Node find(E value) {
        return find(root, value);
    }

    private Node find(Node curr, E val) {
        if (curr == NIL || curr.value.equals(val)) {
            return curr;
        }
        if (curr.value.compareTo(val) > 0) {
            return find(curr.left, val);
        } else {
            return find(curr.right, val);
        }
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != NIL) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        Node z;
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value);
            root.isRed = false;
            root.parent = NIL;
            root.left = NIL;
            root.right = NIL;
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != NIL) {
                        curr = curr.right;
                    } else {
                        curr.right = new Node(value);
                        curr.right.parent = curr;
                        curr.right.left = NIL;
                        curr.right.right = NIL;
                        z = curr.right;
                        break;
                    }
                } else if (cmp > 0) {
                    if (curr.left != NIL) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node(value);
                        curr.left.parent = curr;
                        curr.left.left = NIL;
                        curr.left.right = NIL;
                        z = curr.left;
                        break;
                    }
                }
            }
            AddFixUp(z);
        }
        size++;
        return true;
    }

    public void AddFixUp(Node z) {
        Node y;
        while (z.parent.isRed == true) {
            if (z.parent == z.parent.parent.left) {
                y = z.parent.parent.right;
                if (y.isRed == true) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else if (z == z.parent.right) {
                    z = z.parent;
                    rotateLeft(z);
                }
                else {
                    z.parent.isRed = false;
                    if (compare(z.value, root.value) == 0) {
                        root = z;
                        break;
                    }
                    if (z.parent.parent != NIL) {
                        z.parent.parent.isRed = true;
                        rotateRight(z.parent.parent);
                    }
                }
            } else {
                y = z.parent.parent.left;
                if (y.isRed == true) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else if (z == z.parent.left) {
                    z = z.parent;
                    rotateRight(z);
                }
                else {
                    z.parent.isRed = false;
                    if (compare(z.value, root.value) == 0) {
                        root = z;
                        break;
                    }
                    if (z.parent.parent != NIL) {
                        z.parent.parent.isRed = true;
                        rotateLeft(z.parent.parent);
                    }
                }
            }

        }
        root.isRed = false;
    }

    public void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;

        if (y.left != NIL) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;

        x.parent = y;
    }

    public void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;

        if (y.right != NIL) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.right = x;

        x.parent = y;
    }


    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }
        Node parent = root;
        Node curr = root;
        Node aRemote;
        boolean color;
        int cmp;
        while ((cmp = compare(curr.value, value)) != 0) {
            parent = curr;
            if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
            if (curr == NIL) {
                return false; // ничего не нашли
            }
        }
        if (curr.left != NIL && curr.right != NIL) {
            Node next = curr.right;
            Node pNext = curr;
            while (next.left != NIL) {
                pNext = next;
                next = next.left;
            } //next = наименьший из больших
            curr.value = next.value;
            next.value = null;
            //у правого поддерева нет левых потомков
            color=next.isRed;
            if (pNext == curr) {
                curr.right = next.right;
            } else {
                pNext.left = next.right;
            }
            aRemote=next.right;
            if (aRemote==NIL) {
                aRemote.parent=pNext;
            }
            next.right = NIL;
        } else {
            color=curr.isRed;
            if (curr.left != NIL) {
                reLink(parent, curr, curr.left);
                aRemote=curr.left;
            } else if (curr.right != NIL) {
                reLink(parent, curr, curr.right);
                aRemote=curr.right;
            } else {
                reLink(parent, curr, NIL);
                aRemote=NIL;
            }
            if (aRemote==NIL) {
                aRemote.parent=parent;
            }
        }
        if (color==false) {
            RemoveFixUp(aRemote);
        }
        if (root==NIL) root=null;
        size--;
        return true;
    }

    private void reLink(Node parent, Node curr, Node child) {
        if (parent == curr) {
            root = child;
        } else if (parent.left == curr) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        child.parent=curr.parent;
        curr.value = null;
    }

    public void RemoveFixUp(Node x) {
        Node w;
        while ( x!=root && x.isRed==false) {
            if (x == x.parent.left) {
                w = x.parent.right;
                if (w.isRed == true) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if (w.left.isRed == false && w.right.isRed == false) {
                    w.isRed = true;
                    x = x.parent;
                } else if (w.right.isRed == false) {
                    w.left.isRed = false;
                    w.isRed = true;
                    rotateRight(w);
                    w = x.parent.right;
                } else {
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.right.isRed = false;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                w = x.parent.left;
                if (w.isRed == true) {
                    w.isRed = false;
                    x.parent.isRed = true;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if (w.left.isRed == false && w.right.isRed == false) {
                    w.isRed = true;
                    x = x.parent;
                } else if (w.left.isRed == false) {
                    w.right.isRed = false;
                    w.isRed = true;
                    rotateLeft(w);// *
                    w = x.parent.left;
                } else {
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.left.isRed = false;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.isRed=false;

    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }
}
