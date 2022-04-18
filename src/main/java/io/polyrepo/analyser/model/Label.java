package io.polyrepo.analyser.model;

public class Label {
    private String name;

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        return "{" +
                name +
                '}';
    }
}
