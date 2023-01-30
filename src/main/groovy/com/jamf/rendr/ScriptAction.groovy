package com.jamf.rendr

class ScriptAction extends Action {
    String name
    Closure block

    void run() {
        block()
    }

    void validate() {}

    String toString() {
        name
    }
}
