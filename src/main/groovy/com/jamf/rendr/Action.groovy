package com.jamf.rendr

abstract class Action {
    abstract void run()

    abstract void validate()

    void toss(String message) {
        throw new RendrException(message)
    }

    String clean(String s) {
        s.replaceAll(/\n/, '\\\\n')
    }
}
