package com.ch13;


class webPages{

    value[] value;

    public com.ch13.value[] getValue() {
        return value;
    }

    public void setValue(com.ch13.value[] value) {
        this.value = value;
    }

}

class value{

    String name;
    String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

public class Paginas {

    Paginas(){}

    webPages webPages;

    public Paginas(com.ch13.webPages webPages) {
        this.webPages = webPages;
    }
}
