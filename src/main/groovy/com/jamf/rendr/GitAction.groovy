package com.jamf.rendr

class GitAction extends Action {
    String command

    void run() {
        def p = ['sh', '-c', "git $command"].execute()
        p.waitFor()
    }

    void validate() {}

    String toString() {
        "Run git $command"
    }
}
