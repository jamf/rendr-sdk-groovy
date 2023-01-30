package com.jamf.rendr

class AppendAction extends Action {
    File file
    String text

    AppendAction to(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.append(text)
    }

    void validate() {
        file.exists() ?: toss("Append failed: file '$file' does not exist")
    }

    String toString() {
        "Append '${clean(text)}' on $file.name"
    }
}
