package com.jamf.rendr

class PrependAction extends Action {
    File file
    String text

    PrependAction to(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.text = text + file.text
    }

    void validate() {
        file.exists() ?: toss("Prepend failed: file '$file' does not exist")
    }

    String toString() {
        "Prepend '${clean(text)}' on $file.name"
    }
}
