package com.example.learnframework;

public class Ha {
    public class InnerHa {

    }
}

class Fa extends Ha.InnerHa {
    public Fa(Ha ha) {
        ha.super();
    }
}
