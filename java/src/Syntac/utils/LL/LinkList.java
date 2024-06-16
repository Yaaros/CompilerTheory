package Syntac.utils.LL;

import java.util.LinkedList;
import java.util.function.Consumer;

public class LinkList {
    Symbol head;
    LinkedList<Symbol> lst = new LinkedList<>();
    public LinkList(LinkedList<Symbol> lst) {
        if(lst.size()==0) return;
        head = lst.get(0);
        if(lst.size()>1){
            for (int i = 0; i < lst.size()-1; i++) {
                lst.get(i).setNext(lst.get(i+1));
            }
        }
        this.lst.addAll(lst);
    }

    public boolean contains(Symbol X){
        for (Symbol symbol : lst) {
            if(symbol.equals(X)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        Symbol curr = head;
        while(curr!=null){
            stb.append(curr.symbol);
            curr = curr.getNext();
        }
        return stb.toString();
    }

    public String getStringVer() {
        StringBuilder stb = new StringBuilder();
        Symbol curr = head;
        while(curr!=null){
            stb.append(curr.symbol).append("->");
            curr = curr.getNext();
        }
        stb.delete(stb.length()-2,stb.length());
        return stb.toString();
    }


    public Symbol getHead() {
        return head;
    }

    public void removeFirst(){
        if(isEmpty())return;
        head = head.getNext();
        lst.poll();
    }
    public boolean isEmpty(){
        return head==null||lst.size()==0;
    }

    public void loop(Consumer<Symbol> c){
        Symbol curr = head;
        while(curr!=null){
            c.accept(curr);
            curr = curr.getNext();
        }
    }

    /**pop()
     * 丢出最前面的元素
     * @return 最前面的元素
     */
    public Symbol pop(){
        lst.removeFirst();
        Symbol temp = head;
        head = head.next;
        temp.next = null;
        return temp;
    }

    public int size(){
        int size = 0;
        Symbol curr = head;
        while(curr!=null){
            curr=curr.next;
            size++;
        }
        return size;
    }

}
