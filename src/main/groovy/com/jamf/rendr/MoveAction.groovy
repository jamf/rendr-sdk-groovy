package com.jamf.rendr

class MoveAction extends Action {
    File file
    File target

    MoveAction to(target) {
        this.target = new File(target)
        this
    }

    void run() {
        file.renameTo(target)
    }

    void validate() {
        file.exists() ?: toss("Move failed: file '$file' does not exist")
    }

    String toString() {
        "Move $file to $target"
    }
}
